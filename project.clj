(defproject clj-todo "0.3.0-SNAPSHOT"
  :description "A small lib for adding todo annotations to Clojure projects."
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
		 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
		 [leiningen "1.2.0-SNAPSHOT"]]
  :dev-dependencies [[leiningen/lein-swank "1.1.0"]
		     [autodoc "0.7.1"]]
  :autodoc {:copyright "2010 Thomas G. Kristensen"}
  :todo-log "todo-summary.log")