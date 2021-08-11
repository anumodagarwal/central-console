(ns cell-console.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :current-route
 (fn [db]
   (:current-route db)))

(rf/reg-sub
 :db
 (fn [db]
   db))

(rf/reg-sub
 :errors
 (fn [db]
   (:errors db)))

(rf/reg-sub
 :current-route
 (fn [db]
   (:current-route db)))

(rf/reg-sub
 :drawer/open?
 (fn [db]
   (:drawer/open? db)))

(rf/reg-sub
 :services
 (fn [db]
   (:services db)))

(rf/reg-sub
 :selected-service
 (fn [db]
   (:selected-service db)))