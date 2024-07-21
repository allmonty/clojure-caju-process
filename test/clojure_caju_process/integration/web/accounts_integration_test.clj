(ns clojure-caju-process.integration.web.accounts-integration-test
  (:require [clj-http.client :as http]
            [clojure-caju-process.system :as system]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [cheshire.core :refer [parse-string]]
            [schema.test :as s]))

(def test-system (atom nil))

(defn with-test-db
  [f]
  (reset! test-system (system/start))
  (f)
  (jdbc/execute! (-> @test-system :database-driver :database) ["DELETE FROM accounts;"])
  (component/stop @test-system))

(use-fixtures :once with-test-db)

(s/deftest ^:integration test-integration-accounts-api
  (testing "Happy path:"
    (testing "POST /accounts creates a new account"
      (let [request-body {:id "123" :balance {:food 100 :meal 200 :cash 300}}
            response (http/post "http://localhost:3000/accounts"
                                {:accept :json
                                 :content-type :json
                                 :form-params request-body})]
        (is (= 200 (:status response)))
        (is (= request-body (-> response :body (parse-string true))))))
    
    (testing "GET /accounts/:id returns the account"
      (let [request-body {:id "123" :balance {:food 100 :meal 200 :cash 300}}
            response (http/get "http://localhost:3000/accounts/123" {:accept :json})]
        (is (= 200 (:status response)))
        (is (= request-body (-> response :body (parse-string true)))))))
  
  (testing "Edge cases:"
    (testing "GET /accounts/:id that doesn't exists, return 404"
      (let [response (http/get "http://localhost:3000/accounts/000" {:throw-exceptions false})]
        (is (= 404 (:status response)))
        (is (= "" (:body response)))))))
