(ns clojure-caju-process.system
  (:require [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp :as acc-ri]
            [clojure-caju-process.infrastructure.repository.merchants.merchants-repository-imp :as mer-ri]
            [clojure-caju-process.infrastructure.repository.transactions.transactions-repository-imp :as tra-ri]
            [clojure-caju-process.infrastructure.web.http :as web-handler]

            [clojure-caju-process.usecase.accounts.create-account-usecase :as create-account-usecase]
            [clojure-caju-process.usecase.accounts.get-account-usecase :as get-account-usecase]

            [clojure-caju-process.usecase.merchants.create-merchant-usecase :as create-merchant-usecase]
            [clojure-caju-process.usecase.merchants.get-merchant-usecase :as get-merchant-usecase]

            [clojure-caju-process.usecase.debits.create-debit-usecase :as create-debit-usecase]

            [com.stuartsierra.component :as component]))

(defn- system
  []
  (component/system-map
   :http_presenter (component/using (web-handler/new) [:create-account-usecase :get-account-usecase
                                                       :create-merchant-usecase :get-merchant-usecase
                                                       :create-debit-usecase])

   :database-driver (component/using (database-driver/new) [])
   :accounts-repository (component/using (acc-ri/new) [:database-driver])
   :merchants-repository (component/using (mer-ri/new) [:database-driver])
   :transactions-repository (component/using (tra-ri/new) [:database-driver])
   
   :create-account-usecase (component/using (create-account-usecase/new) [:accounts-repository])
   :get-account-usecase (component/using (get-account-usecase/new) [:accounts-repository])
   
   :create-merchant-usecase (component/using (create-merchant-usecase/new) [:merchants-repository])
   :get-merchant-usecase (component/using (get-merchant-usecase/new) [:merchants-repository])
   
   :create-debit-usecase (component/using (create-debit-usecase/new) [:accounts-repository :merchants-repository :transactions-repository])))

(defn start
  []
  (component/start (system)))
