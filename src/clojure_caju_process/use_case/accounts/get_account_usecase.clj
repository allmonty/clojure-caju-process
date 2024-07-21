(ns clojure-caju-process.use-case.accounts.get-account-usecase
  (:require [clojure-caju-process.use-case.use-case :refer [UseCase]]
            [clojure-caju-process.use-case.accounts.get-account-usecase-schema :as schema]
            [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            ;; [clojure.tools.logging :as logging]
            [schema.core :as s]))

(s/defrecord GetAccountUseCase
             [accounts-repository]
  UseCase
  (execute
   [_this params]
   (s/validate schema/Input params)
   (acc-repo/get-by-id accounts-repository params)))

(defn new []
  (map->GetAccountUseCase {}))