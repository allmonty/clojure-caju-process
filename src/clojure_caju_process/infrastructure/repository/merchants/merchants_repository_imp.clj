(ns clojure-caju-process.infrastructure.repository.merchants.merchants-repository-imp
  (:require [clojure-caju-process.domain.merchants.merchants-repository :as repository]
            [schema.core :as s]))

(s/defrecord MerchantsRepositoryImp
  [database_driver]
  
  repository/MerchantsRepository
  (get-by-name [_this name]
    (print "Getting by name " name)))

(defn new
  []
  (map->MerchantsRepositoryImp {}))