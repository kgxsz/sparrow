(ns sparrow.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [sparrow.config :as config]
            [sparrow.effects :as effects]
            [sparrow.events :as events]
            [sparrow.subscriptions :as subscriptions]
            [sparrow.views :as views]))


(defn dev-setup []
  (enable-console-print!)
  (js/console.warn "dev mode"))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/app]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (when config/debug? (dev-setup))
  (mount-root))
