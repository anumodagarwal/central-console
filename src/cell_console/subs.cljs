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

(rf/reg-sub
 :service-cells
 (fn [db [_ service-name]]
   (get-in db [:cell-info service-name])))

(rf/reg-sub
 :execution-result
 (fn [db]
   (get-in db [:execution-result])))

(rf/reg-sub
 :show-command
 (fn [db]
   (get-in db [:show-command])))

(rf/reg-sub
 :show-result
 (fn [db]
   (get-in db [:show-result])))

(rf/reg-sub
 :show-cell-info
 (fn [db]
   (get-in db [:show-cell-info])))

(rf/reg-sub
 :show-cell-info
 (fn [db]
   (get-in db [:show-cell-info])))

(rf/reg-sub
 :get-command
 (fn [db]
   (get-in db [:command])))

(rf/reg-sub
 :cell-info-loading?
 (fn [db]
    (get-in db [:cell-info-loading?])))

(rf/reg-sub
 :execution-result-loading?
 (fn [db]
   (get-in db [:execution-result-loading?])))