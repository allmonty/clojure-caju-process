(ns clojure-caju-process.use-case.transaction.create 
  (:require [clojure-caju-process.use-case.use-case :refer [UseCase]]))

(defrecord TransactionCreate
  []
  
  UseCase
  (execute [_this params]
    (str "Creating transaction " params)))

(defn new
  []
  (map->TransactionCreate {}))