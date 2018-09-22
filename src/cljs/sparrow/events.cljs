(ns sparrow.events
  (:require [ajax.core :as ajax]
            [re-frame.core :as re-frame]
            [sparrow.interceptors :as interceptors]))


(re-frame/reg-event-fx
 :initialize-db
 [interceptors/schema]
 (fn [{:keys [db]} event]
   {:query [:items]
    :db {:items-by-added-at {}
         :item-list '()
         :input-value ""
         :sort-by-desc-added-at? true}}))


(re-frame/reg-event-fx
 :query-succeeded
 [interceptors/schema]
 (fn [{:keys [db]} [_ query {:keys [items] :as response}]]
   (case (-> query first keyword)
     :items (let [keys (map :added-at items)
                  sort-by-desc-added-at? (:sort-by-desc-added-at? db)]
              {:db (-> db
                       (assoc :items-by-added-at (zipmap keys items))
                       (assoc :item-list (sort (if sort-by-desc-added-at? > <) keys)))})
     (throw Exception.))))


(re-frame/reg-event-fx
 :query-failed
 [interceptors/schema]
 (fn [{:keys [db]} [_ query response]]
   {:db db}))


(re-frame/reg-event-fx
 :command-succeeded
 [interceptors/schema]
 (fn [{:keys [db]} [_ command response]]
   {:db db}))


(re-frame/reg-event-fx
 :command-failed
 [interceptors/schema]
 (fn [{:keys [db]} [_ command response]]
   {:db db}))


(re-frame/reg-event-fx
 :update-input-value
 [interceptors/schema]
 (fn [{:keys [db]} [_ input-value]]
   {:db (assoc db :input-value input-value)}))


(re-frame/reg-event-fx
 :add-item-to-item-list
 [interceptors/schema]
 (fn [{:keys [db]} [_]]
   (let [added-at (.now js/Date)
         text (:input-value db)
         checked? false
         item {:added-at added-at
               :text text
               :checked? checked?}
         sort-by-desc-added-at? (:sort-by-desc-added-at? db)]
     {:command [:add-item added-at text checked?]
      :db (-> db
              (assoc :input-value "")
              (assoc-in [:items-by-added-at added-at] item)
              (update-in [:item-list] (partial cons added-at))
              (update :item-list #(sort (if sort-by-desc-added-at? > <) %)))})))


(re-frame/reg-event-fx
 :toggle-item-checked?
 [interceptors/schema]
 (fn [{:keys [db]} [_ added-at]]
   (let [checked? (not (get-in db [:items-by-added-at added-at :checked?]))]
     {:command [:set-item-checked? added-at checked?]
      :db (assoc-in db [:items-by-added-at added-at :checked?] checked?)})))


(re-frame/reg-event-fx
 :delete-item-from-item-list
 [interceptors/schema]
 (fn [{:keys [db]} [_ added-at]]
   {:command [:delete-item added-at]
    :db (-> db
            (update :items-by-added-at dissoc added-at)
            (update :item-list #(remove (partial = added-at) %)))}))

(re-frame/reg-event-fx
 :toggle-sort-item-by-desc-added-at?
 [interceptors/schema]
 (fn [{:keys [db]} [_ added-at]]
   (let [sort-by-desc-added-at? (not (:sort-by-desc-added-at? db))]
     {:db (-> db
              (assoc :sort-by-desc-added-at? sort-by-desc-added-at?)
              (update :item-list #(sort (if sort-by-desc-added-at? > <) %)))})))
