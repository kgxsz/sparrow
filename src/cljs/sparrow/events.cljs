(ns sparrow.events
  (:require [ajax.core :as ajax]
            [medley.core :as medley]
            [re-frame.core :as re-frame]
            [sparrow.interceptors :as interceptors]))


(re-frame/reg-event-fx
 :initialize-db
 [interceptors/schema]
 (fn [{:keys [db]} event]
   {:query [:items]
    ;; TODO - use an initialising flag here
    :db {:items-by-added-at {}
         :item-list '()
         :input-value ""
         :sort-by-desc-added-at? true}}))

(re-frame/reg-event-fx
 :query-succeeded
 [interceptors/schema]
 (fn [{:keys [db]} [_ query-type {:keys [items] :as response}]]
   (js/console.warn items)
   (let [keys (map :added-at items)
         sort-by-desc-added-at? (:sort-by-desc-added-at? db)]
     {:db (-> db
              (assoc :items-by-added-at (zipmap keys items))
              (assoc :item-list (sort (if sort-by-desc-added-at? > <) keys)))})))


(re-frame/reg-event-fx
 :query-failed
 [interceptors/schema]
 (fn [{:keys [db]} [_ query-type response]]
   (js/console.warn response)
   {:db db #_(assoc db :input-value input-value)}))

;; TODO - put this in its own file
(re-frame/reg-fx
 :query
 (fn [[query-type & args]]
   (ajax/POST "https://api.gridr.io"
              {:params {}
               :handler (fn [response] (re-frame/dispatch [:query-succeeded query-type response]))
               :error-handler (fn [response] (re-frame/dispatch [:query-failed query-type response]))
               :response-format :json
               :keywords? true})))

(re-frame/reg-event-fx
 :update-input-value
 [interceptors/schema]
 (fn [{:keys [db]} [_ input-value]]
   {:db (assoc db :input-value input-value)}))

(re-frame/reg-event-fx
 :add-item-to-item-list
 [interceptors/schema]
 (fn [{:keys [db]} [_ added-at]]
   (let [item {:added-at added-at
               :text (:input-value db)
               :checked? false}
         sort-by-desc-added-at? (:sort-by-desc-added-at? db)]
     {:db (-> db
              (assoc :input-value "")
              (assoc-in [:items-by-added-at added-at] item)
              (update-in [:item-list] (partial cons added-at))
              (update :item-list #(sort (if sort-by-desc-added-at? > <) %)))})))

(re-frame/reg-event-fx
 :toggle-item-checked?
 [interceptors/schema]
 (fn [{:keys [db]} [_ added-at]]
   {:db (update-in db [:items-by-added-at added-at :checked?] not)}))

(re-frame/reg-event-fx
 :delete-item-from-item-list
 [interceptors/schema]
 (fn [{:keys [db]} [_ added-at]]
   {:db (-> db
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
