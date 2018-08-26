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
                    :headers {"Access-Control-Allow-Origin" "*"}
                    :body (generate-string {:hello "world"})}]
      (generate-stream response writer))))
