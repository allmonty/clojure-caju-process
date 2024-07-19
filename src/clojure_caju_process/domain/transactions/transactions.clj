(ns clojure-caju-process.domain.transactions.transactions
  (:require [clojure-caju-process.domain.merchant-categories.merchant-categories :as mc]
            [schema.core :as s]))

(def TransactionAmount s/Num)

(s/defschema Transaction
  {:id s/Str
   :account s/Str
   :amount TransactionAmount
   :merchant-category mc/MerchantCategory
   :merchant-name s/Str
   s/Any s/Any})
