(ns clojure-caju-process.infrastructure.repository.transactions.transactions-repository-imp
  (:require [clojure-caju-process.domain.transactions.transactions-repository :as repository]
            [clojure-caju-process.infrastructure.database.driver :as database]
            [schema.core :as s]
            [clojure-caju-process.domain.transactions.transactions :as transactions]))

(def table :transactions)

(s/defn ^:private ->entity :- s/Any
  [{:keys [id account amount merchant-category merchant-name]} :- transactions/Transaction]
  {:id id
   :account account
   :amount amount
   :merchant_category (name merchant-category)
   :merchant_name merchant-name})

(s/defn ^:private ->transaction :- transactions/Transaction
  [#:transactions{:keys [id account amount merchant_category merchant_name]}]
  {:id id
   :account account
   :amount amount
   :merchant-category (keyword merchant_category)
   :merchant-name merchant_name})

(s/defrecord TransactionsRepositoryImp
  [database-driver]
  
  repository/TransactionsRepository
  (create! [_this transaction]
   (->> transaction
        (->entity)
        (database/insert! database-driver table)
        (->transaction)))
  
  (get-by-id [_this id]
    (some-> (database/get-by database-driver table :id id)
            first
            ->transaction)))

(defn new
  []
  (map->TransactionsRepositoryImp {}))