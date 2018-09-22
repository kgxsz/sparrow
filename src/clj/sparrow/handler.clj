(ns sparrow.handler
  (:require [cheshire.core :as cheshire]
            [taoensso.faraday :as faraday]
            [clojure.java.io :as io])
  (:import [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:gen-class
   :name sparrow.Handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))


(def ddb-config {:access-key (System/getenv "ACCESS_KEY")
                 :secret-key (System/getenv "SECRET_KEY")
                 :endpoint "http://dynamodb.eu-west-1.amazonaws.com"})

(def table-name "sparrow-items")


(defmulti handle-query (comp keyword first))

(defmethod handle-query :items [query]
  {:items (faraday/scan ddb-config table-name)})

(defmethod handle-query :default [query]
  (throw (Exception.)))


(defmulti handle-command (comp keyword first))

(defmethod handle-command :add-item [[_ added-at text checked?]]
  (let [n (count (faraday/scan ddb-config table-name))
        item {:added-at added-at :text text :checked? checked?}]
    (when (< n 10)
      (faraday/put-item ddb-config table-name item)))
  {})

(defmethod handle-command :delete-item [[_ added-at]]
  (faraday/delete-item ddb-config table-name {:added-at added-at})
  {})

(defmethod handle-command :set-item-checked? [[_ added-at checked?]]
  (faraday/update-item ddb-config table-name {:added-at added-at} {:update-map {:checked? [:put checked?]}})
  {})

(defmethod handle-command :default [command]
  (throw (Exception.)))


(defn read-input [reader]
  (let [{:keys [body]} (cheshire/parse-stream reader true)
        {:keys [query command]} (cheshire/parse-string body true)]
    {:query query :command command}))


(defn write-output [writer status-code body]
  (let [response {:statusCode status-code
                  :headers {"Access-Control-Allow-Origin" "*"}
                  :body (cheshire/generate-string body)}]
    (cheshire/generate-stream response writer)))


(defn -handleRequest
  [_ input-stream output-stream context]
  (with-open [reader (io/reader input-stream)
              writer (io/writer output-stream)]
    (try
      (let [{:keys [query command]} (read-input reader)]
        (write-output writer 200 (or (some-> query handle-query)
                                     (some-> command handle-command)
                                     (throw (Exception.)))))
      (catch Exception e
        (write-output writer 500 {})))))

; Limit on ten items and return error
