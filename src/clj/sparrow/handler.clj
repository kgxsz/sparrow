(ns sparrow.handler
  (:require [cheshire.core :as cheshire]
            [taoensso.faraday :as faraday]
            [clojure.java.io :as io])
  (:import [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:gen-class
   :name sparrow.Handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn -handleRequest
  [_ input-stream output-stream context]
  (with-open [writer (io/writer output-stream)]
    (let [ddb-config {:access-key (System/getenv "ACCESS_KEY")
                      :secret-key (System/getenv "SECRET_KEY")
                      :endpoint "http://dynamodb.eu-west-1.amazonaws.com"}
          #__      #_(faraday/put-item ddb-config "sparrow-items" (let [added-at (rand-int 9999999)]
                                                                {:added-at added-at
                                                                 :text (str "item - " added-at)
                                                                 :checked? false}))
          items (faraday/scan ddb-config "sparrow-items")
          response {:statusCode 200
                    :headers {"Access-Control-Allow-Origin" "*"}
                    :body (cheshire/generate-string {:items items})}]
      (cheshire/generate-stream response writer))))

; Look into adding an item to a DDB table every time you call the endpoint [DONE]
; Split endpoints out into query and command, with post payloads
; Tie query and command to two separate dummy tasks, mess around with postman
; See about kicking a static webpage using Finch [DONE]
; Setup https with app.gridr.io
; basic todo list using existing re-frame application working
; rename subs to subscriptions
; rename folder structure [DONE]
; make a side-effects file


; Manual steps:
; There's possibly out of the box executions
; Anybody can hit the endpoint
; Have to hook up the custom domain manually
; Gotta unhook the base path mapping manually before tearing down
; consistent naming throughout? Don't care about stages, hide them
