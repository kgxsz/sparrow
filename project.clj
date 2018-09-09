(defproject sparrow "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.amazonaws/aws-lambda-java-core "1.0.0"]
                 [com.taoensso/faraday "1.9.0"]
                 [cheshire "5.8.0"]]

  :source-paths ["src/clj"]

  :profiles {:uberjar {:aot :all
                       :uberjar-name "sparrow.jar"}})
