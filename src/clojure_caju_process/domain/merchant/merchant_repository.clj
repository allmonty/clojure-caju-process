(ns clojure-caju-process.domain.merchant.merchant-repository
  (:require [clojure-caju-process.domain.merchant.merchant :as merchant]
            [schema.core :as s]))

(s/defprotocol MerchantRepository
  (get-by-name :- merchant/Merchant [this name :- s/Str]))
 
