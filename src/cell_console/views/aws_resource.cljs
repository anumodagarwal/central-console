(ns cell-console.views.aws-resource 
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [cell-console.subs]
   ["@material-ui/core" :refer [Paper Button TextField Divider Grid Typography Container List ListItem Stepper Step StepLabel StepContent] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/FilterDrama" :default FilterDramaIcon]
   ["@material-ui/lab" :refer [Autocomplete]]))

;;; Styles
(defn command-ui-styles [^js/Mui.Theme theme]
  (clj->js
   {:icon {:marginRight (.spacing theme 2)}
    :heroContent {:backgroundColor (.. theme -palette -background -paper)
                  :padding (.spacing theme 8 0 6)}
    :heroHeader {:backgroundColor (.. theme -palette -background -paper)
                 :padding (.spacing theme 8 0 6)}
    :paper {:margin (.spacing theme 2 0)
            :padding (.spacing theme 3 3)
            :display "flex"
            :overflow "auto"
            :flexDirection "column"}
    :footer {:backgroundColor (.. theme -palette -background -paper)
             :padding (.spacing theme 6)}}))

(def with-styles (withStyles command-ui-styles))

;; Components
(defn drawer-icon []
  [:> FilterDramaIcon])

(defn search-bar [{:keys [^js classes]}]
  (let [services @(rf/subscribe [:services])]
    [:> Grid {:item true :xs 12}
     [:> Paper {:class (.-paper classes)}
      [:> Autocomplete
       {:options services
        :fullWidth false
        :on-change (fn [_ value]
                     (rf/dispatch [:selected-service value])
                     (if (nil? value)
                       (do
                         (rf/dispatch [:show-cell-info false])
                         (rf/dispatch [:show-command false])
                         (rf/dispatch [:show-result false]))
                      ; else
                       (rf/dispatch [:show-cell-info true])))
        :render-input (fn [^js params]
                        (set! (.-variant params) "outlined")
                        (set! (.-label params) "Select service")
                        (reagent/create-element mui/TextField params))}]]]))


(defn regex-comp [{:keys [^js classes]}]
  [:> Paper {:class (.-paper classes)}
   [:> TextField
    {:fullWidth false
     :class "command-text"
     :InputProps {:classes {:input "command-text"}}
     :on-change #()
     :variant "outlined"
     :label "Enter Regex"}]])

(defn valid-mcm-comp [{:keys [^js classes]}]
  [:> Paper {:class (.-paper classes)}
   [:> TextField
    {:fullWidth false
     :class "valid-mcm-text"
     :InputProps {:classes {:input "command-text"}}
     :on-change #()
     :variant "outlined"
     :label "Enter Valid MCM"}]])

(defn carnaval-steps [{:keys [^js classes]}]

  [:> Stepper {:activeStep  @(rf/subscribe [:carnaval-workflow-step]) :orientation "vertical"}
   [:> Step {:key "step_0"}
    [:> StepLabel
     "Select Service Name"]
    [:> StepContent
     [search-bar {:classes classes}]
     [:> Button {:variant "contained"
                 :color "primary"
                 :disabled false
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 1])
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Next"]]]
   [:> Step {:key "step_1"}
    [:> StepLabel
     "Provide Regex"]
    [:> StepContent
     [regex-comp {:classes classes}]
     [:> Button {:variant "contained"
                 :color "primary"
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 2])
                 :disabled false
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Next"]
     [:> Button {:variant "contained"
                 :color "primary"
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 0])
                 :disabled false
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Back"]]]
   [:> Step {:key "step_2"}
    [:> StepLabel "Verify Carnal Monitors to be deleted"]
    [:> StepContent
     [:> Paper {:class (.-paper classes)}
      [:> List
       [:> ListItem
        [:> Typography {:component "h6" :variant "h5"} "Execution Results"]]
       [:> ListItem
        [:> Typography {:component "h6" :variant "h5"} "SOmething2 "]]
       [:> ListItem
        [:> Typography {:component "h6" :variant "h5"} "Execution Results"]]]]
     [:> Button {:variant "contained"
                 :color "primary"
                 :disabled false
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 3])
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Verify"]
     [:> Button {:variant "contained"
                 :color "primary"
                 :disabled false
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 1])
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Back"]]]
   [:> Step {:key "step_3"}
    [:> StepLabel
     "Provide Valid MCM"]
    [:> StepContent
     [valid-mcm-comp {:classes classes}]
     [:> Button {:variant "contained"
                 :color "primary"
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 5])
                 :disabled false
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Next"]
     [:> Button {:variant "contained"
                 :color "primary"
                 :disabled false
                 :onClick #(rf/dispatch [:set-carnaval-workflow-step 2])
                 :style {:color "white" :background "#2E3B55" :padding 2}} "Back"]]]
   [:> Step {:key "step_4"}
    [:> StepLabel "Alarms Deleted !"]]])



(defn carnaval-ui [{:keys [^js classes]}]
  [:<>
   [:div {:class (.-heroHeader classes) :style {:backgroundColor "#0e2b621f" :height "250px"}}
    [:> Container {:maxWidth "sm"}
     [:> Typography {:component "h3" :variant "h3" :align "center"
                     :color "textPrimary" :gutterBottom true}
      "AWS Resource Across Cells"]]]

   [:> Divider]

   [:> Container {:class (.-cardGrid classes) :maxWidth "md" :style {:paddingTop "50px"}}
    [:> Grid {:container true :spacing 4 :alignItems "flex-start"}
     [:> Grid {:item true :xs 12}
      [carnaval-steps {:classes classes}]]]]
   [:> Container {:class (.-cardGrid classes) :maxWidth "md" :style {:paddingTop "50px"}}
    [:> Button {:variant "contained"
                :color "primary"
                :onClick #(rf/dispatch [:set-carnaval-workflow-step 0])
                :disabled false
                :style {:color "white" :background "#2E3B55" :padding 2}} "Reset"]]])


(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-styles (reagent/reactify-component carnaval-ui))]])

(comment
  (for [[cell-id cell-info] @(rf/subscribe [:service-cells "IhmHoustonService"])]
    (:selected cell-info))
  (rf/dispatch [:toggle-cell "IhmHoustonService" :ihm-dub])
  (rf/dispatch [:toggle-instance "IhmHoustonService" :ihm-dub :id1])
  (rf/dispatch [:toggle-instances-view "IhmHoustonService" :ihm-dub])
  (rf/dispatch [:result/toggle-instance :ihm-dub :id1])
  (rf/dispatch [:result/toggle-cell :ihm-dub])
  (rf/dispatch [:show-command])
  (rf/dispatch [:show-result])
  (rf/dispatch [:set-carnaval-workflow-step 3])
  @(rf/subscribe [:service-cells "IhmHoustonService"])
  (for [[cell-id cell-info] @(rf/subscribe [:service-cells "IhmHoustonService"])]
    (if (:selected cell-info)
      {cell-id (keys (:instances cell-info))}
      {cell-id (for [[instance-id instance-info] (:instances cell-info)]
                 (when (:selected instance-info)
                   instance-id))})))



