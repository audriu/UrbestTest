(ns ubtest.events
  (:require [ajax.core :as http]
            [clojure.edn :as edn]
            [day8.re-frame.http-fx]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 :issues-file-loaded
 (fn [db [_ file-content]]
   (let [data (into {} (map #(vector (:id %) %) (edn/read-string file-content)))]
     (-> db
         (dissoc :issues-loading)
         (dissoc :issues-loading-error)
         (assoc :issues data)))))

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

(re-frame/reg-event-db
 ::add-issue
 (fn [db [_ new-issue]]
   (update db :issues conj {(:id new-issue) new-issue})))

(re-frame/reg-event-db
 ::update-issue-status
 (fn [db [_ id status]]
   (assoc-in db [:issues id :status] status)))

(re-frame/reg-event-db
 ::set-search-text
 (fn [db [_ text]]
   (assoc db :search-text text)))

(re-frame/reg-event-db
 ::set-category-to-show
 (fn [db [_ category]]
   (assoc db :category-to-show category)))
