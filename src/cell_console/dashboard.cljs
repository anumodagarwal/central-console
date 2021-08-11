(ns cell-console.dashboard
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]
   [reitit.core :as reitit]
   [cell-console.events]
   ["@material-ui/core/AppBar" :default AppBar]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core/Divider" :default Divider]
   ["@material-ui/core/Drawer" :default Drawer]
   ["@material-ui/core/IconButton" :default IconButton]
   ["@material-ui/core/List" :default List]
   ["@material-ui/core/ListItem" :default ListItem]
   ["@material-ui/core/ListItemIcon" :default ListItemIcon]
   ["@material-ui/core/ListItemText" :default ListItemText]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/Menu" :default MenuIcon]
   ["@material-ui/icons/ChevronLeft" :default ChevronLeftIcon]
   ["@material-ui/core/Toolbar" :default Toolbar]
   ["@material-ui/core/Typography" :default Typography]
   ["@material-ui/icons/Description" :default DescriptionIcon]
   ["@material-ui/icons/AccountCircle" :default AccountCircleIcon]))
   

(def drawer-width 240)

(defn dashboard-styles [^js/Mui.Theme theme]
  (clj->js
   {:root {:display "flex"}
    :toolbar {:paddingRight 24}  ; keep right padding when drawer closed
    :toolbarIcon (merge {:display "flex"
                         :alignItems "center"
                         :justifyContent "flex-end"
                         :padding "0 8px"}
                        (js->clj (.. theme -mixins -toolbar)))
    :appBar {:zIndex (+ (.. theme -zIndex -drawer) 1)
             :background "#2E3B55"
             :transition (.. theme -transitions
                             (create #js ["width" "margin"]
                                     #js {:easing (.. theme -transitions -easing -sharp)
                                          :duration (.. theme -transitions -duration -leavingScreen)}))}
    :appBarShift {:marginLeft drawer-width
                  :width (str "calc(100% - " drawer-width "px)")
                  :transition (.. theme -transitions
                                  (create #js ["width" "margin"]
                                          #js {:easing (.. theme -transitions -easing -sharp)
                                               :duration (.. theme -transitions -duration -enteringScreen)}))}
    :menuButton {:marginRight 36}
    :menuButtonHidden {:display "none"}
    :title {:flexGrow 1}
    :drawerPaper {:position "relative"
                  :whiteSpace "nowrap"
                  :width drawer-width
                  :transition (.. theme -transitions
                                  (create "width"
                                          #js {:easing (.. theme -transitions -easing -sharp)
                                               :duration (.. theme -transitions -duration -enteringScreen)}))}
    :drawerPaperClose {:overflowX "hidden"
                       :transition (.. theme -transitions
                                       (create "width"
                                               #js {:easing (.. theme -transitions -easing -sharp)
                                                    :duration (.. theme -transitions -duration -leavingScreen)}))
                       :width (.spacing theme 7)
                       (.breakpoints.up theme "sm") {:width (.spacing theme 9)}}
    :appBarSpacer (.. theme -mixins -toolbar)
    :content {:flexGrow 1
              :height "100vh"
              :overflow "auto"}
    :container {:paddingTop (.spacing theme 4)
                :paddingBottom (.spacing theme 4)}
    :paper {:padding (.spacing theme 4)
            :display "flex"
            :overflow "auto"
            :flexDirection "column"}
    :fixedHeight {:height 240}}))

(def with-dashboard-styles (withStyles dashboard-styles))


;;; Components
(defn list-item [{:keys [selected route-name text icon]}]
  [:> ListItem {:button true
                :selected selected
                :on-click #(rfe/push-state route-name)}
   [:> ListItemIcon [icon]]
   [:> ListItemText {:primary text}]])


(defn dashboard [{:keys [router current-route]}]
  (fn [{:keys [^js classes] :as props}]
    (let [open? @(rf/subscribe [:drawer/open?])]
      [:div {:class (.-root classes)}
       [:> CssBaseline]

       [:> AppBar {:position "absolute"
                   :class [(.-appBar classes)
                           (when open? (.-appBarShift classes))]}
        [:> Toolbar {:class (.-toolbar classes)}
         [:> IconButton {:edge "start"
                         :color "inherit"
                         :aria-label "open drawer"
                         :on-click #(rf/dispatch [:drawer/open])
                         :class [(.-menuButton classes) (when open? (.-menuButtonHidden classes))]}
          [:> MenuIcon]]
         [:> Typography {:component "h1"
                         :variant "h6"
                         :color "inherit"
                         :no-wrap true
                         :class (.-title classes)}
          "Central Cell Operational Console"]]]

       [:> Drawer {:variant "permanent"
                   :classes {:paper (str (.-drawerPaper classes) " "
                                         (if open? "" (.-drawerPaperClose classes)))}
                   :open open?}
        [:div {:class (.-toolbarIcon classes)}
         [:> IconButton {:on-click #(rf/dispatch [:drawer/close])}  ; Close drawer
          [:> ChevronLeftIcon]]]
        [:> Divider]
        [:> List
         [:> ListItem {:button true
                       :selected false
                       :on-click #(. js/window (open "https://search-central-log-visualizer-2-rcz2qqcgvzziv64cyfsfob67ei.us-west-2.es.amazonaws.com/_plugin/kibana/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-15m,to:now))&_a=(columns:!(_source),filters:!(),index:'1fb78dc0-fac0-11eb-ab7e-d72fcc10b058',interval:auto,query:(language:kuery,query:''),sort:!())"))}
          [:> ListItemIcon [:> DescriptionIcon]]
          [:> ListItemText {:primary "Logs"}]]]
        [:> Divider]
        [:> List
         (for [route-name (reitit/route-names router)
               :let [route (reitit/match-by-name router route-name)
                     text (-> route :data :link-text)
                     icon (-> route :data :icon)
                     selected? (= route-name (-> current-route :data :name))]]
           ^{:key route-name} [list-item {:text text
                                          :icon icon
                                          :route-name route-name
                                          :selected selected?}])]
        [:> List {:style {:marginTop "auto"}}
         [:> ListItem {:button true
                       :on-click #(.open js/window "https://phonetool.amazon.com/users/saidev")}
          [:> ListItemIcon [:> AccountCircleIcon]]
          [:> ListItemText {:primary "saidev"}]]]]
       [:main {:class (.-content classes)}
        [:div {:class (.-appBarSpacer classes)}]
        (when current-route
          [(-> current-route :data :view) {:classes classes}])]])))

(defn page [{:keys [router current-route]}]
  [:> (with-dashboard-styles
        (reagent/reactify-component
         (dashboard {:router router :current-route current-route})))])
