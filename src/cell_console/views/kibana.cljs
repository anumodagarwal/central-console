(ns cell-console.views.kibana
 (:require ["@material-ui/icons/Assignment" :default AssignmentIcon]
           ["react-iframe" :default Iframe]))


(defn drawer-icon []
  [:> AssignmentIcon])

(defn main []
 [:> Iframe 
    {:url "https://search-central-log-visualizer-2-rcz2qqcgvzziv64cyfsfob67ei.us-west-2.es.amazonaws.com/_plugin/kibana/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-15m,to:now))&_a=(columns:!(_source),filters:!(),index:'1fb78dc0-fac0-11eb-ab7e-d72fcc10b058',interval:auto,query:(language:kuery,query:''),sort:!())"
     :width "100%"
     :height "100%"
     :referrerPolicy "origin-when-cross-origin"}])