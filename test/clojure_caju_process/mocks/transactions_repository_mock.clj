(ns clojure-caju-process.mocks.transactions-repository-mock 
  (:require [clojure-caju-process.domain.transactions.transactions-repository :as tra-repo]))

(defrecord MockTransactionsRepository
  [mocked-functions]
  tra-repo/TransactionsRepository
  (create [_ account]
   ((:create mocked-functions) account)))

(defn mock-repository
  "Returns a mock account repository"
  [& {:keys [create-fn]}]
  (->MockTransactionsRepository {:create create-fn}))