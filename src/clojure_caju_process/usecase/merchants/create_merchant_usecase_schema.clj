(ns clojure-caju-process.usecase.merchants.create-merchant-usecase-schema
  (:require [clojure-caju-process.domain.merchants.merchants :as merchants]
            [schema.core :as s]))

(s/defschema Input
  "Input for Create Merchant UseCase"
  merchants/Merchant)

(s/defschema Output
  "Output for Create Merchant UseCase"
  merchants/Merchant)
