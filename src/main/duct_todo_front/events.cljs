(ns duct-todo-front.events
  (:require
   [duct-todo-front.db :as db]
   [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-current-route
 (fn [db [_ route]]
   (assoc db :route route)))

