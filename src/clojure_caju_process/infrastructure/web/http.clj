(ns clojure-caju-process.infrastructure.web.http
  (:require [clojure-caju-process.use-case.use-case :as UseCase]
            [clojure-caju-process.use-case.accounts.create-account-usecase-schema :as create-acc-s]
            [com.stuartsierra.component :refer [Lifecycle]]
            [muuntaja.core :as m]
            [reitit.dev.pretty :as pretty]
            [reitit.coercion.schema]
            [reitit.openapi :as openapi]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja] 
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]))

(def openapi
  [["/openapi.json"
    {:get {:no-doc true
           :openapi {:info {:title "Caju Coding Challenge"
                            :description "openapi3 docs with reitit-ring"
                            :version "0.0.1"}}
           :handler (openapi/create-openapi-handler)}}]])

(defn accounts-context [{:keys [create]}]
  [["/accounts"
    {:post {:summary "Create account"
            :parameters {:body create-acc-s/Input}
            :responses {200 {:body create-acc-s/Output}}
            :handler (fn [{{body :body} :parameters}]
                       {:status 200
                        :body (UseCase/execute create body)})}}]])

;; (defn transaction-context [{:keys [create]}]
;;   [["/transactions/:id"
;;     {:get {:summary "retrieve all transactions"
;;            :parameters {:path {:id int?}}
;;            :responses {200 {:body {:data string?}}}
;;            :handler (fn [{params :parameters}]
;;                       {:status 200
;;                        :body {:data (UseCase/execute create params)}})}}]])

(def router-configs
  {;; :reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
   ;; :validate spec/validate ;; enable spec validation for route data
   ;; :reitit.spec/wrap spell/closed ;; strict top-level validation
   :exception pretty/exception
   :data {:coercion reitit.coercion.schema/coercion
          :muuntaja m/instance
          :middleware [openapi/openapi-feature
                       parameters/parameters-middleware
                       muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-response-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-exceptions-middleware
                       multipart/multipart-middleware]}})

(defn web-router [{:keys [accounts-usecases]}]
  (ring/ring-handler
   (ring/router
    (concat
      openapi
      (accounts-context accounts-usecases))
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
           [create-account-usecase]
  Lifecycle
  (start [_this]
    (-> {:accounts-usecases {:create create-account-usecase}}
        (web-router)
        (jetty/run-jetty {:port 3000, :join? false}))))
    
(defn new
  []
  (map->HTTPWebHandler {}))