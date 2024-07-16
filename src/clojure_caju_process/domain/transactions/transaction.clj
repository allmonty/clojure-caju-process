(ns clojure-caju-process.domain.transactions.transaction
  (:require [schema.core :as s]))

(s/defschema Transaction
  {:account s/Str
   :totalAmount s/Num
   :merchant-category s/Str
   :merchant-name s/Str
   s/Any s/Any})
