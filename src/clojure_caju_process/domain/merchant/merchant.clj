(ns clojure-caju-process.domain.merchant.merchant 
  (:require [schema.core :as s]))

(s/defschema Merchant
  "Schema for a Merchant"
  {:name s/Str
   :merchant-code s/Str
   s/Any s/Any})