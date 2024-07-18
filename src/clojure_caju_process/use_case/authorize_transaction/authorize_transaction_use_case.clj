(ns clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            [clojure-caju-process.domain.merchants.merchants-repository :as mer-repo]
            [clojure-caju-process.domain.merchant-categories.merchant-categories :as mc]
            [clojure-caju-process.use-case.authorize-transaction.authorize-transaction-schema :as schema]
            [clojure-caju-process.use-case.use-case :refer [UseCase]]
            ;; [clojure.tools.logging :as logging]
            [schema.core :as s]))

(s/defrecord TransactionCreate
             [accounts-repository merchants-repository]

  UseCase
  (execute
    [_this params]
    ;; authorize transaction
    ;; save account with new balance
    ;; save transaction

    (let [{:keys [account merchant-name mcc total-amount]} (s/validate schema/Input params)
          merchant-info (mer-repo/get-by-name merchants-repository merchant-name)
          account-info  (acc-repo/get-by-id accounts-repository account)
          category      (mc/get-category mcc (:merchant-category merchant-info))]
          
          
      (println (str "category found: " category))
      (println merchant-info)
      (println account-info)

      (str "Creating transaction " params))))

(defn new
  []
  (map->TransactionCreate {}))