(ns clojure-caju-process.infrastructure.repository.transactions.transactions-repository-imp
  (:require [clojure-caju-process.domain.transactions.transactions-repository :as repository]
            [clojure-caju-process.infrastructure.database.driver :as database]
            [schema.core :as s]
            [clojure-caju-process.domain.transactions.transactions :as transactions]))

(def table :transactions)

(s/defn ^:private cents->double :- s/Num
  [cents :- s/Int]
  (double (/ cents 100)))

(s/defn ^:private double->cents :- s/Int
  [double :- s/Num]
  (int (* double 100)))

(s/defn ^:private ->entity :- s/Any
  [{:keys [id account amount merchant-category merchant-name type]} :- transactions/Transaction]
  {:id id
   :account account
   :amount (double->cents amount)
   :merchant_category (name merchant-category)
   :merchant_name merchant-name
   :type (name type)})

(s/defn ^:private ->transaction :- transactions/Transaction
  [#:transactions{:keys [id account amount merchant_category merchant_name type created_at]}]
  {:id id
   :account account
   :amount (cents->double amount)
   :merchant-category (keyword merchant_category)
   :merchant-name merchant_name
   :type (keyword type)
   :created-at (.toString created_at)})

(s/defrecord TransactionsRepositoryImp
  [database-driver]
  
  repository/TransactionsRepository
  (create! [_this {conn :conn} transaction]
   (->> transaction
        (->entity)
        (database/insert! database-driver {:conn conn} table)
        (->transaction)))
  
  (get-by-id [_this id]
    (some-> (database/get-by database-driver {} table :id id)
            first
            ->transaction)))

(defn new
  []
  (map->TransactionsRepositoryImp {}))