(ns leiningen.todo
  (:use [leiningen.core.eval :only (eval-in-project)])
  (:use [clojure.java.io :only [file]])
  (:require [leiningen.core.project :as p])
  (:use [clojure.tools.namespace.find :only [find-namespaces-in-dir]]))

(defn delete-recursively [fname]
  (let [func (fn [func f]
               (when (.isDirectory f)
                 (doseq [f2 (.listFiles f)]
                   (func func f2)))
               (clojure.java.io/delete-file f))]
    (func func (clojure.java.io/file fname))))

(defn- ns-find-waring [e dir-path]
  (format
    "value: %s in source paths collection caused %s"
    dir-path
    (style (.toString e) :red)))

(defn- get-source-paths [project]
  (let [source-paths-key :source-paths]
    (concat (source-paths-key p/defaults)
            (source-paths-key (source-paths-key project)))))

(defn- safely-find-namespaces-in-dir [^String dir-path]
  (try
    (find-namespaces-in-dir (file dir-path))
    (catch Exception e (print-warning (ns-find-waring e dir-path)))))

(defn- get-namespaces [source-paths]
  (map
    safely-find-namespaces-in-dir
    source-paths))

(defn todo
  "Prints a summary of todos annotated using clj-todo.todo/todo.
  If namespaces are given as commandline args, prints the summary
  for those namespaces; otherwise prints the summary for all the
  :namespaces in project.clj. If :todo-log is given in project,
  also writes the log to the filename in :todo-log."
  [project & namespaces]
  (let [namespaces (if (seq namespaces)
                     (map symbol namespaces)
                     (find-namespaces-in-dir (file (:source-path project))))]
    (delete-recursively (file (:compile-path project)) true)
    (eval-in-project project
                     `(do
                        (require '~'clj-todo)
                        (apply require '~namespaces)
                        (clj-todo/todo-summary)))
    (if (contains? project :todo-log)
      (eval-in-project project
                       `(do
                          (require '~'clj-todo)
                          (apply require '~namespaces)
                          (clj-todo/todo-summary-file ~(:todo-log project)))))))
