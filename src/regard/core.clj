(ns regard.core
  (:gen-class))

(require 'frak)

(defn -main [& args]
  "Take ARGS and pass them to frak, insult the user if none."
  (if args
    (println "Regexp : " (frak/pattern (vec args)))
    (println "usage: app_name \"pattern1\" \"pattern2\" \"patternN\"")))
