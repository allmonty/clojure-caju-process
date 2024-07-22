(ns clojure-caju-process.infrastructure.repository.merchants.merchants-repository-imp
  (:require [clojure-caju-process.domain.merchants.merchants-repository :as repository]
            [clojure-caju-process.infrastructure.database.driver :as database]
            [schema.core :as s]
            [clojure-caju-process.domain.merchants.merchants :as merchants]))

(def table :merchants)

(s/defn ^:private ->entity :- s/Any
  [merchant :- merchants/Merchant]
  {:id (-> merchant :id)
   :name (-> merchant :name)
   :merchant_category (-> merchant :merchant-category name)})

(s/defn ^:private ->merchant :- merchants/Merchant
  [#:merchants{:keys [id name merchant_category]}]
  {:id id
   :name name
   :merchant-category (keyword merchant_category)})

(s/defrecord MerchantsRepositoryImp
  [database-driver]
  
  repository/MerchantsRepository
  (create! [_this merchant]
   (->> merchant
        (->entity)
        (database/insert! database-driver {} table)
        (->merchant)))
  
  (get-by-name [_this name]
    (some-> (database/get-by database-driver {} table :name name)
            first
            ->merchant)))

(defn new
  []
  (map->MerchantsRepositoryImp {}))