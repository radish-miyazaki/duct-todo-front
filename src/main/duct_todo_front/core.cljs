(ns duct-todo-front.core
  (:require
   [accountant.core :as accountant]
   [bidi.bidi :as bidi]
   [duct-todo-front.config :as config]
   [duct-todo-front.events :as events]
   [duct-todo-front.routes :refer [routes]]
   [duct-todo-front.views :as views]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [reagent.dom.client :as client]))

(defonce root (client/create-root (.getElementById js/document "app")))

(defn- dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn- mount-root
  "The `:dev/after-load` metadata causes this function to be called after
  shadow-cljs hot-reloads code. This function is called implicitly by its
  annotation.
  @ref https://stackoverflow.com/a/72477660"
  []
  (re-frame/clear-subscription-cache!)
  (.render root (reagent/as-element [views/main-panel])))

(defn ^:dev/after-load re-render []
  (mount-root))

(defn ^:export init []
  (re-frame.core/dispatch-sync [::events/initialize-db])
  (accountant/configure-navigation!
   {:nav-handler (fn [path]
                   (re-frame/dispatch [::events/set-current-route (bidi/match-route routes path)]))
    :path-exists? (fn [path]
                    (boolean (bidi/match-route routes path)))
    :reload-same-path? true})
  (accountant/dispatch-current!)
  (dev-setup)
  (mount-root))

