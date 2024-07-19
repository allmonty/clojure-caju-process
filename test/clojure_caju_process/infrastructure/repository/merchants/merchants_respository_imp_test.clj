(ns clojure-caju-process.infrastructure.repository.merchants.merchants-respository-imp-test
  (:require [clojure-caju-process.domain.merchants.merchants-repository :as mer-repo]
            [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.repository.merchants.merchants-repository-imp :as mri]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [schema.test :as s]
            [next.jdbc :as jdbc])
  (:import (java.util UUID)
           [org.postgresql.util PSQLException]))

(def test-system (atom nil))

(defn with-test-db
  [f]
  (reset! test-system (component/start
                       (component/system-map
                        :database-driver (component/using (database-driver/new) [])
                        :merchants-repository (component/using (mri/new) [:database-driver]))))
  (f)
  (jdbc/execute! (-> @test-system :database-driver :database) ["DELETE FROM merchants;"])
  (component/stop @test-system))

(use-fixtures :once with-test-db)

(s/deftest ^:integration test-merchants-repository
  (let [new-merchant {:id (UUID/randomUUID) :name "name", :merchant-category :food}]
    (testing "Happy path:"
      (testing "When creating new merchant, returns the merchant created"
        (is (= new-merchant
               (mer-repo/create (:merchants-repository @test-system) new-merchant))))
    
      (testing "When getting existing merchant by name, returns the merchant"
        (is (= new-merchant
               (mer-repo/get-by-name (:merchants-repository @test-system) "name")))))
    
    (testing "Edge cases:"
      (testing "When no merchant is found by name, return empty"
        (is (nil? (mer-repo/get-by-name (:merchants-repository @test-system) "000"))))
    
      (testing "When trying to create merchant with same id, throws exception"
        (is (thrown-with-msg? PSQLException #"ERROR: duplicate key value violates unique constraint \"merchants_pkey\"\n  Detail: Key \(id\)="
                              (mer-repo/create (:merchants-repository @test-system) (assoc new-merchant :name "other name")))))
      
      (testing "When trying to create merchant with same name, throws exception"
        (is (thrown-with-msg? PSQLException #"ERROR: duplicate key value violates unique constraint \"merchants_name_key\"\n  Detail: Key \(name\)="
                              (mer-repo/create (:merchants-repository @test-system) (assoc new-merchant :id (UUID/randomUUID)))))))))
