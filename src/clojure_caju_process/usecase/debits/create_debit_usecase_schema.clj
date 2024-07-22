(ns clojure-caju-process.usecase.debits.create-debit-usecase-schema
  (:require [schema.core :as s]))

(s/defschema Input
  "Input for Debit creation Use Case"
  {:id            s/Str
   :account-id    s/Str
   :merchant-name s/Str
   :mcc           s/Str
   :amount        s/Num
   s/Any s/Any})

(s/defschema Output
  "Output for Debit creation Use Case"
  (s/enum :debit-successful :insufficient-funds))
