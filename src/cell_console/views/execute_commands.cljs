(ns cell-console.views.execute-commands
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [cell-console.subs]
   ["@material-ui/core" :refer [Paper Divider Button TextField IconButton Collapse Grid Typography Container List ListItem ListItemText ListItemIcon Checkbox] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/SlowMotionVideo" :default SlowMotionVideoIcon]
   ["@material-ui/icons/ExpandLess" :default ExpandLess]
   ["@material-ui/icons/ExpandMore" :default ExpandMore]
   ["@material-ui/icons/Ballot" :default BallotIcon]
   ["@material-ui/icons/ArtTrack" :default ArtTrackIcon]
   ["@material-ui/lab" :refer [Autocomplete]]
   [ajax.core :refer [POST]]))

;;; Styles
(defn command-ui-styles [^js/Mui.Theme theme]
  (clj->js
   {:icon {:marginRight (.spacing theme 2)}
    :heroContent {:backgroundColor (.. theme -palette -background -paper)
                  :padding (.spacing theme 6 0 6)}
    :paper {:margin (.spacing theme 1.5 0)
            :padding (.spacing theme 3 3)
            :display "flex"
            :overflow "auto"
            :flexDirection "column"}
    :footer {:backgroundColor (.. theme -palette -background -paper)
             :padding (.spacing theme 6)}}))

(def with-styles (withStyles command-ui-styles))

;; Components
(defn drawer-icon []
  [:> SlowMotionVideoIcon])

