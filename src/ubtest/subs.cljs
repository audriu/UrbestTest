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
 ::search-text
 (fn [db]
   (:search-text db)))

(re-frame/reg-sub
 ::category-to-show
 (fn [db]
   (:category-to-show db)))

(re-frame/reg-sub
 ::issues
 :<- [::all-issues]
 :<- [::search-text]
 :<- [::category-to-show]
 (fn [[all-issues search-text category-to-show] [_ status]]
   (let [filtered-issues (filter #(= (:status (second %)) status) all-issues)
         filtered-issues (if category-to-show
                           (filter #(= (:category (second %)) category-to-show) filtered-issues)
                           filtered-issues)
         filtered-issues (if search-text
                           (filter #(or (re-find (re-pattern search-text) (:title (second %)))
                                        (re-find (re-pattern search-text) (:description (second %))))
                                   filtered-issues)
                           filtered-issues)]
     filtered-issues)))
