(ns duct-todo-front.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::current-route
 (fn [db _]
   (get db :route {:handler :todo-app.views/home})))

