(ns sparrow.styles.core
  (:require [sparrow.styles.components :as components]
            [sparrow.styles.constants :as c]
            [sparrow.styles.fonts :as fonts]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-keyframes]]
            [garden.units :refer [px percent ms]]
            [normalize.core :refer [normalize]]))

(defstyles app

  ;; third party css
  normalize

  ;; foundations
  [:*
   {:box-sizing :border-box
    :margin 0
    :padding 0}]

  ;; fonts
  fonts/icomoon


  ;; components
  components/button
  components/calendar
  components/icon
  components/logo
  components/notification
  components/page
  components/sad-message
  components/text
  components/user
  components/user-details)
