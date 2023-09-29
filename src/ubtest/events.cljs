(ns ubtest.events
  (:require [ajax.core :as http]
            [clojure.edn :as edn]
            [day8.re-frame.http-fx]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 :issues-file-loaded
 (fn [db [_ file-content]]
   (-> db
       (dissoc :issues-loading)
       (dissoc :issues-loading-error)
       (assoc :issues (edn/read-string file-content)))))

;;todo - right now, we just throw away the error
(re-frame/reg-event-db
 :loading-issues-file-failed
 (fn [db [_ error]]
   (-> db
       (dissoc :issues-loading)
       (assoc :error error))))

(re-frame/reg-event-fx
 ::initialize-db
 (fn [{:keys [db]} _]
   {:db (assoc db :issues-loading true)
    :http-xhrio {:method          :get
                 :uri             "/data.edn"
                 :response-format (http/raw-response-format)
                 :on-success      [:issues-file-loaded]
                 :on-failure      [:loading-issues-file-failed]}}))
