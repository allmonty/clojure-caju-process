(ns clojure-caju-process.use-case.accounts.create-account-usecase
  (:require [clojure-caju-process.use-case.use-case :refer [UseCase]]
            [clojure-caju-process.use-case.accounts.create-account-usecase-schema :as schema]
            [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            ;; [clojure.tools.logging :as logging]
            [schema.core :as s]))

(s/defrecord CreateAccountUseCase
             [accounts-repository]
  UseCase
  (execute
   [_this params]
   (s/validate schema/Input params)
   (acc-repo/create! accounts-repository params)))

(defn new []
  (map->CreateAccountUseCase {}))