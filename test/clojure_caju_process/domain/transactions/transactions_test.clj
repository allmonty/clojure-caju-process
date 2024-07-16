(ns clojure-caju-process.domain.transactions.transactions-test
  (:require [clojure.test :refer :all]
            [clojure-caju-process.domain.transactions.transactions :as t]
            [schema.test :as s]))

(s/deftest ^:integration test-get-transaction-category
  (testing "known mcc codes to categories"
    (is (= (t/get-transaction-category "5411") :food))
    (is (= (t/get-transaction-category "5412") :food))
    (is (= (t/get-transaction-category "5811") :meal))
    (is (= (t/get-transaction-category "5811") :meal)))

  (testing "unmapped mcc codes will return :cash"
    (is (= (t/get-transaction-category "123") :cash))
    (is (= (t/get-transaction-category "teste") :cash))))

(s/deftest test-authorize
  (testing "main route"
    (is (= (t/authorize 10) true))))
