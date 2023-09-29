(ns ubtest.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::issues-loading
 (fn [db]
   (:issues-loading db)))

(re-frame/reg-sub
 ::all-issues
 (fn [db]
   (:issues db)))

(re-frame/reg-sub
 ::issues
 :<- [::all-issues]
 (fn [all-issues [_ type]]
   (filter #(= (:status %) type) all-issues)))
