(ns clojure-caju-process.use-case.transactions.create 
  (:require [clojure-caju-process.domain.merchant.merchant-repository :as MerchantRepository]
            [clojure-caju-process.use-case.use-case :refer [UseCase]]
            [schema.core :as s]))

(s/defrecord TransactionCreate
  [merchant-repository]
  
  UseCase
  (execute [_this params]
    ;; Validate the params (transaction infos)
    ;; get the information about MCC -> Category
    ;; get information about the transaction merchant -> Category
    ;; Define the transaction category
    ;; get information about the account and balance
    ;; authorize transaction
    ;; save account with new balance
    ;; save transaction

    (print (MerchantRepository/get-by-name merchant-repository "test"))
    
    (str "Creating transaction " params)))

(defn new
  []
  (map->TransactionCreate {}))