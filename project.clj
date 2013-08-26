(defproject regard "1.0.0-SNAPSHOT"
  :description "Spits out regexps, given keywords"
  :dependencies [[org.clojure/clojure "1.3.0"]
                [frak "0.1.2"]]
  :main regard.core
  :aot [regard.core])
