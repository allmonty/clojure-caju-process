(ns clojure-caju-process.system
  (:require [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.repository.merchants.merchants-repository-imp :as merchants-repository-imp]
            [clojure-caju-process.infrastructure.web.http :as web-handler]

            [clojure-caju-process.use-case.authorize-transaction.authorize-transaction-use-case :as transaction-create]
            
            [com.stuartsierra.component :as component]))

(defn- system
  []
  (component/system-map
   :database_driver (component/using (database-driver/new) [])
   :merchants-repository (component/using (merchants-repository-imp/new) [:database_driver])
   :transaction_usecase_create (component/using (transaction-create/new) [:merchants-repository])
   :http_presenter (component/using (web-handler/new) [:transaction_usecase_create])))

(defn start
  []
  (component/start (system)))
