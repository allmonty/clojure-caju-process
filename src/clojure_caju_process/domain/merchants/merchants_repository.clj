(ns clojure-caju-process.domain.merchants.merchants-repository
  (:require [clojure-caju-process.domain.merchants.merchants :as merchants]
            [schema.core :as s]))

(s/defprotocol MerchantsRepository
  (get-by-name :- (s/maybe merchants/Merchant) [this name :- s/Str]))
 