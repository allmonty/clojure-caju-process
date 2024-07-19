(ns clojure-caju-process.domain.accounts.accounts
  (:require [clojure-caju-process.domain.merchant-categories.merchant-categories :as mc]
            [clojure-caju-process.domain.transactions.transaction :as tr]
            [schema.core :as s]))

(s/defschema Account
  "Schema for a Account"
  {:id s/Str
   :balance {:food s/Num
             :meal s/Num
             :cash s/Num
             s/Any s/Any}
   s/Any s/Any})

(s/defn debit :- (s/conditional
                  #(contains? % :error) {:error s/Keyword}
                  :else Account)
 "Debits an account balance based on the merchant category"
 [account :- Account
  category :- mc/MerchantCategory
  amount :- tr/TransactionAmount]
 (cond
   (>= (get-in account [:balance category]) amount)
   (update-in account [:balance category] - amount)

   (>= (get-in account [:balance :cash]) amount)
   (update-in account [:balance :cash] - amount)
   
   :else
   {:error :insufficient-funds}))
