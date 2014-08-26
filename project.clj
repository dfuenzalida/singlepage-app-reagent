(defproject singlepage-app-om "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.7.1"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src", "src-cljs"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src-cljs"]
              :compiler {
                :output-to "resources/public/js/app.js"
                :output-dir "resources/public/js/out"
                :optimizations :none
                :source-map true}}]})
