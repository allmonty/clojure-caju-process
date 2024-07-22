(ns clojure-caju-process.infrastructure.repository.transactions.transactions-repository-imp-test
  (:require [clojure-caju-process.domain.accounts.accounts-repository :as acc-repo]
            [clojure-caju-process.domain.transactions.transactions-repository :as tra-repo]
            [clojure-caju-process.infrastructure.database.driver :as database-driver]
            [clojure-caju-process.infrastructure.repository.accounts.accounts-repository-imp :as accri]
            [clojure-caju-process.infrastructure.repository.transactions.transactions-repository-imp :as tri]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [schema.test :as s]
            [matcher-combinators.test]
            [next.jdbc :as jdbc])
  (:import [org.postgresql.util PSQLException]))

(def test-system (atom nil))

(defn with-test-db
  [f]
  (reset! test-system (component/start
                       (component/system-map
                        :database-driver (component/using (database-driver/new) [])
                        :accounts-repository (component/using (accri/new) [:database-driver])
                        :transactions-repository (component/using (tri/new) [:database-driver]))))
  (f)
  (jdbc/execute! (-> @test-system :database-driver :database) ["DELETE FROM transactions;"])
  (jdbc/execute! (-> @test-system :database-driver :database) ["DELETE FROM accounts;"])
  (component/stop @test-system))

(use-fixtures :once with-test-db)

(s/deftest ^:integration test-transactions-repository
  (let [new-account {:id "trarepotest:456" :balance {:food 100 :meal 100 :cash 100}}
        new-transaction {:id "trarepotest:123" :account (:id new-account) :amount 789.0 :merchant-category :food :merchant-name "mer name" :type :debit}
        _ (acc-repo/create! (:accounts-repository @test-system) new-account)]
    (testing "Happy path:"
      (testing "When creating new transaction for existing account, returns transaction"
        (is (match? new-transaction (tra-repo/create! (:transactions-repository @test-system) {} new-transaction))))
      (testing "Transactions can receive float"
        (let [transaction-float (assoc new-transaction :id "trarepotest:457" :amount 789.98)]
          (is (match? transaction-float (tra-repo/create! (:transactions-repository @test-system) {} transaction-float)))))

      (testing "When getting existing transaction by id, returns the transaction"
        (is (match? new-transaction
               (tra-repo/get-by-id (:transactions-repository @test-system) (:id new-transaction))))))

    (testing "Edge cases:"
      (testing "When no transaction is found by id, return empty"
        (is (nil? (tra-repo/get-by-id (:transactions-repository @test-system) "000"))))

      (testing "When trying to create transaction with same id, throws exception"
        (is (thrown-with-msg? PSQLException #"ERROR: duplicate key value violates unique constraint \"transactions_pkey\"\n  Detail: Key \(id\)="
                              (tra-repo/create! (:transactions-repository @test-system) {} new-transaction))))
      
      (testing "When trying to create transaction for unexistent account, throws exception"
        (is (thrown-with-msg? PSQLException #"ERROR: insert or update on table \"transactions\" violates foreign key constraint \"transactions_account_fkey"
                              (tra-repo/create! (:transactions-repository @test-system) {} (assoc new-transaction
                                                                                                  :id "trarepotest:000"
                                                                                                  :account "000"))))))))
