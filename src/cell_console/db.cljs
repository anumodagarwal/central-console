(ns cell-console.db)

(def app-db
  {:drawer/open? true
   :services ["IhmHoustonService" "IhmLibbyService" "IhmClaireService"]
   :command nil
   :cell-info-loading? false
   :execution-result-loading? false
   :selected-service nil
   :show-cell-info false
   :show-command false
   :show-result false
   :cell-info nil
   :execution-result nil})

(comment
  (for [[_ instance-info] (get-in app-db [:cell-info "IhmHoustonService" :ihm-dub :instances])]
   (:selected instance-info))
  (clj->js (get app-db :cell-info)))