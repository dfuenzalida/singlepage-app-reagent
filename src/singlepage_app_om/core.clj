(ns singlepage-app-om.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [clojure.data.json :as json])
  (:use [compojure.core :only [defroutes GET POST DELETE]]
        [ring.middleware.file-info :only [wrap-file-info]]
        [ring.middleware.json :only [wrap-json-response wrap-json-body]]
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.resource :only [wrap-resource]]
        [ring.middleware.session :only [wrap-session]]))

;; DATABASE ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def items
  (atom [{:id 1 :name "backend 1" :description "backend descr"}
         {:id 2 :name "clojure items" :description "from clj"}
         {:id 3 :name "item #301" :description "descr #301"}]))

(def primary-key (->> @items (map :id) (reduce max) atom))

(defn new-pk! [] (swap! primary-key inc))

(defn new-item! [{:keys [name description] :as params}]
  (swap! items conj {:id (new-pk!) :name name :description description}))

(defn delete-item! [id]
  (let [new-items (filter #(not= (str id) (str (:id %))) @items)]
    (reset! items new-items)))

(defn sorted-items []
  (sort #(compare (:id %1) (:id %2)) @items))

;; ROUTES ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes routes

  (GET "/" []
       {:status 200
        :headers {"Content-type" "text/html", "myfoo" "barbarbar"}
        :body (slurp "resources/public/index.html")})

  (GET "/api/items" []
       {:status 200 :body (sorted-items)})

  (POST "/api/items" request
        (let [items (new-item! (:body request))]
          {:status 200 :body (last (sorted-items))}))

  (DELETE "/api/items/:id" [id]
          (delete-item! id)
          {:status 200})
  )

;; APP ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn wrap-logging [handler]
  (fn [{:keys [remote-addr request-method uri] :as request}]
    (println remote-addr (.toUpperCase (name request-method)) uri)
    (handler request)))

(def app (-> routes
             (wrap-resource "public") ;; serve from "resources/public"
             (wrap-json-body {:keywords? true})
             wrap-json-response
             wrap-params
             wrap-file-info ;; sends the right headers for static files
             ;; wrap-session
             wrap-logging))

(defn -main [& args]
  (let [port 3000]
    (println "Starting server on port" port)
    (jetty/run-jetty app {:port port})))
