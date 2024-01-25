(ns duct-todo-front.routes 
  (:require
   [accountant.core :as accountant]
   [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :duct-todo-front.views/list
        "create" :duct-todo-front.views/create
        ["edit/" [#"\d+" :id]] :duct-todo-front.views/edit}])

(def path-for (partial bidi/path-for routes))

(defn navigate
  ([view] (navigate view {}))
  ([view params]
   (accountant/navigate! (apply path-for view (apply concat params)))))

