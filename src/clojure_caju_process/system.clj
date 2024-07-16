(ns clojure-caju-process.system
  (:require [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.web.http :as web-handler]
            [clojure-caju-process.use-case.transactions.create :as transaction-create]
            [com.stuartsierra.component :as component]))

(defn- system
  []
  (component/system-map
   :transaction_usecase_create (component/using (transaction-create/new) {})
   :database_driver (component/using (database-driver/new) {})
   :http_presenter (component/using (web-handler/new)
                                    {:transaction_usecase_create :transaction_usecase_create})))

(defn start
  []
  (component/start (system)))
