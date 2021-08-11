(ns cell-console.views.home
  (:require
   [reagent.core :as reagent]
   ["@material-ui/core" :refer [Typography Container] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
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
   [:div {:class (.-heroContent classes)}
    [:> Container {:maxWidth "sm"}
     [:> Typography {:component "h2" :variant "h2" :align "center"
                     :color "textPrimary" :gutterBottom true}
      "Welcome to Central Cell Console"]
     [:> Typography {:variant "h5" :align "center" :color "textSecondary" :paragraph true}
      "Here you can perform multiple operations."]]]])

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-showcase-styles (reagent/reactify-component showcase))]])
