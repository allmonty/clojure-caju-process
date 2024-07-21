(ns clojure-caju-process.use-case.debits.create-debit-usecase
  (:require [clojure-caju-process.domain.accounts.accounts :as acc]
            [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            [clojure-caju-process.domain.merchants.merchants-repository :as mer-repo]
            [clojure-caju-process.domain.transactions.transactions-repository :as tra-repo]
            [clojure-caju-process.domain.merchant-categories.merchant-categories :as mc]
            [clojure-caju-process.domain.transactions.transactions :as transactions]
            [clojure-caju-process.use-case.debits.create-debit-usecase-schema :as schema]
            [clojure-caju-process.use-case.use-case :refer [UseCase]]
            [schema.core :as s]))

(s/defn ^:pprivate ->transaction :- transactions/Transaction
  [id account-id merchant-name category amount]
  {:id id
   :account account-id
   :merchant-name merchant-name
   :merchant-category category
   :amount amount})

(s/defrecord CreateDebit
             [accounts-repository merchants-repository transactions-repository]
  UseCase
  (execute
    [_this params]
    (let [{:keys [id account-id merchant-name mcc amount]} (s/validate schema/Input params)
          account-info  (acc-repo/get-by-id accounts-repository account-id)
          merchant-info (mer-repo/get-by-name merchants-repository merchant-name)
          category      (mc/get-category mcc (:merchant-category merchant-info))
          debit-result  (acc/debit account-info category amount)
          new-transaction (->transaction id account-id merchant-name category amount)]
      (case debit-result
        {:error :insufficient-funds}
        :insufficient-funds

        (do
          (tra-repo/create! transactions-repository new-transaction)
          (acc-repo/update-balance! accounts-repository debit-result)
          :debit-successful)))))

(defn new
  []
  (map->CreateDebit {}))