(ns clojure-caju-process.domain.accounts.accounts-repository
  (:require [clojure-caju-process.domain.accounts.accounts :as accounts]
            [schema.core :as s]))

(s/defprotocol AccountsRepository
  (get-by-id :- (s/maybe accounts/Account) [this id :- s/Str]))
 