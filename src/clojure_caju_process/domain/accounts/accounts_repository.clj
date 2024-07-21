(ns clojure-caju-process.domain.accounts.accounts-repository
  (:require [clojure-caju-process.domain.accounts.accounts :as accounts]
            [schema.core :as s]))

(s/defprotocol AccountsRepository
  (create! :- accounts/Account [this account :- accounts/Account])
  (get-by-id :- (s/maybe accounts/Account) [this id :- s/Str])
  (update-balance! :- (s/maybe accounts/Account) [this account :- accounts/Account]))
