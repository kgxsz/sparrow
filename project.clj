(defproject sparrow "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [com.amazonaws/aws-lambda-java-core "1.0.0"]
                 [com.taoensso/faraday "1.9.0"]
                 [cheshire "5.8.0"]]

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.16"]
                                  [cider/piggieback "0.3.9"]]
                   :plugins [[lein-figwheel "0.5.16"]
                             [lein-pdo "0.1.1"]]
                   :figwheel {:css-dirs ["resources/public/css"]}}

             :uberjar {:aot :all
                       :uberjar-name "sparrow.jar"}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "re-frame-todo.core/mount-root"}
                        :compiler {:main re-frame-todo.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install :all}}}}
                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main re-frame-todo.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}]})
