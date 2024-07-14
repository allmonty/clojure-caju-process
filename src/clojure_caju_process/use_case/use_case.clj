(ns clojure-caju-process.use-case.use-case)

(defprotocol UseCase
  "Protocol for use cases"
  (execute [this params]))