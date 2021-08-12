(ns cell-console.db)

(def app-db
  {:drawer/open? true
   :services ["IhmHoustonService" "IhmLibbyService" "IhmClaireService"]
   :selected-service ""
   :show-cell-info false
   :show-command false
   :show-result false
   :execution-result
     {:ihm-dub
       {:name "IHM-DUB"
        :instances-open false
        :instances
         {:id1
           {:result-open false
            :result "Hello this is a text"
            :name "id1"}
          :id2
           {:name "id2"
            :result-open false
            :result "Hello this is a text"}}}
      :ihm-pdx
       {:name "IHM-PDX"
        :instances-open false
        :instances
         {:id1
           {:result-open false
            :result "Hello this is a text"
            :name "id1"}
          :id2
           {:name "id2"
            :result-open false
            :result "Hello this is a text"}}}}
   :cell-info 
    {"IhmHoustonService"
      {:ihm-dub
        {:selected false
         :name "IHM-DUB"
         :instances-open false
         :instances
          {:id1
            {:selected false
             :name "id1"}
           :id2
            {:selected false
             :name "id2"}}}
       :ihm-pdx
        {:selected false
         :name "IHM-PDX"
         :instances-open false
         :instances
          {:id1
            {:selected false
             :name "id1"}
           :id2
            {:selected false
             :name "id2"}}}}}})
         
         
         
(comment
  (for [[_ instance-info] (get-in app-db [:cell-info "IhmHoustonService" :ihm-dub :instances])]
   (:selected instance-info))
  (clj->js (get app-db :cell-info)))