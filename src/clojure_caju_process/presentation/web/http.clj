(ns clojure-caju-process.presentation.web.http
  (:require [clojure-caju-process.use-case.use-case :as UseCase]
            [clojure-caju-process.presentation.web.exception-middleware :as ex-middleware]
            [com.stuartsierra.component :refer [Lifecycle]]
            [compojure.api.sweet :refer [DELETE GET PATCH POST api context resource routes]]
            [compojure.api.exception :as ex]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [schema.core :as s]))

(defn transaction-context [{:keys [create]}]
  (routes
   (context "/transaction" []
     :tags ["Transaction"]
     (resource
      {:get {:parameters {:query-params {:name String}}
             :responses {200 {:schema s/Any}
                         404 {}
                         500 {:schema s/Any}}
             :handler (fn [params] (UseCase/execute create params))}}))))

(defn web-routes [{:keys [transaction_usecases]}]
  (routes
   (transaction-context transaction_usecases)))

(defrecord HTTPWebHandler
           [transaction_usecase_create]
  Lifecycle
  (start [_this]
    (->
     (api
      {:exceptions {:handlers {::ex/default ex-middleware/wrap-exception-handler}}
       :swagger {:ui   "/docs"
                 :spec "/swagger.json"
                 :data {:info     {:title       "Caju Coding Challenge"
                                   :description "Project to process transactions"}
                        :consumes ["application/json"]
                        :produces ["application/json"]}}}
      (web-routes {:transaction_usecases {:create transaction_usecase_create}}))
     (wrap-defaults site-defaults)
     (jetty/run-jetty {:port 3000, :join? false}))))
    
(defn new
  []
  (map->HTTPWebHandler {}))