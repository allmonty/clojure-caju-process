(ns clojure-caju-process.infrastructure.web.http
  (:require [clojure-caju-process.use-case.use-case :as UseCase]
            [com.stuartsierra.component :refer [Lifecycle]]
            [reitit.ring :as ring]
            [reitit.openapi :as openapi]
            [reitit.dev.pretty :as pretty]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart] 
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]))

(def openapi
  [["/openapi.json"
    {:get {:no-doc true
           :openapi {:info {:title "Caju Code Challenge"
                            :description "openapi3 docs with reitit-ring"
                            :version "0.0.1"}}
           :handler (openapi/create-openapi-handler)}}]])

(defn transaction-context [{:keys [create]}]
  [["/transactions/:id"
    {:get {:summary "retrieve all transactions"
           :parameters {:path {:id int?}}
           :responses {200 {:body {:data string?}}}
           :handler (fn [{params :parameters}]
                      {:status 200
                       :body {:data (UseCase/execute create params)}})}}]])

(def router-configs
  {;; :reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
   ;; :validate spec/validate ;; enable spec validation for route data
   ;; :reitit.spec/wrap spell/closed ;; strict top-level validation
   :exception pretty/exception
   :data {:coercion reitit.coercion.spec/coercion
          :muuntaja m/instance
          :middleware [openapi/openapi-feature
                       parameters/parameters-middleware
                       muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-response-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-exceptions-middleware
                       multipart/multipart-middleware]}})

(defn web-router [{:keys [transaction_usecases]}]
  (ring/ring-handler
   (ring/router
    (concat
      openapi
      (transaction-context transaction_usecases))
    router-configs)
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/"
      :config {:validatorUrl nil
               :urls [{:name "openapi", :url "openapi.json"}]
               :urls.primaryName "openapi"
               :operationsSorter "alpha"}})
    (ring/create-default-handler))))

(defrecord HTTPWebHandler
           [transaction_usecase_create]
  Lifecycle
  (start [_this]
    (-> {:transaction_usecases {:create transaction_usecase_create}}
        (web-router)
        (jetty/run-jetty {:port 3000, :join? false}))))
    
(defn new
  []
  (map->HTTPWebHandler {}))