(ns clojure-caju-process.use-case.accounts.create-account-usecase-schema
  (:require [clojure-caju-process.domain.accounts.accounts :as accounts]
            [schema.core :as s]))

(s/defschema Input
  "Input for Create Account UseCase"
  accounts/Account)

(s/defschema Output
  "Output for Create Account UseCase"
  accounts/Account)
