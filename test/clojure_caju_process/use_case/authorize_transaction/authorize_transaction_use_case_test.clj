(ns clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case-test
  (:require [clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case :as authorize-transaction-use-case]
            [clojure-caju-process.mocks.accounts-repository-mock :as acr-mock]
            [clojure-caju-process.mocks.merchants-repository-mock :as mr-mock]
            [clojure-caju-process.mocks.transactions-repository-mock :as tr-mock]
            [clojure-caju-process.use-case.use-case :as use-case]
            [schema.test :as s]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]))

(s/deftest test-transaction-create
  (testing "Test transaction create"
    (let [mocked-merchant-info {:id "123" :name "teste" :merchant-category :food}
          mocked-merchants-repo (mr-mock/mock-repository :get-by-name-fn (fn [_] mocked-merchant-info)) 
          mocked-account-info {:id "123" :balance {:food 100 :meal 100 :cash 100}}
          mocked-accounts-repo (acr-mock/mock-repository :get-by-id-fn (fn [_] mocked-account-info)
                                                         :save-fn identity)
          mocked-transactions-repo (tr-mock/mock-repository :create-fn identity)
          authorize-transaction-use-case (authorize-transaction-use-case/new)
          system (component/start (component/system-map
                                   :accounts-repository (component/using mocked-accounts-repo [])
                                   :merchants-repository (component/using mocked-merchants-repo [])
                                   :transactions-repository (component/using mocked-transactions-repo [])
                                   :transaction_usecase_create (component/using authorize-transaction-use-case
                                                                                [:accounts-repository :merchants-repository :transactions-repository])))
          params {:id "321" :account "123", :merchant-name "teste", :mcc "123", :amount 50}]
      (is (= (use-case/execute (:transaction_usecase_create system) params) true)))))
