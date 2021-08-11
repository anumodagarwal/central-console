(ns cell-console.views.execute-commands
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [cell-console.subs]
   ["@material-ui/core" :refer [Paper Collapse Grid Typography Container List ListItem ListItemText ListItemIcon Checkbox] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/SlowMotionVideo" :default SlowMotionVideoIcon]
   ["@material-ui/icons/ExpandLess" :default ExpandLess]
   ["@material-ui/lab" :refer [Autocomplete]]))

;;; Styles
(defn command-ui-styles [^js/Mui.Theme theme]
  (clj->js
   {:icon {:marginRight (.spacing theme 2)}
    :heroContent {:backgroundColor (.. theme -palette -background -paper)
                  :padding (.spacing theme 8 0 6)}
    :paper {:margin (.spacing theme 2 0)
            :padding (.spacing theme 4 4)
            :display "flex"
            :overflow "auto"
            :flexDirection "column"}
    :footer {:backgroundColor (.. theme -palette -background -paper)
             :padding (.spacing theme 6)}}))

(def with-styles (withStyles command-ui-styles))


;; Components
(defn drawer-icon []
  [:> SlowMotionVideoIcon])

(defn cell-list []
  (let [cells ["Cell 1" "Cell 2" "Cell 3"]]
    [:> Paper
     [:> ListItem {:button true}
      [:> ListItemIcon
       [:> Checkbox
          {:checked false}]]
      [:> ListItemText {:primary "Cell 1"}]
      [:> ExpandLess]]
     [:> Collapse {:in true :timeout "auto" :unmountonexit "true"}
      [:> List {:disablePadding true}
       [:> ListItem {:button true}
        [:> ListItemIcon
         [:> Checkbox
            {:checked false}]]
        [:> ListItemText {:primary "Hello"}]]]]]))

(defn search-bar [{:keys [^js classes]}]
  (let [services @(rf/subscribe [:services])]
   [:> Grid {:item true :xs 12}
    [:> Paper {:class (.-paper classes)}
     [:> Autocomplete 
      {:options services
       :fullWidth false
       :on-change (fn [e value]
                    (rf/dispatch [:selected-service (if value value "")]))
       :render-input (fn [^js params]
                       (set! (.-variant params) "outlined")
                       (set! (.-label params) "Select service")
                       (reagent/create-element mui/TextField params))}]]]))

(defn command-ui [{:keys [^js classes] :as props}]
  [:<>
   [:div {:class (.-heroContent classes)}
    [:> Container {:maxWidth "sm"}
     [:> Typography {:component "h3" :variant "h3" :align "center"
                     :color "textPrimary" :gutterBottom true}
      "Execute Commands Across Cells"]]]
   [:> Container {:class (.-cardGrid classes) :maxWidth "md"}
    [:> Grid {:container true :spacing 4}
     [search-bar {:classes classes}]]
    (when-not (= @(rf/subscribe [:selected-service]) "")
     [:> Grid {:item true :xs 4}
      [cell-list]])]])

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-styles (reagent/reactify-component command-ui))]])

(comment
  @(rf/subscribe [:selected-service])
  (rf/dispatch [:selected-service "IhmLibby"])
  (if 1 1 ""))