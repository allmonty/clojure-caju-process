(ns clojure-caju-process.domain.merchant-categories.merchant-categories-test
  (:require [clojure.test :refer :all]
            [clojure-caju-process.domain.merchant-categories.merchant-categories :as mc]
            [schema.test :as s]))

(s/deftest test-get-category
  (testing "When no category is provided"
    (testing "return the category for known mcc codes"
      (is (= (mc/get-category "5411" nil) :food))
      (is (= (mc/get-category "5412" nil) :food))
      (is (= (mc/get-category "5811" nil) :meal))
      (is (= (mc/get-category "5811" nil) :meal))))

  (testing "return :cash for unknown mcc codes"
    (is (= (mc/get-category "123" nil) :cash))
    (is (= (mc/get-category "teste" nil) :cash)))
  
  (testing "When a category is provided"
    (testing "return the category provided ignoring the mcc code"
      (is (= (mc/get-category "5811" :food) :food))
      (is (= (mc/get-category "5411" :meal) :meal))
      (is (= (mc/get-category "5811" :cash) :cash))
      (is (= (mc/get-category "123" :food) :food))
      (is (= (mc/get-category "teste" :meal) :meal)))))
