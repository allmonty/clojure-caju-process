(ns clojure-caju-process.integration.web.merchants-integration-test
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
  (jdbc/execute! (-> @th/system :database-driver :database) ["DELETE FROM merchants;"])
  (component/stop @th/system))

(use-fixtures :once with-system)

(s/deftest ^:integration test-integration-merchants-api
  (testing "Happy path:"
    (let [new-merchant {:id (str (UUID/randomUUID)) :name "nometeste" :merchant-category "food"}]
      (testing "POST /merchants creates a new merchant"
        (let [response (http/post "http://localhost:3000/merchants"
                                  {:accept :json
                                   :content-type :json
                                   :form-params new-merchant})]
          (is (= 200 (:status response)))
          (is (= new-merchant (-> response :body (parse-string true))))))

      (testing "GET /merchants/:id returns the merchant"
        ;; TODO: create a get with id instead of name because name has blank spaces
        (let [response (http/get "http://localhost:3000/merchants/nometeste" {:accept :json})]
          (is (= 200 (:status response)))
          (is (= new-merchant (-> response :body (parse-string true))))))))
  
  (testing "Edge cases:"
    (testing "GET /merchants/:id that doesn't exists, return 404"
      (let [response (http/get "http://localhost:3000/merchants/000" {:throw-exceptions false})]
        (is (= 404 (:status response)))
        (is (= "" (:body response)))))))
