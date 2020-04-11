(ns noisesmith.cyberbaby
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:gen-class))

(def new-game
  {:actions {:notice []
             :wiggle []
             :grab []
             :shout []}
   :status {:location :home
            :resources {:fuel 1.0}
            :damage {}
            :enhancements {}}
   :entities {:self {:place :home}
              :home {:place :?
                     :exits {}
                     :entrances {}}}
   :pending {}})

(defn load-save-file
  [f]
  (if (.exists f)
      (slurp f)))

(defn state-from
  [f]
  (let [document (and f (load-save-file (io/file f)))]
    (cond (and (not document)
               f)
          (throw (ex-info "Could not load save file."
                          {:f f
                           :document document}))
          document (edn/read-string document)
          :else new-game)))

(defn act
  [text world]
  (cond
   (= text "notice")
   (do (println "you are in a simple world, and you have seen the entirety of what exists")
       (update world :status assoc :done true :won true))
   :else (do (println "?")
             world)))

(defn game-function
  [world]
  (newline)
  (print "> ")
  (flush)
  (act (read-line)
       world))

(defn finished?
  [world]
  (get-in world [:status :done]))

(defn game
  [input output world]
  (binding [*in* input
            *out* output]
    (drop-while (complement finished?)
                (iterate game-function
                         world))))

(defn wrap-up
  [world]
  (cond (get-in world [:status :won])
        (println "YAY YOU WON!")
        ;; TODO - save game etc.
        :else "nice try"))

(defn -main
  "Creates and runs a game state"
  ([& [file & _]]
   (wrap-up
    (first (game *in* *out* (state-from file))))))
