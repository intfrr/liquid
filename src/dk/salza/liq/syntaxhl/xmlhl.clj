(ns dk.salza.liq.syntaxhl.xmlhl
  (:use [dk.salza.liq.slider :as slider :exclude [create]]))

(defn look-ahead
  [sl n]
  (apply str (map get-char (take n (iterate #(right % 1) sl)))))

(defn next-face
  [face ch pch ppch sl]
  (cond (= face :string)  (cond (and (= pch "\"") (= ppch "\\")) face
                                (and (= pch "\"") (= ch ">")) :type1
                                (and (= pch "\"") (string? (-> sl (left 2) (get-char)))) :plain
                                (and (= pch "\"") (re-matches #"[^=#\( \[{\n]" ppch)) :plain
                                (and (= pch "\"") (re-matches #"[\)\]}]" (or ch " "))) :plain
                                :else face)
        (= face :plain)   (cond (and (= ch "\"") (re-matches #"[=#\( \[{\n]" pch)) :string
                                (= ch ";") :comment
                                (and (= ch "#") (or (= pch "\n") (= (get-point sl) 0))) :comment 
                                (= ch "<") :type1
                                (= ch ">") :type1
                                (and (= ch ":") (re-matches #"[\( \[{\n]" pch)) :type3
                                :else face)
        (= face :type1)   (cond (= pch "<") :type2
                                (and (= pch ">") (= ch "<")) :type1
                                :else :plain)
        (= face :type2)   (cond (= ch " ") :plain
                                (= ch ">") :type1
                                :else face)
        (= face :type3)   (cond (re-matches #"[\)\]}\s]" (or ch " ")) :plain
                                :else face)
        (= face :comment) (cond (= ch "\n") :plain
                                :else face)
                          :else face))