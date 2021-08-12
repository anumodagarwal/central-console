(ns cell-console.views.execute-commands
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [cell-console.subs]
   [ajax.core :refer [POST]]
   ["@material-ui/core" :refer [Paper CircularProgress Divider Button TextField IconButton Collapse Grid Typography Container List ListItem ListItemText ListItemIcon Checkbox] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/SlowMotionVideo" :default SlowMotionVideoIcon]
   ["@material-ui/icons/ExpandLess" :default ExpandLess]
   ["@material-ui/icons/ExpandMore" :default ExpandMore]
   ["@material-ui/icons/Ballot" :default BallotIcon]
   ["@material-ui/icons/ArtTrack" :default ArtTrackIcon]
   ["@material-ui/lab" :refer [Autocomplete]]))
   
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
  (let [service-cell-info @(rf/subscribe [:service-cells service-name])]
   [:> Paper {:class (.-paper classes)}
    [:> List
     [:> ListItem
      [:> Typography {:component "h5" :variant "h5"} "Select Cells / Instances"]]]
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

(defn get-cell-info [service-name]
  (rf/dispatch [:cell-info-loading? true])
  (POST "http://127.0.0.1:5000/cell-info"
    {:params
     {:service_name service-name
      :domain "gamma"}
     :format :json
     :response-format :json
     :keywords? true
     :handler 
     (fn [response]
       (rf/dispatch [:update-cell-info response])
       (rf/dispatch [:cell-info-loading? false])
       (rf/dispatch [:show-cell-info true]))}))

(defn get-selected-instances []
  (let [empty-vals (fn [[_ v]] (empty? v))]
    (into {}
      (remove empty-vals
        (into {} 
          (for [[cell-id cell-info] @(rf/subscribe [:service-cells :IhmHoustonService])]
            {cell-id (remove nil? (for [[instance-id instance-info] (:instances cell-info)]
                                      (when (:selected instance-info)
                                        instance-id)))}))))))
(defn get-execution-result []
  (rf/dispatch [:execution-result-loading? true])
  (let [service-name  @(rf/subscribe [:selected-service])
        command       @(rf/subscribe [:get-command])
        instance-info (get-selected-instances)]
    (POST "http://127.0.0.1:5000/execute-command"
      {:params
       {:service_name (name service-name)
        :instance_info instance-info
        :command command
        :domain "gamma"}
       :format :json
       :response-format :json
       :keywords? true
       :handler 
       (fn [response] 
         (rf/dispatch [:update-execution-result response])
         (rf/dispatch [:execution-result-loading? false])
         (rf/dispatch [:show-result true]))})))

(defn search-bar [{:keys [^js classes]}]
  (let [services @(rf/subscribe [:services])
        selected-service @(rf/subscribe [:selected-service])]
   [:> Grid {:item true :xs 12}
    [:> Paper {:class (.-paper classes)}
     [:> Autocomplete 
      {:options services
       :value (if selected-service (name selected-service) nil)
       :fullWidth false
       :on-change (fn [_ value]
                    (rf/dispatch [:selected-service (keyword value)])
                    (if (nil? value)
                      (do
                        (rf/dispatch [:show-cell-info false])
                        (rf/dispatch [:show-command false])
                        (rf/dispatch [:show-result false])
                        (rf/dispatch [:update-cell-info nil])
                        (rf/dispatch [:update-execution-result nil]))
                      ; else -- We have a service selected. Get the cell information
                      (get-cell-info value)))
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
       :on-change #(rf/dispatch [:set-command (.. % -target -value)])
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
          show-cell-info @(rf/subscribe [:show-cell-info])
          cell-info-loading? @(rf/subscribe [:cell-info-loading?])]
      (if cell-info-loading?
        [:> Grid {:container true :spacing 4 :alignItems "center" :justifyContent "center"}
         [:> Grid {:item true}
          [:> CircularProgress {:value 200 :color "primary"}]]]
        ;; else
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
              :style {:color "white" :background "#2E3B55" :padding 2 :border-radius 4 :font-size 15 :width 100}} "Next"]]])))
    (when @(rf/subscribe [:show-command])
      [:> Grid {:container true :spacing 4 :alignItems "center" :justifyContent "center"}
       [:> Grid {:item true :xs 8}
        [command-text {:classes classes}]]
       [:> Grid {:item true :xs 2}
        [:> Button
         {:variant "contained"
          :color "primary"
          :disabled false
          :onClick (fn []
                     (get-execution-result))
          :style {:color "white" :background "#2E3B55" :padding 2 :border-radius 4 :font-size 15 :width 100}} "Execute"]]])
    (let [execution-result @(rf/subscribe [:execution-result])
          show-result @(rf/subscribe [:show-result])
          execution-result-loading? @(rf/subscribe [:execution-result-loading?])]
      (if execution-result-loading?
        [:> Grid {:container true :spacing 4 :alignItems "center" :justifyContent "center"}
         [:> Grid {:item true}
          [:> CircularProgress {:value 200 :color "primary"}]]]
        ;; else
        (when (and (not (nil? execution-result)) show-result)
          [:> Grid {:item true :xs 12}
           [results-view {:result execution-result :classes classes}]])))]])

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-styles (reagent/reactify-component command-ui))]])

(comment
  (for [[cell-id cell-info] @(rf/subscribe [:service-cells "IhmHoustonService"])]
   (:selected cell-info))
  @(rf/subscribe [:service-cells :IhmHoustonService])
  (rf/dispatch [:toggle-cell :IhmHoustonService :ihm-dub true])
  (rf/dispatch [:toggle-instance :IhmHoustonService :ihm-dub :id1 false])
  (rf/dispatch [:toggle-instances-view "IhmHoustonService" :ihm-dub])
  (rf/dispatch [:result/toggle-instance :ihm-dub :id1])
  (rf/dispatch [:result/toggle-cell :ihm-dub])
  (rf/dispatch [:show-command])
  @(rf/subscribe [:execution-result])
  (rf/dispatch [:show-result])
  (get-execution-result)
  (rf/dispatch [:cell-info-loading? false])
  @(rf/subscribe [:cell-info-loading?])
  @(rf/subscribe [:selected-service])
  @(rf/subscribe [:service-cells "IhmHoustonService"])
  
  (into {}
    (remove (fn [[_ v]] (empty? v))
      (into {} (for [[cell-id cell-info] @(rf/subscribe [:service-cells :IhmHoustonService])]
                 {cell-id (remove nil? (for [[instance-id instance-info] (:instances cell-info)]
                                           (when (:selected instance-info)
                                             instance-id)))})))))

  

  
   
