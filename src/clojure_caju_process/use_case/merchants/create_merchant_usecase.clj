(ns clojure-caju-process.use-case.merchants.create-merchant-usecase
  (:require [clojure-caju-process.use-case.use-case :refer [UseCase]]
            [clojure-caju-process.use-case.merchants.create-merchant-usecase-schema :as schema]
            [clojure-caju-process.domain.merchants.merchants-repository :as mer-repo]
            ;; [clojure.tools.logging :as logging]
            [schema.core :as s]))

(s/defrecord CreateMerchantUseCase
             [merchants-repository]
  UseCase
  (execute
   [_this params]
   (s/validate schema/Input params)
   (mer-repo/create! merchants-repository params)))

(defn new []
  (map->CreateMerchantUseCase {}))