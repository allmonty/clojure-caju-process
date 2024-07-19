(ns clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as repository]
            [clojure-caju-process.infrastructure.database.driver :as database]
            [schema.core :as s]
            [clojure-caju-process.domain.accounts.accounts :as accounts]))

(def table :accounts)

(s/defn ^:private ->entity :- s/Any
  [{id :id {:keys [food meal cash]} :balance} :- accounts/Account]
  {:id id
   :balance_food food
   :balance_meal meal
   :balance_cash cash})

(s/defn ^:private ->account :- accounts/Account
  [#:accounts{:keys [id balance_food balance_meal balance_cash]}]
  {:id id
   :balance {:food balance_food
             :meal balance_meal
             :cash balance_cash}})

(s/defrecord AccountsRepositoryImp
  [database-driver]
  
  repository/AccountsRepository
  (create [_this account]
   (->> account
        (->entity)
        (database/insert! database-driver table)
        (->account)))
  
  (get-by-id [_this id]
    (some-> (database/get-by database-driver table :id id)
            first
            ->account)))

(defn new
  []
  (map->AccountsRepositoryImp {}))