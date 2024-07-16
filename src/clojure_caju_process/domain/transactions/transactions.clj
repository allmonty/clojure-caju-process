(ns clojure-caju-process.domain.transactions.transactions
  (:require [schema.core :as s]))

(s/def ^:private mcc->transaction-category
  {"5411" :food
   "5412" :food
   "5811" :meal
   "5812" :meal})

(s/defn get-transaction-category :- s/Keyword
  [mcc-code :- s/Str]
  (get mcc->transaction-category mcc-code :cash))

(defn authorize
  [num]
  true)
