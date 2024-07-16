(ns clojure-caju-process.infrastructure.repository.merchant.merchant-repository-imp
  (:require [clojure-caju-process.domain.merchant.merchant-repository :as repository]
            [schema.core :as s]))

(s/defrecord MerchantRepositoryImp
  [database_driver]
  
  repository/MerchantRepository
  (get-by-name [_this name]
    (print "Getting by name " name)))

(defn new
  []
  (map->MerchantRepositoryImp {}))