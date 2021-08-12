(ns cell-console.views.home
  (:require
   [reagent.core :as reagent]
   ["@material-ui/core" :refer [ IconButton Grid Paper Typography Container] :as mui]
   ["@material-ui/icons/SlowMotionVideo" :default SlowMotionVideoIcon]
   ["@material-ui/icons/FilterDrama" :default FilterDramaIcon]
   ["@material-ui/icons/AccessAlarm" :default AccessAlarm]
   ["@material-ui/icons/Assignment" :default AssignmentIcon]
   ["@material-ui/icons/DesktopAccessDisabled" :default DesktopAccessDisabledIcon]
   ["@material-ui/icons/Link" :default LinkIcon]
   ["@material-ui/icons/VpnKey" :default VpnKeyIcon]
   ["@material-ui/core/styles" :refer [withStyles]]
   [reitit.frontend.easy :as rfe]
   ["@material-ui/icons/Home" :default HomeIcon]))

;;; Styles
(defn showcase-styles [^js/Mui.Theme theme]
  (clj->js
   {:icon {:marginRight (.spacing theme 2)}
    :heroContent {:backgroundColor (.. theme -palette -background -paper)
                  :padding (.spacing theme 8 0 6)}}))

(def with-showcase-styles (withStyles showcase-styles))

(defn drawer-icon []
  [:> HomeIcon])

(defn showcase [{:keys [^js classes] :as props}]
  [:<>
   [:div {:class (.-heroContent classes) :style {:backgroundColor "#0e2b621f" :height "250px"}}
    [:> Container {:maxWidth "md"}
     [:> Typography {:component "h2" :variant "h2" :align "center"
                     :color "textPrimary" :gutterBottom true}
      "Welcome to Central Cell Console"]
     [:> Typography {:variant "h5" :align "center" :color "textSecondary" :paragraph true}
      "One Stop place to perform multiple operation across cells"]]]
    [:> Container {:maxWidth "md" :align "center" :style {:padding-top "30px"}}
      [:> Grid {:container true :spacing 4 :alignItems "center" :justifyContent "center" }
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                                    [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}}
                                     [:> IconButton {:onClick #(rfe/push-state :routes/kibana)} [:> AssignmentIcon {:style {:font-size "4rem"}}]]
                                     [:> Typography {:component "h6" :variant "h6" :align "center"
                                                     :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}}
                                      "Log Analysis"]]]]
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                             [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}} 
                             [:> IconButton {:onClick #(rfe/push-state :routes/execute-commands)} [:> SlowMotionVideoIcon {:style {:font-size "4rem"}}]]
                             [:> Typography {:component "h6" :variant "h6" :align "center"
                                             :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}} 
                              "Execute Command"]]]]
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                             [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}} 
                             [:> IconButton {:onClick #(rfe/push-state :routes/carnaval)} [:> AccessAlarm {:style {:font-size "4rem"}}]]
                             [:> Typography {:component "h6" :variant "h6" :align "center"
                                             :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}} 
                              "Carnaval Alarm"]]]]
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                             [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}} 
                             [:> IconButton {:onClick #(rfe/push-state :routes/maws-cleanup)} [:> DesktopAccessDisabledIcon {:style {:font-size "4rem"}}]]
                             [:> Typography {:component "h6" :variant "h6" :align "center"
                                             :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}} 
                              "MAWS Cleanup"]]]]
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                             [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}} 
                             [:> IconButton {:onClick #(rfe/push-state :routes/vip)} [:> LinkIcon {:style {:font-size "4rem"}}]]
                             [:> Typography {:component "h6" :variant "h6" :align "center"
                                             :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}} 
                              "VIP"]]]]
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                             [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}} 
                             [:> IconButton {:onClick #(rfe/push-state :routes/odin)} [:> VpnKeyIcon {:style {:font-size "4rem"}}]]
                             [:> Typography {:component "h6" :variant "h6" :align "center"
                                             :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}} 
                              "Odin"]]]]
       [:> Grid {:item true :xs 4} [:> Paper {:class (.-paper classes) :style {:height "200px" :background-color "rgba(75, 76, 100, 0.01)"}}
                             [:div {:style {:padding-top "35px" :text-align "center" :display "block" :margin-top "5px"}} 
                             [:> IconButton {:onClick #(rfe/push-state :routes/aws-resource)} [:> FilterDramaIcon {:style {:font-size "4rem"}}]]
                             [:> Typography {:component "h6" :variant "h6" :align "center"
                                             :color "textPrimary" :gutterBottom true :style {:padding-top "10px"}} 
                              "AWS Resource"]]]]
     ]
    ]])

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-showcase-styles (reagent/reactify-component showcase))]])
