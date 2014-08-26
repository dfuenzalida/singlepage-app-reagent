(ns singlepage-app-om.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST DELETE]]))

(enable-console-print!)

;; global state
(def app-state (atom {:alert nil
                      :items nil
                      :text "Hello world!"}))

;; Set the alert message
(om/root
  (fn [app owner]
    (reify om/IRender
      (render [_]
        (when (:alert app)
          (dom/div #js {:className "row alert alert-info"} (:alert app))))))
  app-state
  {:target (. js/document (getElementById "alert"))})

;; app
(om/root
  (fn [app owner]
    (reify om/IRender
      (render [_]
        (dom/h1 nil (:text app)))))
  app-state
  {:target (. js/document (getElementById "app"))})

(defn set-alert [message]
  (swap! app-state assoc :alert message))

(defn error-handler [response]
  (.log js/console (str "ERROR: " response)))

(defn load-items []
  (GET "/api/items" {:handler error-handler
               :error-handler error-handler}))


(.setTimeout js/window (fn [] (set-alert "Loaded from core.cljs! ... closing in 5...")) 1000)
(.setTimeout js/window (fn [] (set-alert nil)) 6000)
(load-items)
