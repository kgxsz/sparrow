(ns re-frame-todo.events
  (:require [cljs.spec.alpha :as spec]
            [ajax.core :as ajax]
            [re-frame.core :as re-frame]
            [re-frame-todo.db :as db]
            [re-frame-todo.schema :as schema]))

(def schema-interceptor
  (re-frame/after
   (fn [db]
     (when-not (spec/valid? ::schema/db db)
       (throw (ex-info (str "spec check failed: " (spec/explain-str ::schema/db db)) {}))))))

(re-frame/reg-event-fx
 :initialize-db
 [schema-interceptor]
 (fn [{:keys [db]} event]
   {:db db/default-db
    :query event}))

(re-frame/reg-fx
 :query
 (fn [event]
   (js/console.warn "query for: " event)
   (ajax/POST "https://api.gridr.io"
              {:params {}
               :handler (fn [response] (js/console.warn response))
               :error-handler (fn [response] (js/console.warn "badddd!"))})))

(re-frame/reg-event-fx
 :update-input-value
 [schema-interceptor]
 (fn [{:keys [db]} [_ input-value]]
   {:db (assoc db :input-value input-value)}))

(re-frame/reg-event-fx
 :add-item-to-item-list
 [schema-interceptor]
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
 [schema-interceptor]
 (fn [{:keys [db]} [_ added-at]]
   {:db (update-in db [:items-by-added-at added-at :checked?] not)}))

(re-frame/reg-event-fx
 :delete-item-from-item-list
 [schema-interceptor]
 (fn [{:keys [db]} [_ added-at]]
   {:db (-> db
            (update :items-by-added-at dissoc added-at)
            (update :item-list #(remove (partial = added-at) %)))}))

(re-frame/reg-event-fx
 :toggle-sort-item-by-desc-added-at?
 [schema-interceptor]
 (fn [{:keys [db]} [_ added-at]]
   (let [sort-by-desc-added-at? (not (:sort-by-desc-added-at? db))]
     {:db (-> db
              (assoc :sort-by-desc-added-at? sort-by-desc-added-at?)
              (update :item-list #(sort (if sort-by-desc-added-at? > <) %)))})))
