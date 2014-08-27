(ns singlepage-app-om.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST DELETE]]))

(enable-console-print!)

;; helper

(defn by-id [elem-id]
  (.getElementById js/document elem-id))

;; global state
(def state-message (atom nil))
(def state-items (atom nil))

(defn set-alert [message]
  (reset! state-message message))

(defn error-handler [response]
  (.log js/console (str "ERROR: " response)))

(declare load-items)
(declare add-item)

(defn show-message []
  (when @state-message
    [:div.row.alert.alert-info @state-message]))

(defn delete-item [id]
  (when (js/confirm "Sure?")
    (DELETE (str "/api/items/" id)
            {:handler load-items
             :error-handler error-handler
             :format :json
             :response-format :json
             :keywords? true})))

(defn create-item [name desc]
  (POST "/api/items"
        {:params {:name name :description desc}
         :headers {:api-token "SPECIAL-TOKEN-HERE"}
         :handler load-items
         :error-handler error-handler
         :format :json
         :response-format :json
         :keywords? true}))

(defn show-items []
  (when @state-items
    [:div
     [:div.row
      [:h3 "List of Items - Reagent"]]
     [:div.row
      [:table.table.table-hover.table-condensed
       [:thead
        [:tr
         [:th "#"] [:th "Name"] [:th "Description"] [:th "Actions"]]]
       [:tbody
        (for [item @state-items]
          ^{:key (item "id")}
          [:tr
           [:td (item "id")] [:td (item "name")] [:td (item "description")]
           [:td [:a.btn.btn-default.btn-sm {:id (str "delete-" (item "id"))
                                            :onClick (fn [] (delete-item (item "id")))}
                 [:span.glyphicon.glyphicon-remove] " delete"]]
           ])]]]

     [:div.row
      [:h3 "Add a new item"]
      [:div.row
       [:form.form-inline {:id "new-item" :role "form"}
        [:div.form-group [:label.sr-only {:for "new-name"} "Name"]
         [:input#new-name.form-control {:type "text" :name "name" :placeholder "name"}]]
        [:div.form-group [:label.sr-only {:for "new-desc"} "Description"]
         [:input#new-desc.form-control {:type "text" :name "description" :placeholder "descripti
on"}]]
        [:button.btn.btn-primary {:id "submit-btn" :onClick add-item :type "submit"} "Submit"]]]]
     ]))

(defn load-items []
  (GET "/api/items" {:handler (fn [items] (reset! state-items items))
               :error-handler error-handler}))

(defn add-item [event]
  (let [name (.-value (by-id "new-name"))
        desc (.-value (by-id "new-desc"))]
    (.preventDefault event)
    (.log js/console "name:" name ", description:" desc)
    (when (not= 0 (count name) (count desc))
      (aset (by-id "new-name") "value" "")
      (aset (by-id "new-desc") "value" "")
      (create-item name desc))))

(reagent/render-component (fn [] [show-message]) (by-id "alert"))
(reagent/render-component (fn [] [show-items]) (by-id "app"))

(.setTimeout js/window (fn [] (set-alert "Loaded from core.cljs! ... closing in 3 seconds...")) 1)
(.setTimeout js/window (fn [] (set-alert nil)) 3000)
(.setInterval js/window load-items 3000) ;; refresh every 3 seconds
(load-items)
