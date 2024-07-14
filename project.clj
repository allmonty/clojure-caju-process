(defproject clojure-caju-process "0.1.0-SNAPSHOT"
  :description "Caju code challenge"
  :url "http://example.com/FIXME"
  :main clojure-caju-process.core/-main
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]

                 ; Dependency
                 [com.stuartsierra/component "1.1.0"]
                 [metosin/compojure-api "1.1.14"]
                 [prismatic/schema "1.4.1"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 
                 ; O11y
                 [org.clojure/tools.logging "1.2.4"]] 
  :plugins [[lein-ring "0.12.5"]]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]
         :plugins      [[lein-cloverage "1.2.2"]]}})
