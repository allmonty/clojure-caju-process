(ns clojure-caju-process.presentation.web.http
  (:require [com.stuartsierra.component :refer [Lifecycle]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes transaction
  (context "/transaction" []
    (GET "/:id" [id] (str "Transaction ID: " id))))

(defroutes web-routes
  transaction
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(defrecord HTTPWebHandler
           []
  Lifecycle
  (start [_this]
    (-> web-routes
        (wrap-defaults site-defaults)
        (jetty/run-jetty {:port 3000, :join? false}))))
    
(defn new
  []
  (map->HTTPWebHandler {}))