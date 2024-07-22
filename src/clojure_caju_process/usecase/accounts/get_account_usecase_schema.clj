(ns clojure-caju-process.usecase.accounts.get-account-usecase-schema
  (:require [clojure-caju-process.domain.accounts.accounts :as accounts]
            [schema.core :as s]))

(s/def Input
  "Input for Get Account UseCase"
  s/Str)

(s/defschema Output
  "Output for Get Account UseCase"
  accounts/Account)
