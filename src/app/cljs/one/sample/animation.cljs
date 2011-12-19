(ns ^{:doc "Defines animations which are used in the sample
  application."}
  one.sample.animation
  (:use [one.core :only (start)]
        [one.browser.animation :only (bind parallel serial)]
        [domina :only (xpath by-id set-html! set-styles! destroy-children! append!)])
  (:require [goog.dom.forms :as gforms]))

(def form "//div[@id='form']")
(def cloud "//div[@id='greeting']")
(def label "//label[@id='name-input-label']/span")

(defn play
  "Accepts an element and any number of animations and plays them in
  order."
  [element & animations]
  (start (apply bind element animations)))

(def ^:private
  form-in [{:effect :slide :down 880 :time 800}
           {:effect :fade :start 0 :end 1 :time 400}])

(defn initialize-views
  "Accepts the form and greeting view HTML and adds them to the
  page. Animates the form sliding in from above. This function must be
  run before any other view functions. It may be called from any state
  to reset the UI."
  [form-html greeting-html]
  (let [style {:position "absolute" :width "960px"}
        content (xpath "//div[@id='content']")]
    (destroy-children! content)
    (set-html! content form-html)
    (set-styles! (xpath form) (assoc style :top "-780px"))
    (append! content greeting-html)
    (set-styles! (xpath cloud) (assoc style :top "460px" :opacity "0"))
    (play form form-in)))

(comment ;; Try it

  (initialize-views (:form one.sample.view/snippets)
                    (:greeting one.sample.view/snippets))
  )

(defn label-move-up
  "Move the passed input field label above the input field. Run when
  the field gets focus and is empty."
  [label]
  (play label [{:effect :color :end "#53607b" :time 200}
               {:effect :slide :up 40 :time 200}]))

(defn label-fade-out
  "Make the passed input field label invisible. Run when the input
  field loses focus and contains a valid input value."
  [label]
  (play label {:effect :fade :end 0 :time 200}))

(def move-down [{:effect :fade :end 1 :time 200}
                {:effect :color :end "#BBC4D7" :time 200}
                {:effect :slide :down 40 :time 200}])

(def fade-in {:effect :fade :end 1 :time 400})

(def fade-out {:effect :fade :start 1 :end 0 :time 400})

(defn label-move-down
  "Make the passed input field label visible and move it down into the
  input field. Run when an input field loses focus and is empty."
  [label]
  (play label move-down))

(comment ;; Examples of label effects.
  
  (label-move-up label)
  (label-fade-out label)
  (label-move-down label)
  )

(defn show-greeting
  "Move the form out of view and the greeting into view. Run when the
  submit button is clicked and the form has valid input."
  []
  (start (parallel (bind form [{:effect :slide :up 800 :time 800}
                               {:effect :fade :start 1 :end 0 :time 400}])
                   (bind cloud [{:effect :slide :up 400 :time 500}
                                {:effect :fade :start 0 :end 1 :time 500}]))))

(defn show-form
  "Move the greeting cloud out of view and show the form. Run when the
  back button is clicked from the greeting view."
  []
  (start (serial (parallel (bind form form-in)
                           (bind cloud [{:effect :slide
                                         :down 400
                                         :time 400
                                         :accel :ease-in}
                                        fade-out]))
                 (bind label fade-in move-down))))

(comment ;; Switch between greeting and form views

  (label-move-up label)
  (show-greeting)
  (show-form)
  )

(defn disable-button
  "Accepts an element id for a button and disables it. Fades the
  button to 0.2 opacity."
  [id]
  (let [button (by-id id)]
    (gforms/setDisabled button true)
    (play button (assoc fade-out :end 0.2))))

(defn enable-button
  "Accepts an element id for a button and enables it. Fades the button
  to an opactiy of 1."
  [id]
  (let [button (by-id id)]
    (gforms/setDisabled button false)
    (play button fade-in)))

(comment ;; Examples of all effects

  (initialize-views (:form one.sample.view/snippets)
                    (:greeting one.sample.view/snippets))
  (label-move-up label)
  (label-fade-out label)
  (show-greeting)
  (show-form)

  (disable-button "greet-button")
  (enable-button "greet-button")
  )