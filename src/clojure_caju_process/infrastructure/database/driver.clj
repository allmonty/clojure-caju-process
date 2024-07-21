(ns clojure-caju-process.infrastructure.database.driver
  (:require [com.stuartsierra.component :refer [Lifecycle]]
            [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

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

(defprotocol Database
  (execute! [this query])
  (get-by [this table column data])
  (insert! [this table data])
  (update! [this table id data])
  (delete! [this table id]))

(defrecord DatabaseDriver []
  Database
  (execute! [this query]
    (jdbc/execute! (:database this) query {:return-keys true}))

  (get-by [this table column data]
    (sql/find-by-keys (:database this) table {column data}))

  (insert! [this table data]
    (sql/insert! (:database this) table data))

  (update! [this table id data]
    (sql/update! (:database this) table data {:id id} {:return-keys true}))

  (delete! [this table id]
    (sql/delete! (:database this) table {:id id}))

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
