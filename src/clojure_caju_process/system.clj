(ns clojure-caju-process.system
  (:require [clojure-caju-process.presentation.web.http :as web_handler]
            [clojure-caju-process.use-case.transaction.create :as transaction_create]
            [com.stuartsierra.component :as component]))

(defn- system
  []
  (component/system-map
   :transaction_usecase_create (component/using (transaction_create/new) {})
   :http_presenter (component/using (web_handler/new)
                                    {:transaction_usecase_create :transaction_usecase_create})))

(defn start
  []
  (component/start (system)))
