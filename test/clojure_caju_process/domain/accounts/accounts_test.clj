(ns clojure-caju-process.domain.accounts.accounts-test
  (:require [clojure.test :refer :all]
            [clojure-caju-process.domain.accounts.accounts :as acc]
            [schema.core :as s]
            [schema.test :as st]))

(s/def account :- acc/Account
  {:id "123"
   :balance {:food 100
             :meal 100
             :cash 200}})

(st/deftest test-debit
  (testing "When sufficient balance"
    (testing "return account with debited balance"
      (is (= (acc/debit account :food 50)
             (update-in account [:balance :food] - 50)))
      (is (= (acc/debit account :meal 50)
             (update-in account [:balance :meal] - 50)))
      (is (= (acc/debit account :cash 50)
             (update-in account [:balance :cash] - 50)))))

  (testing "When unsufficient balance for the category but sufficient in cash"
    (testing "return account with debited cash balance"
      (is (= (acc/debit account :food 150)
             (update-in account [:balance :cash] - 150)))
      (is (= (acc/debit account :meal 150)
             (update-in account [:balance :cash] - 150)))
      (is (= (acc/debit account :cash 150)
             (update-in account [:balance :cash] - 150)))))

  (testing "When unsufficient balance either for category or in cash"
    (testing "return error of unsufficient funds"
      (is (= (acc/debit account :food 300)
             {:error :insufficient-funds}))
      (is (= (acc/debit account :meal 300)
             {:error :insufficient-funds}))
      (is (= (acc/debit account :cash 300)
             {:error :insufficient-funds})))))
