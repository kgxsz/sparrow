(ns sparrow.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [sparrow.effects :as effects]
            [sparrow.events :as events]
            [sparrow.subscriptions :as subscriptions]
            [sparrow.views :as views]))


(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/app]
                  (.getElementById js/document "root")))


(defn ^:export initialise []
  (re-frame/dispatch-sync [:initialise])
  (enable-console-print!)
  (mount-root))
