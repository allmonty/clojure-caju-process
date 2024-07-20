(ns clojure-caju-process.mocks.merchants-repository-mock
  (:require [clojure-caju-process.domain.merchants.merchants-repository :as MerchantsRepository]))

(defrecord MockMerchantsRepository
           [mocked-functions]
  MerchantsRepository/MerchantsRepository
  (get-by-name [_ name]
    ((:get-by-name mocked-functions) name))
  (create! [_ account]
    ((:create mocked-functions) account)))

(defn mock-repository
  "Returns a mock merchant repository"
  [& {:keys [get-by-name-fn create-fn]}]
  (->MockMerchantsRepository {:get-by-name get-by-name-fn
                              :create create-fn}))