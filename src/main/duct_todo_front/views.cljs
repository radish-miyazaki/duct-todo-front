(ns duct-todo-front.views
  (:require
   ["@mui/icons-material/Add$default" :as AddIcon]
   ["@mui/icons-material/Delete$default" :as DeleteIcon]
   ["@mui/icons-material/Save$default" :as SaveIcon]
   ["@mui/icons-material/Work$default" :as WorkIcon]
   ["@mui/material" :refer [AppBar Avatar Breadcrumbs Button Container Dialog
                            DialogActions DialogTitle Fab IconButton Link List
                            ListItem ListItemAvatar ListItemSecondaryAction ListItemText TextField Toolbar
                            Typography]]
   [duct-todo-front.events :as events]
   [duct-todo-front.routes :as routes]
   [duct-todo-front.subs :as subs]
   [fork.reagent :as fork]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]))

(defmulti view :handler)

(defmethod view ::list [_]
  [:div
   [:p "Todo List"]
   [:> List
    (map (fn [{:keys [id title]}]
           ^{:key id}
           [:> ListItem {:button true
                         :on-click #(re-frame/dispatch [::events/navigate ::edit {:id id}])}
            [:> ListItemAvatar
             [:> Avatar
              [:> WorkIcon]]]
            [:> ListItemText {:primary title}]
            [:> ListItemSecondaryAction
             [:> IconButton {:edge "end"
                             :aria-label "delete"
                             :on-click #(re-frame/dispatch [::events/open-delete-dialog id])}
              [:> DeleteIcon]]]])
         @(re-frame/subscribe [::subs/todos]))]
   [:> Dialog {:open @(re-frame/subscribe [::subs/delete-dialog-open?])
               :on-close #(re-frame/dispatch [::events/close-delete-dialog])
               :aria-labelledby "alert-dialog-title"}
    [:> DialogTitle {:id "alert-dialog-title"}
     "Are you sure you want to delete this?"]
    [:> DialogActions
     [:> Button {:on-click #(re-frame/dispatch [::events/close-delete-dialog])}
      "Cancel"]
     [:> Button {:on-click (fn []
                             (re-frame/dispatch [::events/delete-todo-by-id
                                                 @(re-frame/subscribe [::subs/delete-target])])
                             (re-frame/dispatch [::events/close-delete-dialog]))
                 :variant "contained"
                 :color "error"}
      "Delete"]]]
    [:> Fab {:color "primary"
             :aria-label "add"
             :on-click #(re-frame/dispatch [::events/navigate ::create])}
     [:> AddIcon]]])

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
     [:> TextField {:label "Title"
                    :required true
                    :name "title"
                    :default-value (values "title")
                    :on-change handle-change
                    :on-blur handle-blur}]
     [:> Button {:type "submit"
                 :disabled submitting?
                 :variant "contained"
                 :start-icon (reagent/as-element [:> SaveIcon])}
      "Save"]]) ])

(defmethod view ::create [_]
  [:div
   [:p "Create New Todo"]
   [todo-form {:on-submit #(re-frame/dispatch [::events/create-todo %])}]])

(defmethod view ::edit [{:keys [route-params]}]
  [:div
   [:p (str "Edit Todo " (:id route-params))]
   (when-let [{:keys [id title]} @(re-frame/subscribe [::subs/selected-todo])]
     [todo-form {:initial-values {"title" title}
                 :on-submit #(re-frame/dispatch [::events/update-todo-by-id id %])}])])

(defmethod view :default [_]
  [:div
   [:p "404 Not Found"]])

(defn main-panel []
  (let [{:keys [handler] :as current-route} @(re-frame/subscribe [::subs/current-route])]
    [:div
     [:> AppBar {:position "static"}
      [:> Toolbar
       [:> Breadcrumbs {:aria-label "breadcrum"}
        [:> Link {:color "white"
                  :variant "h6"
                  :underline "none"
                  :href (routes/path-for ::list)}
         "TODOS"]
        (case handler
          ::create [:> Typography {:color "white"
                                   :variant "h6"}
                    "CREATE"]
          ::edit [:> Typography {:color "white"
                                 :variant "h6"}
                  "EDIT"]
          nil)]]]
     [:> Container
      [view current-route]]]))

