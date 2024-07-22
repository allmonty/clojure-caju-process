(ns clojure-caju-process.usecase.accounts.get-account-usecase
  (:require [clojure-caju-process.usecase.usecase :refer [UseCase]]
            [clojure-caju-process.usecase.accounts.get-account-usecase-schema :as schema]
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