(defn cell-list [{:keys [^js classes service-name]}]
  (let [service-cell-info @(rf/subscribe [:service-cells "IhmHoustonService"])]
    [:> Paper {:class (.-paper classes)}
     [:> List
      [:> ListItem
       [:> Typography {:component "h5" :variant "h5"} "Cell Information"]]]
     [:> List
      (for [[cell-id cell-info] service-cell-info]
        ^{:key cell-id}
        [:div
          [:> ListItem {:button true}
           [:> ListItemIcon
            [:> Checkbox
              {:checked (:selected cell-info)
               :onChange #(rf/dispatch [:toggle-cell service-name cell-id (.. % -target -checked)])
               :color "primary"}]]
           [:> ListItemText {:primary (:name cell-info)}]
           [:> IconButton {:onClick #(rf/dispatch [:toggle-instances-view service-name cell-id])}
            (if (:instances-open cell-info)
             [:> ExpandLess]
             ;; else
             [:> ExpandMore])]]
          [:> Collapse {:in (:instances-open cell-info) :timeout "auto" :unmountonexit "true"}
           (for [[instance-id instance-info] (:instances cell-info)]
             ^{:key instance-id}
             [:> List {:disablePadding true :dense true}
              [:> ListItem {:button true}
               [:> ListItemIcon
                [:> Checkbox
                   {:checked (:selected instance-info)
                    :size "small"
                    :onChange #(rf/dispatch [:toggle-instance service-name cell-id instance-id (.. % -target -checked)])
                    :color "primary"}]]
               [:> ListItemText {:primary (:name instance-info)}]]])]])]]))

(defn search-bar [{:keys [^js classes]}]
  (let [services @(rf/subscribe [:services])]
   [:> Grid {:item true :xs 12}
    [:> Paper {:class (.-paper classes)}
     [:> Autocomplete 
      {:options services
       :value @(rf/subscribe [:selected-service])
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

(defn command-text [{:keys [^js classes]}]
  [:> Paper {:class (.-paper classes)}
    [:> TextField
      {:fullWidth false
       :class "command-text"
       :InputProps {:classes {:input "command-text"}}
       :on-change #()
       :variant "outlined"
       :label "Enter command"}]])

(defn results-view [{:keys [^js classes result]}]
  [:> Paper {:class (.-paper classes)}
   [:> List
    [:> ListItem
      [:> Typography {:component "h5" :variant "h5"} "Execution Results"]]]
   [:> List
    (for [[cell-id cell-info] result]
      ^{:key cell-id}
      [:div
        [:> ListItem {:button true}
         [:> ListItemIcon
          [:> ArtTrackIcon]]
         [:> ListItemText {:primary (:name cell-info)}]
         [:> IconButton {:onClick #(rf/dispatch [:result/toggle-cell cell-id])}
          (if (:instances-open cell-info)
           [:> ExpandLess]
           ;; else
           [:> ExpandMore])]]
        [:> Collapse {:in (:instances-open cell-info) :timeout "auto" :unmountonexit "true"}
         (for [[instance-id instance-info] (:instances cell-info)]
           ^{:key instance-id}
           [:div
            [:> List {:disablePadding true :dense true}
             [:> ListItem {:button true}
              [:> ListItemIcon
                [:> BallotIcon]]
              [:> ListItemText {:primary (:name instance-info)}]
              [:> IconButton {:onClick #(rf/dispatch [:result/toggle-instance cell-id instance-id])}
                (if (:result-open instance-info)
                  [:> ExpandLess]
                  ;; else
                  [:> ExpandMore])]]]
            [:> Collapse {:in (:result-open instance-info) :timeout "auto" :unmountonexit "true"}
             [:> Paper {:class (.-paper classes)}
              [:div (:result instance-info)]]]])]
        [:> Divider]])]])


(defn command-ui [{:keys [^js classes]}]
  [:<>
   [:div {:class (.-heroContent classes)}
    [:> Container {:maxWidth "sm"}
     [:> Typography {:component "h3" :variant "h3" :align "center"
                     :color "textPrimary" :gutterBottom true}
      "Execute Commands Across Cells"]]]
   [:> Container {:class (.-cardGrid classes) :maxWidth "md"}
    [:> Grid {:container true :spacing 4 :alignItems "flex-start"}
     [search-bar {:classes classes}]]
    (let [selected-service @(rf/subscribe [:selected-service])
          show-cell-info @(rf/subscribe [:show-cell-info])]
       (when (and (not (nil? selected-service)) show-cell-info)
        [:> Grid {:container true :spacing 4 :alignItems "center" :justifyContent "center"}
         [:> Grid {:item true :xs 8}
           [cell-list {:service-name selected-service :classes classes}]]
         [:> Grid {:item true :xs 2}
          [:> Button 
            {:variant "contained" 
             :color "primary" 
             :disabled false
             :onClick #(rf/dispatch [:show-command true])
             :style {:color "white" :background "#2E3B55" :padding 2 :border-radius 4 :font-size 15 :width 100}} "Next"]]]))
    (when @(rf/subscribe [:show-command])
     [:> Grid {:container true :spacing 4 :alignItems "center" :justifyContent "center"}
      [:> Grid {:item true :xs 8}
       [command-text {:classes classes}]]
      [:> Grid {:item true :xs 2}
       [:> Button
        {:variant "contained"
         :color "primary"
         :disabled false
         :onClick #(rf/dispatch [:show-result true])
         :style {:color "white" :background "#2E3B55" :padding 2 :border-radius 4 :font-size 15 :width 100}} "Execute"]]])
    (let [execution-result @(rf/subscribe [:execution-result])
          show-result @(rf/subscribe [:show-result])]
     (when (and (not (nil? execution-result)) show-result)
       [:> Grid {:item true :xs 12}
        [results-view {:result execution-result :classes classes}]]))]])

(defn cell-info-response-handler [response]
  (print response))

(defn get-cell-info [service-name]
  (POST "http://127.0.0.1:5000/cell-info"
    {:params
     {:service_name service-name
      :domain "gamma"}
     :format :json
     :response-format :json
     :keywords? true
     :handler cell-info-response-handler}))

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-styles (reagent/reactify-component command-ui))]])

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
  @(rf/subscribe [:service-cells "IhmHoustonService"])
  (for [[cell-id cell-info] @(rf/subscribe [:service-cells "IhmHoustonService"])]
    (if (:selected cell-info)
      {cell-id (keys (:instances cell-info))}
      {cell-id (for [[instance-id instance-info] (:instances cell-info)]
                 (when (:selected instance-info)
                   instance-id))})))

  

  
   
