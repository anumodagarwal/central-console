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

(rf/reg-event-db
 :toggle-cell
 (fn [db [_ service-name cell-id selected?]]
   (doseq [[instance-id _] (get-in app-db [:cell-info service-name cell-id :instances])]
    (rf/dispatch [:toggle-instance service-name cell-id instance-id selected?]))
   (assoc-in db [:cell-info service-name cell-id :selected] selected?)))
   
(rf/reg-event-db
 :toggle-instance
 (fn [db [_ service-name cell-id instance-id selected?]]
   (assoc-in db [:cell-info service-name cell-id :instances instance-id :selected] selected?)))

(rf/reg-event-db
 :toggle-instances-view
 (fn [db [_ service-name cell-id]]
   (update-in db [:cell-info service-name cell-id :instances-open] not)))

(rf/reg-event-db
  :result/toggle-cell
  (fn [db [_ cell-id]]
  ;;  (doseq [[instance-id _] (get-in app-db [:execution-result cell-id :instances])]
  ;;   (rf/dispatch [:result/toggle-instance cell-id instance-id]))
   (update-in db [:execution-result cell-id :instances-open] not)))

(rf/reg-event-db
 :result/toggle-instance
 (fn [db [_ cell-id instance-id]]
  (update-in db [:execution-result cell-id :instances instance-id :result-open] not)))

(rf/reg-event-db
 :show-command
 (fn [db [_ show?]]
   (assoc db :show-command show?)))

(rf/reg-event-db
 :show-result
 (fn [db [_ show?]]
   (assoc db :show-result show?)))

(rf/reg-event-db
 :show-cell-info
 (fn [db [_ show?]]
    (assoc db :show-cell-info show?)))