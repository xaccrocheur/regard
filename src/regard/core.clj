(ns regard.core)

(require 'frak)

(defn -main
  "I don't do a whole lot."
  [& args]
  (frak/pattern ["foo" "bar" "baz" "quux"])
  (println "Hello, World! arg? %s" args))

(def command (atom ""))

(defn print-prompt []
  (print "prompt> ")
  (flush)
)

(defn ask-for-input []
    (print-prompt)
    (let [x (str (read-line))]
      (println (str "User input: " x))
      (reset! command x)
    )
)

(ask-for-input)
