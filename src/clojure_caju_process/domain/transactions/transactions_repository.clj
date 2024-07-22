(ns clojure-caju-process.domain.transactions.transactions-repository
  (:require [clojure-caju-process.domain.transactions.transactions :as transactions]
            [schema.core :as s]))

(s/defprotocol TransactionsRepository
  (create! :- transactions/Transaction [this opts transaction :- transactions/Transaction])
  (get-by-id :- (s/maybe transactions/Transaction) [this id :- s/Str]))
