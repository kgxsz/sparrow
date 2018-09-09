(ns sparrow.handler
  (:require [cheshire.core :refer [generate-stream parse-stream generate-string]]
            [clojure.java.io :as io])
  (:import [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:gen-class
   :name sparrow.Handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn -handleRequest
  [_ input-stream output-stream context]
  (with-open [writer (io/writer output-stream)]
    (let [response {:statusCode 200
                    :headers {}
                    :body (generate-string {:hello "world"})}]
      (generate-stream response writer))))

; Look into adding an item to a DDB table every time you call the endpoint
; Split endpoints out into query and command, with post payloads
; Tie query and command to two separate dummy tasks, mess around with postman
; See about kicking a static webpage using Finch [DONE]
; Setup https with app.gridr.io
; basic todo list using existing re-frame application working
