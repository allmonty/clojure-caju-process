(ns clojure-caju-process.usecase.usecase)

(defprotocol UseCase
  "Protocol for use cases"
  (execute [this params]))