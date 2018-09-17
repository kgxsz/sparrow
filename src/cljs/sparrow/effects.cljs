(ns sparrow.effects
  (:require [ajax.core :as ajax]
            [re-frame.core :as re-frame]))


(re-frame/reg-fx
 :query
 (fn [[query-type & args]]
   (ajax/POST "https://api.gridr.io"
              {:params {}
               :handler (fn [response] (re-frame/dispatch [:query-succeeded query-type response]))
               :error-handler (fn [response] (re-frame/dispatch [:query-failed query-type response]))
               :response-format :json
               :keywords? true})))
