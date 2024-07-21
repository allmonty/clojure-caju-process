(ns clojure-caju-process.integration.web.debits-integration-test
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as http]
            [clojure-caju-process.integration.test-helper :as th]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [schema.test :as s]) 
  (:import [java.util UUID]))

(defn with-system
  [f]
  (th/start-system)
  (f)
  (jdbc/execute! (-> @th/system :database-driver :database)
                 ["DELETE FROM transactions; DELETE FROM accounts; DELETE FROM merchants;"])
  (component/stop @th/system))

(use-fixtures :once with-system)

(defn create-account
  [account]
  (http/post "http://localhost:3000/accounts"
             {:accept :json
              :content-type :json
              :form-params account}))

(defn get-account
  [id]
  (http/get (str "http://localhost:3000/accounts/" id)
            {:accept :json
             :content-type :json}))

(defn create-merchant
  [merchant]
  (http/post "http://localhost:3000/merchants"
             {:accept :json
              :content-type :json
              :form-params merchant}))

(defn make-debit
  [debit]
  (http/post "http://localhost:3000/debits"
             {:accept :json
              :content-type :json
              :form-params debit}))

(s/deftest ^:integration test-integration-debits-api
  (testing "Simple debit authorizer with fallback (L1 and L2. using only mcc info):"
    (let [account-id "111"
          account {:id account-id :balance {:food 100 :meal 200 :cash 300}}
          expected-account-1 (assoc-in account [:balance :food] 50)
          expected-account-2 (assoc-in expected-account-1 [:balance :meal] 150)
          expected-account-3 (assoc-in expected-account-2 [:balance :cash] 250)
          expected-account-4 (assoc-in expected-account-2 [:balance :cash] 150)
          _ (create-account account)]

      (testing "Debit from food balance"
        (let [request {:id "trans:1", :account-id account-id, :merchant-name "comida bar", :mcc "5412", :amount 50}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-1 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Debit from meal balance"
        (let [request {:id "trans:2", :account-id account-id, :merchant-name "comida bar", :mcc "5812", :amount 50}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-2 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Debit from cash balance when unknown mcc"
        (let [request {:id "trans:3", :account-id account-id, :merchant-name "comida bar", :mcc "0000", :amount 50}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-3 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Debit from food balance without enough funds, will debit from cash"
        (let [request {:id "trans:4", :account-id account-id, :merchant-name "comida bar", :mcc "5412", :amount 100}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-4 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Not enough funds in meal or cash, will return rejected and not debit"
        (let [request {:id "trans:5", :account-id account-id, :merchant-name "comida bar", :mcc "5812", :amount 999}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-4 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "51"} (-> response :body (parse-string true))))))
      
      (testing "Return code 07 in case of any error"
        (let [request {:id "trans:6", :account-id "000", :merchant-name "comida bar", :mcc "5812", :amount 999}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-4 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "07"} (-> response :body (parse-string true))))))))
  
  (testing "Debit authorizer if merchant info:"
    (let [account-id "112"
          account {:id account-id :balance {:food 100 :meal 200 :cash 300}}
          merchant {:id (str (UUID/randomUUID)) :name "comida restaurante" :merchant-category "meal"}
          expected-account-1 (assoc-in account [:balance :meal] 150)
          expected-account-2 (assoc-in expected-account-1 [:balance :meal] 100)
          expected-account-3 (assoc-in expected-account-2 [:balance :cash] 100)
          _ (create-merchant merchant)
          _ (create-account account)]
      
      (testing "Debit with mcc of food but merchant info overwrites to meal"
        (let [request {:id "trans:7", :account-id account-id, :merchant-name (:name merchant), :mcc "5412", :amount 50}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-1 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Debit with unknown mcc but merchant info overwrites to meal"
        (let [request {:id "trans:8", :account-id account-id, :merchant-name (:name merchant), :mcc "000", :amount 50}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-2 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Debit with not enough funds in meal, merchant info overwrites to meal, but debit from cash"
        (let [request {:id "trans:9", :account-id account-id, :merchant-name (:name merchant), :mcc "5412", :amount 200}
              response (make-debit request)
              account-response (get-account account-id)]
          (is (= expected-account-3 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true)))))))))
