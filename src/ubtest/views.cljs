(ns ubtest.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :refer [at h-box v-box title]]
   [ubtest.subs :as subs]))

(defn card [issue]
  [v-box
   :style {:background-color "white"
           :border "1px solid black"
           :padding "10px"
           :margin "10px"}
   :gap "10px"
   :children [[title
               :src   (at)
               :label (:title issue)
               :level :level2]
              [title
               :src   (at)
               :label (:building issue)
               :level :level3]
              [title
               :src   (at)
               :label (:category issue)
               :level :level3]]])

(defn column [type]
  (let [issues (re-frame/subscribe [::subs/issues type])]
    [:div
     [title
      :src   (at)
      :label type
      :level :level1]
     [v-box
      :style {:background-color "gray"}
      :height "100%"
      :children (mapv (fn [issue] [card issue]) @issues)]]))

(defn main-panel []
  (let [issues-loading (re-frame/subscribe [::subs/issues-loading])]
    (if @issues-loading
      [title
       :src   (at)
       :label "Loading issues..."
       :level :level1]
      [h-box
       :style {:background-color "lightgray"}
       :height "100%"
       :gap "10px"
       :children [[column "todo"]
                  [column "in-progress"]
                  [column "done"]]])))
