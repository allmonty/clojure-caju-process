(ns clojure-caju-process.use-case.authorize-transaction.authorize-transaction-schema
  (:require [schema.core :as s]))

(s/defschema Input
  "Input for Authorize Transaction Use Case"
  {:account       s/Str
   :merchant-name s/Str
   :mcc           s/Str
   :total-amount  s/Num
   s/Any s/Any})

;; (s/defschema Output
;;   "Output for Authorize Transaction Use Case"
;;   {:name s/Str
;;    :merchant-code s/Str
;;    s/Any s/Any})
