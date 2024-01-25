(ns duct-todo-front.views
  (:require
   [duct-todo-front.events :as events]
   [duct-todo-front.subs :as subs]
   [fork.reagent :as fork]
   [re-frame.core :as re-frame]))

(defmulti view :handler)

(defmethod view ::home [_]
  [:div "Home"
   [:ul
    [:li [:button {:on-click #(re-frame/dispatch [::events/navigate ::list])} "View"]]
    [:li [:button {:on-click #(re-frame/dispatch [::events/navigate ::create])} "Create"]]]])

(defmethod view ::list [_]
  [:div "Todo List"
   [:ul
    (map (fn [{:keys [id title]}]
           [:li {:key id}
            title
            [:button {:on-click #(re-frame/dispatch [::events/navigate ::edit {:id id}])} "Edit"]
            [:button {:on-click #(re-frame/dispatch [::events/delete-todo-by-id id])} "Delete"]])
         @(re-frame/subscribe [::subs/todos]))]])

(defn todo-form [props]
  [fork/form (merge {:prevent-default? true
                     :clean-on-unmount? true}
                    props)
  (fn [{:keys [values
              form-id
              handle-blur
              handle-change
              submitting?
              handle-submit]}]
    [:form {:id form-id
            :on-submit handle-submit}
     [:label "Title"]
     [:input {:name "title"
              :value (values "title")
              :on-change handle-change
              :on-blur handle-blur}]
     [:button {:type "submit"
               :disabled submitting?}
      "Submit"]]) ])

(defmethod view ::create [_]
  [:div "Create New Todo"
   [todo-form {:on-submit #(re-frame/dispatch [::events/create-todo %])}]])

(defmethod view ::edit [{:keys [route-params]}]
  [:div (str "Edit Todo " (:id route-params))
   (when-let [{:keys [id title]} @(re-frame/subscribe [::subs/selected-todo])]
     [todo-form {:initial-values {"title" title}
                 :on-submit #(re-frame/dispatch [::events/update-todo-by-id id %])}])])

(defmethod view :default [_]
  [:div "404 Not Found"])

(defn main-panel []
  [:div
   [:div {:on-click #(re-frame/dispatch [::events/navigate ::home])} "Todo App"]
   [view @(re-frame/subscribe [::subs/current-route])]])

