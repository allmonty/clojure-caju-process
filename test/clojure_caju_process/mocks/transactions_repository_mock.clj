(ns clojure-caju-process.mocks.transactions-repository-mock
  (:require [clojure-caju-process.domain.transactions.transactions-repository :as tra-repo]))

(defrecord MockTransactionsRepository
           [mocked-functions]
  tra-repo/TransactionsRepository
  (get-by-id [_ id]
    ((:get-by-id mocked-functions) id))
  (create! [_ opts account]
    ((:create mocked-functions) opts account)))

(defn mock-repository
  "Returns a mock account repository"
  [& {:keys [get-by-id-fn create-fn]}]
  (->MockTransactionsRepository {:get-by-id get-by-id-fn
                                 :create create-fn}))