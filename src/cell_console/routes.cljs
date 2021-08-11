(ns cell-console.routes
  (:require
   [re-frame.core :as rf]
   [reitit.coercion.spec]
   [reitit.frontend]
   [reitit.frontend.easy :as rfe]
   [cell-console.views.execute-commands :as execute-commands]
   [cell-console.views.kibana :as kibana]
   [cell-console.views.home :as home]))


(def routes
  ["/"
   [""
    {:name :routes/home
     :view home/main
     :link-text "Home"
     :icon home/drawer-icon}]
   ["execute-commands"
    {:name :routes/execute-commands
     :view execute-commands/main
     :link-text "Execute Commands"
     :icon execute-commands/drawer-icon}]
   ["kibana"
    {:name :routes/kibana
     :view kibana/main
     :link-text "Kibana"
     :icon kibana/drawer-icon}]])

(def router
  (reitit.frontend/router
   routes
   {:data {:coercion reitit.coercion.spec/coercion}}))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [:navigated new-match])))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment true}))
