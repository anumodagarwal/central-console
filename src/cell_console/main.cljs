(ns cell-console.main
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [cell-console.routes :as routes]
   [cell-console.dashboard :as dashboard]
   [cell-console.subs]
   ["@material-ui/core" :refer [ThemeProvider createTheme]]))


;;; Styles
(defn custom-theme []
  (createTheme (clj->js {:palette {:type "light"}
                         :status {:danger "red"}})))

;;; Views
(defn main-shell [{:keys [router]}]
  (let [current-route @(rf/subscribe [:current-route])]
    [:> ThemeProvider {:theme (custom-theme)}
     [dashboard/page {:router router :current-route current-route}]]))


;;; Core
(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (routes/init-routes!)
  (rdom/render [main-shell {:router routes/router}]
               (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (mount-root))
