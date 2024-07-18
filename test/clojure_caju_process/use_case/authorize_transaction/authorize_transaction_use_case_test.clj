(ns clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case-test
  (:require [clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case :as authorize-transaction-use-case]
            [clojure-caju-process.mocks.merchants-repository-mock :as mr-mock]
            [clojure-caju-process.mocks.accounts-repository-mock :as acr-mock]
            [clojure-caju-process.use-case.use-case :as use-case]
            [schema.test :as s]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]))

(s/deftest test-transaction-create
  (testing "Test transaction create"
    (let [mocked-merchant-info {:name "teste" :merchant-category :food}
          mocked-merchant-repo (mr-mock/mock-repository :get-by-name-fn (fn [_] mocked-merchant-info))
          mocked-account-info {:id "123" :balance {:food 100 :meal 100 :cash 100}}
          mocked-account-repo (acr-mock/mock-repository :get-by-id-fn (fn [_] mocked-account-info))
          authorize-transaction-use-case (authorize-transaction-use-case/new)
          system (component/start (component/system-map
                                   :accounts-repository (component/using mocked-account-repo [])
                                   :merchants-repository (component/using mocked-merchant-repo [])
                                   :transaction_usecase_create (component/using authorize-transaction-use-case
                                                                                [:accounts-repository :merchants-repository])))
          params {:account "123", :merchant-name "teste", :mcc "123", :total-amount 150}]
      (is (= (use-case/execute (:transaction_usecase_create system) params) true)))))
