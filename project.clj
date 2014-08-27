(defproject singlepage-app-om "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [
                 ;; server side
                 [org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.1"]
                 [ring/ring-devel "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.1.8"]
                 [org.clojure/data.json "0.2.5"]
                 [http-kit "2.0.0"]

                 ;; client side
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [whoops/reagent "0.4.3"]
                 [cljs-ajax "0.2.6"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src", "src-cljs"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src-cljs"]
              :compiler {
                :output-to "resources/public/js/app.js"
                :output-dir "resources/public/js/out"
                :optimizations :none
                :source-map true}}]}

  :main singlepage-app-om.core)
