(ns clojure-caju-process.integration.test-helper
  (:require [clojure-caju-process.system :as system]))

(def system (atom nil))

(defn start-system []
  (when (nil? @system)
    (reset! system (system/start))))
