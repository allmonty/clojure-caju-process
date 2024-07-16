(ns clojure-caju-process.infrastructure.database.driver
  (:require [com.stuartsierra.component :refer [Lifecycle]]
            [migratus.core :as migratus]
            [next.jdbc :as jdbc]))

(def db-config
  {:dbtype "postgres"
   :dbname "clojure-caju-process.database"
   :user "postgres"
   :password "secret"
   :dataSourceProperties {:socketTimeout 30}})

(def migration-config
  {:store :database
   :migration-dir "resources/migrations/"
   :db db-config})

(defrecord DatabaseDriver []
  
  Lifecycle
  (start [this]
   (migratus/migrate migration-config)
   (assoc this :database (jdbc/get-datasource db-config)))
  
  (stop [this]
    (.close (jdbc/get-connection (:database this)))
    (dissoc this :database)))

(defn new
  []
  (map->DatabaseDriver {}))
