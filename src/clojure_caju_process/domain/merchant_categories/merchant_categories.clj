(ns clojure-caju-process.domain.merchant-categories.merchant-categories
  (:require [schema.core :as s]))

(s/defschema MerchantCategory
  "Schema for a merchant categories"
  (s/enum :food :meal :cash))

(s/defn ^:private mcc->category :- MerchantCategory
  [mcc-code :- s/Str]
  (-> {"5411" :food
       "5412" :food
       "5811" :meal
       "5812" :meal}
      (get mcc-code :cash)))

(s/defn get-category
  [mcc-code :- s/Str
   merchant-category :- (s/maybe MerchantCategory)]
  (or merchant-category
      (mcc->category mcc-code)))
