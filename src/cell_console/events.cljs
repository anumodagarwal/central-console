(ns cell-console.events
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]
            [cell-console.db :refer [app-db]]))


(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   app-db))

(rf/reg-event-fx
 :navigate
 (fn [_cofx [_ & route]]
   {:navigate! route}))

;; Triggering navigation from events.
(rf/reg-fx
 :navigate!
 (fn [route]
   (apply rfe/push-state route)))


(rf/reg-event-db
 :navigated
 (fn [db [_ new-match]]
   (let [old-match   (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :current-route (assoc new-match :controllers controllers)))))


(rf/reg-event-db
 :drawer/open
 (fn [db _]
   (assoc db :drawer/open? true)))

(rf/reg-event-db
 :drawer/close
 (fn [db _]
   (assoc db :drawer/open? false)))

(rf/reg-event-db
 :selected-service
  (fn [db [_ service-name]]
    (assoc db :selected-service service-name)))
