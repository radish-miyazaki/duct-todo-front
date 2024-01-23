(ns duct-todo-front.routes
  (:require
   [accountant.core :as accountant]
   [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :duct-todo-front.views/home
        "list" :duct-todo-front.views/list
        "create" :duct-todo-front.views/create
        [[#"\d+" :id] "/edit"] :duct-todo-front.views/edit}])

(defn navigate [view]
  (accountant/navigate! (bidi/path-for routes view)))

