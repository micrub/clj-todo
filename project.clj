(defproject micrub/clj-todo "0.5.0"
  :description "A small lib for adding todo annotations to Clojure projects.
                Forked from https://github.com/tgk/clj-todo"
  :url "https://github.com/micrub/clj-todo"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [leiningen-core "2.5.0"]
                 [org.clojure/tools.namespace "0.2.7"]]
  :dev-dependencies [[autodoc "0.7.1"]]
  :autodoc {:copyright "2010 Thomas G. Kristensen"}
  :todo-log "todo-summary.log")
