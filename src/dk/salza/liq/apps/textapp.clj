(ns dk.salza.liq.apps.textapp
  (:require [dk.salza.liq.editor :as editor]
            [dk.salza.liq.modes.textmode :as textmode]
            [dk.salza.liq.syntaxhl.clojuremdhl :as clojuremdhl]
            [dk.salza.liq.syntaxhl.javascripthl :as javascripthl]
            [dk.salza.liq.syntaxhl.xmlhl :as xmlhl]
            [dk.salza.liq.coreutil :refer :all]))

(defn run
  [filepath]
  (if (editor/get-buffer filepath)
    (editor/switch-to-buffer filepath)
    (let [syntaxhl (cond (nil? filepath) clojuremdhl/next-face
                         (re-matches #"^.*\.js$" filepath) javascripthl/next-face
                         (re-matches #"^.*\.java$" filepath) javascripthl/next-face
                         (re-matches #"^.*\.xml$" filepath) xmlhl/next-face
                          :else clojuremdhl/next-face) ;; In other cases use clojure/markdown
          mode (textmode/create syntaxhl)]
      (editor/create-buffer-from-file filepath)
      (editor/set-mode mode))))