(defproject regard "1.0.0-SNAPSHOT"
  :description "Spits out regexps, given keywords"
  :dependencies [[org.clojure/clojure "1.5.1"]
                [frak "0.1.3"]]
  :main regard.core
  :aot [regard.core]
  :uberjar-name "regard.jar")
