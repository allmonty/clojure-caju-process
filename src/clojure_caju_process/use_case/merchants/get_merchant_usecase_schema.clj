(ns clojure-caju-process.use-case.merchants.get-merchant-usecase-schema
  (:require [clojure-caju-process.domain.merchants.merchants :as merchants]
            [schema.core :as s]))

(s/def Input
  "Input for Get Merchant UseCase"
  s/Str)

(s/defschema Output
  "Output for Get Merchant UseCase"
  merchants/Merchant)
