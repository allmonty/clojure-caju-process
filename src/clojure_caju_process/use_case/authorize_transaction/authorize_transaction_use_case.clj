(ns clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case
  (:require [clojure-caju-process.domain.accounts.accounts :as acc]
            [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            [clojure-caju-process.domain.merchants.merchants-repository :as mer-repo]
            [clojure-caju-process.domain.transactions.transactions-repository :as tra-repo]
            [clojure-caju-process.domain.merchant-categories.merchant-categories :as mc]
            [clojure-caju-process.use-case.authorize-transaction.authorize-transaction-schema :as schema]
            [clojure-caju-process.use-case.use-case :refer [UseCase]]
            ;; [clojure.tools.logging :as logging]
            [schema.core :as s]))

(s/defrecord TransactionCreate
             [accounts-repository merchants-repository transactions-repository]
  UseCase
  (execute
    [_this params]
    (let [{:keys [id account merchant-name mcc amount]} (s/validate schema/Input params)
          merchant-info (mer-repo/get-by-name merchants-repository merchant-name)
          account-info  (acc-repo/get-by-id accounts-repository account)
          category      (mc/get-category mcc (:merchant-category merchant-info))
          debit-result  (acc/debit account-info category amount)
          new-transaction {:id id :account account :merchant-name merchant-name :merchant-category category :amount amount}]
      
      (case debit-result
        {:error :insufficient-funds}
        (println debit-result)
        
        (do
          (tra-repo/create transactions-repository new-transaction)
          (acc-repo/save accounts-repository debit-result))))))

(defn new
  []
  (map->TransactionCreate {}))