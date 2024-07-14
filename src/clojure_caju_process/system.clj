(ns clojure-caju-process.system
  (:require [clojure-caju-process.presentation.web.http :as web_handler]
            [com.stuartsierra.component :as component]))

(defn- system
  []
  (component/system-map
   :http_presenter (component/using (web_handler/new) {})))

(defn start
  []
  (component/start (system)))
