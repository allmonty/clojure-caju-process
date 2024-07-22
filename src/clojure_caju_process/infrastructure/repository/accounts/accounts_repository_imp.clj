(ns clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as repository]
            [clojure-caju-process.infrastructure.database.driver :as database]
            [schema.core :as s]
            [clojure-caju-process.domain.accounts.accounts :as accounts]))

(def table :accounts)

(s/defn ^:private cents->double :- s/Num
  [cents :- s/Int]
  (double (/ cents 100)))

(s/defn ^:private double->cents :- s/Int
  [double :- s/Num]
  (int (* double 100)))

(s/defn ^:private ->entity :- s/Any
  [{id :id {:keys [food meal cash]} :balance} :- accounts/Account]
  {:id id
   :balance_food (double->cents food)
   :balance_meal (double->cents meal)
   :balance_cash (double->cents cash)})

(s/defn ^:private ->account :- accounts/Account
  [#:accounts{:keys [id balance_food balance_meal balance_cash]}]
  {:id id
   :balance {:food (cents->double balance_food)
             :meal (cents->double balance_meal)
             :cash (cents->double balance_cash)}})

(s/defrecord AccountsRepositoryImp
  [database-driver]
  
  repository/AccountsRepository
  (create! [_ account]
   (->> account
        (->entity)
        (database/insert! database-driver {} table)
        (->account)))
  
  (get-by-id [_ id]
    (some-> (database/get-by database-driver {} table :id id)
            first
            ->account))
  
  (update-balance! [_ {conn :conn} account]
    (let [{:keys [balance_food balance_meal balance_cash]} (->entity account)]
      (some->> (database/update! database-driver
                                 {:conn conn} table (:id account)
                                 {:balance_food balance_food
                                  :balance_meal balance_meal
                                  :balance_cash balance_cash})
               (->account))))
  
  (consistent-update! [_ id function]
    (database/with-lock-update! database-driver table id
      (fn [account-entity tx]
        (some-> account-entity
                ->account
                (function tx))))))

(defn new
  []
  (map->AccountsRepositoryImp {}))