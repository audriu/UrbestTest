(ns ubtest.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::issues-loading
 (fn [{:keys [issues-loading]}]
   issues-loading))

(re-frame/reg-sub
 ::all-issues
 (fn [{:keys [issues]}]
   issues))

(re-frame/reg-sub
 ::search-text
 (fn [{:keys [search-text]}]
   search-text))

(re-frame/reg-sub
 ::category-to-show
 (fn [{:keys [category-to-show]}]
   category-to-show))

(defn- matches-search-text? [search-text issue]
  (let [{:keys [title description]} (second issue)]
    (or (re-find (re-pattern search-text) title)
        (re-find (re-pattern search-text) description))))

(re-frame/reg-sub
 ::issues
 :<- [::all-issues]
 :<- [::search-text]
 :<- [::category-to-show]
 (fn [[all-issues search-text category-to-show] [_ status]]
   (let [filtered-by-status (filter #(= (:status (second %)) status) all-issues)
         filtered-by-category (if category-to-show
                                (filter #(= (:category (second %)) category-to-show) filtered-by-status)
                                filtered-by-status)
         filtered-by-text (if search-text
                            (filter #(matches-search-text? search-text %) filtered-by-category)
                            filtered-by-category)]
     filtered-by-text)))
