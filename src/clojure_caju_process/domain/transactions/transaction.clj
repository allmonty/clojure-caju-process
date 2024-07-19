(ns clojure-caju-process.domain.transactions.transaction
  (:require [schema.core :as s]))

(def TransactionAmount s/Num)

(s/defschema Transaction
  {:account s/Str
   :totalAmount TransactionAmount
   :merchant-category s/Str
   :merchant-name s/Str
   s/Any s/Any})
