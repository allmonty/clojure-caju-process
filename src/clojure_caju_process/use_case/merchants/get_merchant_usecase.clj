(ns clojure-caju-process.use-case.merchants.get-merchant-usecase
  (:require [clojure-caju-process.use-case.use-case :refer [UseCase]]
            [clojure-caju-process.use-case.merchants.get-merchant-usecase-schema :as schema]
            [clojure-caju-process.domain.merchants.merchants-repository :as mer-repo]
            ;; [clojure.tools.logging :as logging]
            [schema.core :as s]))

(s/defrecord GetMerchantUseCase
             [merchants-repository]
  UseCase
  (execute
   [_this params]
   (s/validate schema/Input params)
   (mer-repo/get-by-name merchants-repository params)))

(defn new []
  (map->GetMerchantUseCase {}))