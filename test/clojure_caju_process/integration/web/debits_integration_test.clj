(ns clojure-caju-process.integration.web.debits-integration-test
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as http]
            [clojure-caju-process.integration.test-helper :as th]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [schema.test :as s]
            [clojure-caju-process.use-case.accounts.create-account-usecase :as create-account-usecase]) 
  (:import [java.util UUID]))

(defn with-system
  [f]
  (th/start-system)
  (f)
  (jdbc/execute! (-> @th/system :database-driver :database) ["DELETE FROM transactions; DELETE FROM accounts;"])
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

(defn make-debit
  [debit]
  (http/post "http://localhost:3000/debits"
             {:accept :json
              :content-type :json
              :form-params debit}))

(s/deftest ^:integration test-integration-merchants-api
  (testing "Simple authorizer with fallback (L1 and L2. using only mcc info):"
    (let [account {:id "123" :balance {:food 100 :meal 200 :cash 300}}
          expected-account-1 (assoc-in account [:balance :food] 50)
          expected-account-2 (assoc-in expected-account-1 [:balance :cash] 200)
          _ (create-account account)]
      (testing "Debit from food balance"
        (let [request {:id "trans:1", :account-id "123", :merchant-name "comida bar", :mcc "5412", :amount 50}
              response (make-debit request)
              account-response (get-account "123")]
          (is (= expected-account-1 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Debit from food balance without enough funds, will debit from cash"
        (let [request {:id "trans:2", :account-id "123", :merchant-name "comida bar", :mcc "5412", :amount 100}
              response (make-debit request)
              account-response (get-account "123")]
          (is (= expected-account-2 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "00"} (-> response :body (parse-string true))))))
      
      (testing "Not enough funds in meal or cash, will return rejected and not debit"
        (let [request {:id "trans:3", :account-id "123", :merchant-name "comida bar", :mcc "5812", :amount 999}
              response (make-debit request)
              account-response (get-account "123")]
          (is (= expected-account-2 (-> account-response :body (parse-string true))))
          (is (= 200 (:status response)))
          (is (= {:code "51"} (-> response :body (parse-string true))))))
      
      (testing "Return code 07 in case of any error"
        (let [request {:id "trans:4", :account-id "000", :merchant-name "comida bar", :mcc "5812", :amount 999}
              response (make-debit request)]
          (is (= 200 (:status response)))
          (is (= {:code "07"} (-> response :body (parse-string true)))))))))
