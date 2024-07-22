(ns clojure-caju-process.infrastructure.web.http
  (:require [clojure-caju-process.usecase.accounts.create-account-usecase-schema :as create-acc-s]
            [clojure-caju-process.usecase.accounts.get-account-usecase-schema :as get-acc-s]
            [clojure-caju-process.usecase.debits.create-debit-usecase-schema :as create-deb-s]
            [clojure-caju-process.usecase.merchants.create-merchant-usecase-schema :as create-mer-s]
            [clojure-caju-process.usecase.merchants.get-merchant-usecase-schema :as get-mer-s]
            [clojure-caju-process.usecase.usecase :as UseCase]
            [clojure.tools.logging :as logging]
            [com.stuartsierra.component :refer [Lifecycle]]
            [muuntaja.core :as m]
            [reitit.coercion.schema]
            [reitit.dev.pretty :as pretty]
            [reitit.openapi :as openapi]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]
            [schema.core :as s]))

(def openapi
  [["/openapi.json"
    {:get {:no-doc true
           :openapi {:info {:title "Caju Coding Challenge"
                            :description "openapi3 docs with reitit-ring"
                            :version "0.0.1"}}
           :handler (openapi/create-openapi-handler)}}]])

(defn accounts-context [{:keys [create get]}]
  [["/accounts"
    {:post {:summary "Create account"
            :tags ["Accounts"]
            :parameters {:body create-acc-s/Input}
            :responses {200 {:body create-acc-s/Output}}
            :handler (fn [{{body :body} :parameters}]
                       {:status 200
                        :body (UseCase/execute create body)})}}]
   ["/accounts/:id"
    {:get {:summary "Retrieve one account"
           :tags ["Accounts"]
           :parameters {:path {:id get-acc-s/Input}}
           :responses {200 {:body get-acc-s/Output}
                       404 {}}
           :handler (fn [{{id :id} :path-params}]
                      (let [resp (UseCase/execute get id)]
                        (cond
                          (nil? resp) {:status 404}
                          :else {:status 200 :body resp})))}}]])

(defn merchants-context [{:keys [create get]}]
  [["/merchants"
    {:post {:summary "Create merchant"
            :tags ["Merchants"]
            :parameters {:body create-mer-s/Input}
            :responses {200 {:body create-mer-s/Output}}
            :handler (fn [{{body :body} :parameters}]
                       {:status 200
                        :body (UseCase/execute create body)})}}]
   ["/merchants/:name"
    {:get {:summary "Retrieve one merchant"
           :tags ["Merchants"]
           :parameters {:path {:name get-mer-s/Input}}
           :responses {200 {:body get-mer-s/Output}
                       404 {}}
           :handler (fn [{{name :name} :path-params}]
                      (let [resp (UseCase/execute get name)]
                        (cond
                          (nil? resp) {:status 404}
                          :else {:status 200 :body resp})))}}]])

(defn debits-context [{:keys [create]}]
  [["/debits"
    {:post {:summary "Create debit"
            :tags ["Debits"]
            :parameters {:body create-deb-s/Input}
            :responses {200 {:body {:code (s/enum "00" "51" "07")}}}
            :handler (fn [{{body :body} :parameters}]
                       (try
                         {:status 200
                          :body (case (UseCase/execute create body)
                                  :debit-successful {:code "00"}
                                  :insufficient-funds {:code "51"})}
                         (catch Exception e
                           (logging/error "Error creating debit" :error_message (.getMessage e))
                           {:status 200
                            :body {:code "07"}})))}}]])

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
                       exception/default-handlers
                       exception/exception-middleware
                       coercion/coerce-response-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-exceptions-middleware
                       multipart/multipart-middleware]}})

(defn web-router [{:keys [accounts-usecases merchants-usecases debits-usecases]}]
  (ring/ring-handler
   (ring/router
    (concat
      openapi
      (accounts-context accounts-usecases)
      (merchants-context merchants-usecases)
      (debits-context debits-usecases))
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
           [create-account-usecase get-account-usecase
            create-merchant-usecase get-merchant-usecase
            create-debit-usecase]
  Lifecycle
  (start [_this]
    (-> {:accounts-usecases {:create create-account-usecase
                             :get get-account-usecase}
         :merchants-usecases {:create create-merchant-usecase
                              :get get-merchant-usecase}
         :debits-usecases {:create create-debit-usecase}}
        (web-router)
        (jetty/run-jetty {:port 3000, :join? false}))))
    
(defn new
  []
  (map->HTTPWebHandler {}))