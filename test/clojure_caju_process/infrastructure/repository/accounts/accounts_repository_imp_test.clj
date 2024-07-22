(ns clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp-test
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp :as accri]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [schema.test :as s]
            [clojure-caju-process.integration.test-helper :as th])
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

(s/deftest ^:integration test-create-get-accounts-repository
  (let [account {:id "accrepotest:123" :balance {:food 100.0 :meal 200.0 :cash 300.0}}]
    (testing "Happy path:"
      (testing "When creating new account, returns the account created"
        (is (= account
               (acc-repo/create! (:accounts-repository @test-system) account))))

      (testing "When getting existing account by id, returns the account"
        (is (= account
               (acc-repo/get-by-id (:accounts-repository @test-system) (:id account))))))
    
    (testing "Edge cases:"
      (testing "When no account is found by id, return empty"
        (is (nil? (acc-repo/get-by-id (:accounts-repository @test-system) "000"))))

      (testing "When trying to create account with same id, throws exception"
        (is (thrown-with-msg? PSQLException #"ERROR: duplicate key value violates unique constraint \"accounts_pkey\"\n  Detail: Key \(id\)="
                              (acc-repo/create! (:accounts-repository @test-system) account)))))))

(s/deftest ^:integration test-update-accounts-repository
  (let [account {:id "accrepotest:124" :balance {:food 100.0 :meal 200.0 :cash 300.0}}
        _ (acc-repo/create! (:accounts-repository @test-system) account)
        new_balance (assoc account :balance {:food 50.0 :meal 150.0 :cash 250.0})]
    (testing "Happy path:"
      (testing "When updating account balance, changes balance and returns the account"
        (is (= new_balance
               (acc-repo/update-balance! (:accounts-repository @test-system) {} new_balance)))))

    (testing "Edge cases:"
      (testing "When trying to update balance of non-existing account, returns nil"
        (is (nil? (acc-repo/update-balance! (:accounts-repository @test-system) {} (assoc account :id "000"))))))))

(s/deftest ^:integration test-consistent-update-accounts-repository
  (let [account {:id "accrepotest:125" :balance {:food 100.0 :meal 200.0 :cash 300.0}}
        account_repository (:accounts-repository @test-system)
        _ (acc-repo/create! account_repository account)
        acc_new_balance (update-in account [:balance :food] - 50)
        acc_newer_balance (update-in acc_new_balance [:balance :food] - 50)]
    (testing "Will lock the account for update and execute the function passing a connection"
      (is (= acc_new_balance
             (acc-repo/consistent-update! account_repository
                                          (:id account)
                                          (fn [_account tx]
                                            (acc-repo/update-balance! account_repository {:conn tx} acc_new_balance)))))
      (is (= acc_new_balance
             (acc-repo/get-by-id account_repository (:id account)))))
    
    (testing "Will rollback the updates in case of exception"
      (is (thrown? Exception   (acc-repo/consistent-update! account_repository
                                                            (:id account)
                                                            (fn [account tx]
                                                              (acc-repo/update-balance! account_repository {:conn tx} acc_newer_balance)
                                                              (throw (Exception. "Rollback"))))))
      (is (not= acc_newer_balance (acc-repo/get-by-id account_repository (:id account))))
      (is (= acc_new_balance (acc-repo/get-by-id account_repository (:id account)))))))
