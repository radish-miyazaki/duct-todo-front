(ns duct-todo-front.fx
  (:require
   [duct-todo-front.routes :as routes]
   [re-frame.core :as re-frame]))

(re-frame/reg-fx
 ::navigate
 (fn [{:keys [view params]}]
   (routes/navigate view params)))

