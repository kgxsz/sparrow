(ns sparrow.handler
  (:require [cheshire.core :refer [generate-stream parse-stream generate-string]]
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
          tables (faraday/describe-table ddb-config "sparrow-users")
          _      (faraday/put-item ddb-config "sparrow-users" {:email (str "k.suzukawa+" (rand-int 99999999) "@gmail.com")
                                                            :foo {:bar "hello"}})
          response {:statusCode 200
                    :headers {}
                    :body (generate-string {:hello "world"
                                            :tables tables})}]
      (generate-stream response writer))))

; Look into adding an item to a DDB table every time you call the endpoint [DONE]
; Split endpoints out into query and command, with post payloads
; Tie query and command to two separate dummy tasks, mess around with postman
; See about kicking a static webpage using Finch [DONE]
; Setup https with app.gridr.io
; basic todo list using existing re-frame application working


; Manual steps:
; Have to specify region separately as an endpoint to Faraday
; Can use an env var for the table defined in the serverless.yml
; There's possibly out of the box executions
; Anybody can hit the endpoint
; Have to hook up the custom domain manually
; Gotta unhook the base path mapping manually before tearing down
; consistent naming throughout? Don't care about stages, hide them
