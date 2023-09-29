(ns ubtest.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [re-com.core :refer [at h-box v-box title]]
   [ubtest.events :as events]
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
      :style {:margin-left "15px"}
      :label (str (clojure.string/capitalize type) " (" (count @issues) ")")
      :level :level1]
     [v-box
      :style {:background-color "gray"}
      :height "100%"
      :children (mapv (fn [issue] [card issue]) @issues)]]))

(defn form-view []
  (let [title (reagent/atom "")
        description (reagent/atom "")
        building (reagent/atom "")
        category (reagent/atom "cleaning")]
    (fn []
      [:div
       [:div
        [:label {:for "title"} "Title: "]
        [:input {:type "text"
                 :id "title"
                 :value @title
                 :on-change #(reset! title (-> % .-target .-value))}]]
       [:div
        [:label {:for "description"} "Description: "]
        [:textarea {:id "description"
                    :value @description
                    :on-change #(reset! description (-> % .-target .-value))}]]
       [:div
        [:label {:for "building"} "Building: "]
        [:input {:type "text"
                 :id "building"
                 :value @building
                 :on-change #(reset! building (-> % .-target .-value))}]]
       [:div
        [:label {:for "category"} "Category: "]
        [:select {:id "category"
                  :value @category
                  :on-change #(reset! category (-> % .-target .-value))}
         [:option {:value "cleaning"} "Cleaning"]
         [:option {:value "security"} "Security"]
         [:option {:value "electricity"} "Electricity"]
         [:option {:value "temperature"} "Temperature"]]]
       [:button {:on-click #(do
                              (re-frame/dispatch [::events/add-issue
                                                  {:title @title
                                                   :description @description
                                                   :building @building
                                                   :status "todo"
                                                   :category @category}])
                              (reset! title "")
                              (reset! description "")
                              (reset! building "")
                              (reset! category "cleaning"))}
        "Submit"]])))

(defn main-panel []
  (let [issues-loading (re-frame/subscribe [::subs/issues-loading])]
    (if @issues-loading
      [title
       :src   (at)
       :label "Loading issues..."
       :level :level1]
      [:div
       [h-box
        :style {:background-color "lightgray"}
        :height "100%"
        :gap "10px"
        :children [[column "todo"]
                   [column "in-progress"]
                   [column "done"]]]
       [form-view]])))
