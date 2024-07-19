(ns clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp-test
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp :as accri]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [schema.test :as s])
  (:import [org.postgresql.util PSQLException]))

(def test-system (atom nil))

(defn with-test-db
  [f]
  (reset! test-system (component/start
                       (component/system-map
                        :database-driver (component/using (database-driver/new) [])
                        :accounts-repository (component/using (accri/new) [:database-driver]))))
  (f)
  (jdbc/execute! (-> @test-system :database-driver :database) ["DELETE FROM accounts;"])
  (component/stop @test-system))

(use-fixtures :once with-test-db)

(s/deftest ^:integration test-accounts-repository
  (let [new-account {:id "123" :balance {:food 100 :meal 200 :cash 300}}]
    (testing "Happy path:"
      (testing "When creating new account, returns the account created"
        (is (= new-account
               (acc-repo/create (:accounts-repository @test-system) new-account))))

      (testing "When getting existing account by id, returns the account"
        (is (= new-account
               (acc-repo/get-by-id (:accounts-repository @test-system) "123")))))
    
    (testing "Edge cases:"
      (testing "When no account is found by id, return empty"
        (is (nil? (acc-repo/get-by-id (:accounts-repository @test-system) "000"))))

      (testing "When trying to create account with same id, throws exception"
        (is (thrown-with-msg? PSQLException #"ERROR: duplicate key value violates unique constraint \"accounts_pkey\"\n  Detail: Key \(id\)="
                              (acc-repo/create (:accounts-repository @test-system) new-account)))))))
                     
