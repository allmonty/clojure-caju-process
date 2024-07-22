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
  (execute!          [this opts query])
  (get-by            [this opts table column data])
  (insert!           [this opts table data])
  (update!           [this opts table id data])
  (delete!           [this opts table id])
  (with-lock-update! [this table entity function]))

(defrecord DatabaseDriver []
  Database
  (execute! [this {:keys [conn]} query]
    (let [conn (or conn (:database this))]
      (jdbc/execute! conn query {:return-keys true})))

  (get-by [this {:keys [conn]} table column data]
    (let [conn (or conn (:database this))]
      (sql/find-by-keys conn table {column data})))

  (insert! [this {:keys [conn]} table data]
    (let [conn (or conn (:database this))]
      (sql/insert! conn table data)))

  (update! [this {:keys [conn]} table id data]
    (let [conn (or conn (:database this))]
      (sql/update! conn table data {:id id} {:return-keys true})))

  (delete! [this {:keys [conn]} table id]
    (let [conn (or conn (:database this))]
     (sql/delete! conn table {:id id})))

  (with-lock-update! [this table id function]
    (jdbc/with-transaction [tx (:database this)]
      (-> (jdbc/execute-one! tx [(str "SELECT * FROM " (name table) " WHERE id = ? FOR UPDATE;") id] {:return-keys true})
          (function tx))))

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
