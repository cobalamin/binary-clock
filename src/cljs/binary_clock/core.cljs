(ns binary-clock.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]))

(enable-console-print!)

(defonce app-state
  (atom
   {:time (js/Date.)}))


(defn decimal-parts [n]
  [(quot n 10) (mod n 10)])

(defn bit-match [n]
  (mapv #(bit-test n %) [3 2 1 0]))

(defn time->parts [time]
  [(.getHours time) (.getMinutes time) (.getSeconds time)])


(defcomponent binary-cell [active _]
  (render
   [_]
   (dom/div {:class (str "cell"
                         (if active " active" ""))})))

(defcomponent binary-column [column owner]
  (render
   [_]
   (dom/div {:class "column"}
            (om/build-all binary-cell (bit-match column))
            (dom/div {:class "num"} column))))

(defcomponent binary-part [part owner]
  (render
   [_]
   (dom/div {:class "part"}
            (om/build-all binary-column (decimal-parts part)))))


(defcomponent main-view [app owner]
  (will-mount
   [_]
   (js/setInterval #(om/update! app :time (js/Date.)) 1))
  
  (render
   [_]
   (dom/div (om/build-all binary-part (time->parts (:time app))))))

(defn main []
  (om/root
    main-view
    app-state
    {:target (. js/document (getElementById "app"))}))
