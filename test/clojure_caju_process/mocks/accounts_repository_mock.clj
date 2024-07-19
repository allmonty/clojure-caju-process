(ns clojure-caju-process.mocks.accounts-repository-mock 
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]))

(defrecord MockAccountsRepository
  [mocked-functions]
  acc-repo/AccountsRepository
  (get-by-id [_ id]
   ((:get-by-id mocked-functions) id))
  (save [_ account]
   ((:save mocked-functions) account)))

(defn mock-repository
  "Returns a mock account repository"
  [& {:keys [get-by-id-fn save-fn]}]
  (->MockAccountsRepository {:get-by-id get-by-id-fn
                             :save save-fn}))