(defproject clojure-caju-process "0.1.0-SNAPSHOT"
  :description "Caju code challenge"
  :url "http://example.com/FIXME"
  :main clojure-caju-process.core/-main
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]

                 ; Components arch / dependency injection
                 [com.stuartsierra/component "1.1.0"]

                 ; Schemas
                 [prismatic/schema "1.4.1"]

                 ; Web server and router
                 [metosin/reitit "0.7.1"]
                 [metosin/muuntaja "0.6.10"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.8.2"]

                ; Database
                 [com.github.seancorfield/next.jdbc "1.3.939"]
                 [migratus "1.5.6"]
                 [org.postgresql/postgresql "42.6.0"]
                 [com.github.seancorfield/honeysql "2.6.1147"]

                 ; O11y
                 [org.clojure/tools.logging "1.2.4"]] 
  :plugins [[lein-ring "0.12.5"]]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]
         :plugins      [[lein-cloverage "1.2.2"]]
         :test-selectors {:default (complement :integration)
                          :integration :integration}}})
