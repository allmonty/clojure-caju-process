(ns clojure-caju-process.domain.merchants.merchants 
  (:require [clojure-caju-process.domain.merchant-categories.merchant-categories :refer [MerchantCategory]]
            [schema.core :as s]))

(s/defschema Merchant
  "Schema for a Merchant"
  {:name s/Str
   :merchant-category MerchantCategory
   s/Any s/Any})
