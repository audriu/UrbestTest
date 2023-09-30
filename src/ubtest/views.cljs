(ns ubtest.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [re-com.core :refer [at h-box v-box line title]]
   [ubtest.events :as events]
   [ubtest.subs :as subs]))

(defn card [issue]
  (let [status (reagent/atom (:status issue))]
    [v-box
     :style {:background-color "white"
             :border "1px solid black"
             :border-radius "5px"
             :padding "10px"
             :margin "10px"}
     :gap "10px"
     :children [[title
                 :src   (at)
                 :label [:div {:style {:display "flex"
                                       :width "100%"
                                       :justify-content "space-between"}}
                         [:span {:style {:color "violet"
                                         :font-size "30px"}} "ƒ"]
                         (:title issue)
                         [:span {:style {:color "gray"
                                         :font-size "30px"}} "Ⅷ"]]
                 :level :level2]
                [title
                 :src   (at)
                 :label (:description issue)
                 :level :level4]
                [title
                 :src   (at)
                 :label (:building issue)
                 :level :level3]
                [h-box
                 :gap      "10px"
                 :children [[line
                             :size  "3px"
                             :color "red"]
                            [title
                             :src   (at)
                             :label (:category issue)
                             :level :level3]]]
                [:div {:style {:text-color "gray"}}
                 [:span {:style {:color "gray"
                                 :font-size "30px"}} "§"]
                 [:label {:for "status"} "Status: "]
                 [:select {:id "status"
                           :value @status
                           :on-change #(do
                                         (reset! status (-> % .-target .-value))
                                         (re-frame/dispatch [::events/update-issue-status (:id issue) @status]))}
                  [:option {:value "todo"} "To Do"]
                  [:option {:value "in-progress"} "In Progress"]
                  [:option {:value "done"} "Done"]]]]]))

(defn column [status]
  (let [issues (re-frame/subscribe [::subs/issues status])]
    [:div
     [title
      :src   (at)
      :style {:margin-left "15px"}
      :label (str (clojure.string/capitalize status) " (" (count @issues) ")")
      :level :level1]
     [v-box
      :style {:background-color "lightgray"}
      :height "100%"
      :children (mapv (fn [issue] [card (second issue)]) @issues)]]))

(defn form-view []
  (let [title (reagent/atom "")
        description (reagent/atom "")
        building (reagent/atom "")
        category (reagent/atom "cleaning")]
    (fn []
      [:div {:style {:margin-top "100px"}}
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
                                                  {:id (js/Date.now)
                                                   :title @title
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
       (let [search-text (re-frame/subscribe [::subs/search-text])
             cathegory-to-show (re-frame/subscribe [::subs/category-to-show])]
         [:div
          [:label "Search: "]
          [:input {:type "text"
                   :value @search-text
                   :on-change #(re-frame/dispatch [::events/set-search-text (-> % .-target .-value)])}]
          [:label "Category to show: "]
          [:select {:value @cathegory-to-show
                    :on-change #(let [set-category-to-show (-> % .-target .-value)
                                      set-category-to-show (if (= set-category-to-show "all") nil set-category-to-show)]
                                  (re-frame/dispatch [::events/set-category-to-show set-category-to-show]))}
           [:option {:value "all"} "All"]
           [:option {:value "cleaning"} "Cleaning"]
           [:option {:value "security"} "Security"]
           [:option {:value "electricity"} "Electricity"]
           [:option {:value "temperature"} "Temperature"]]])
       [h-box
        :style {:background-color "gray"}
        :height "100%"
        :gap "10px"
        :children [[column "todo"]
                   [column "in-progress"]
                   [column "done"]]]
       [form-view]])))
