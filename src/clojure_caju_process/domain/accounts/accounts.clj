(ns clojure-caju-process.domain.accounts.accounts
  (:require [schema.core :as s]))

(s/defschema Account
  "Schema for a Account"
  {:id s/Str
   :balance {:food s/Num
             :meal s/Num
             :cash s/Num
             s/Any s/Any}
   s/Any s/Any})
