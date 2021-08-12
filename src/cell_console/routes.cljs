(ns cell-console.routes
  (:require
   [re-frame.core :as rf]
   [reitit.coercion.spec]
   [reitit.frontend]
   [reitit.frontend.easy :as rfe]
   [cell-console.views.execute-commands :as execute-commands]
   [cell-console.views.carnaval :as carnaval]
   [cell-console.views.kibana :as kibana]
   [cell-console.views.aws-resource :as aws-resource]
   [cell-console.views.maws-cleanup :as maws-cleanup]
   [cell-console.views.odin :as odin]
   [cell-console.views.vip :as vip]
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
     :link-text "log Analysis"
     :on-click #(. js/window (open "https://search-central-log-visualizer-2-rcz2qqcgvzziv64cyfsfob67ei.us-west-2.es.amazonaws.com/_plugin/kibana/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-15m,to:now))&_a=(columns:!(_source),filters:!(),index:'1fb78dc0-fac0-11eb-ab7e-d72fcc10b058',interval:auto,query:(language:kuery,query:''),sort:!())"))
     :icon kibana/drawer-icon}]
   ["carnaval"
       {:name :routes/carnaval
        :view carnaval/main
        :link-text "Carnaval"
        :icon carnaval/drawer-icon}]
   ["maws-cleanup"
    {:name :routes/maws-cleanup
     :view maws-cleanup/main
     :link-text "MAWS Cleanup"
     :on-click #(. js/window (open "https://search-central-log-visualizer-2-rcz2qqcgvzziv64cyfsfob67ei.us-west-2.es.amazonaws.com/_plugin/kibana/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-15m,to:now))&_a=(columns:!(_source),filters:!(),index:'1fb78dc0-fac0-11eb-ab7e-d72fcc10b058',interval:auto,query:(language:kuery,query:''),sort:!())"))
     :icon maws-cleanup/drawer-icon}]
   ["aws-resource"
    {:name :routes/aws-resource
     :view aws-resource/main
     :link-text "AWS Resource"
     :icon aws-resource/drawer-icon}]
   ["odin"
       {:name :routes/odin
        :view odin/main
        :link-text "Odin"
        :icon odin/drawer-icon}]
   ["vip"
    {:name :routes/vip
     :view vip/main
     :link-text "VIP"
     :on-click #(. js/window (open "https://search-central-log-visualizer-2-rcz2qqcgvzziv64cyfsfob67ei.us-west-2.es.amazonaws.com/_plugin/kibana/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-15m,to:now))&_a=(columns:!(_source),filters:!(),index:'1fb78dc0-fac0-11eb-ab7e-d72fcc10b058',interval:auto,query:(language:kuery,query:''),sort:!())"))
     :icon vip/drawer-icon}]])

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
