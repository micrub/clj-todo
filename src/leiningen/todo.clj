(ns leiningen.todo
  (:use [leiningen.core.eval :only (eval-in-project)])
  (:use [clojure.java.io :only [file]])
  (:require [leiningen.core.project :as p])
  (:use [clojure.tools.namespace.find :only [find-namespaces-in-dir]]))

(declare eval-summary)

(defn- style [msg & args]
  msg)

(defn- print-warning [msg]
  (let [label (style "Warning:" :bg-yellow)
        msg   (style (str msg) :yellow :underline)]
    (println label " " msg)))

(defn- delete-recursively [fname]
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
                     (get-namespaces (get-source-paths project)))]
    ;; TODO why do we delete here ? because of eval later ?
    (delete-recursively (file (:compile-path project)))
    (eval-summary project namespaces)
    ;; also write to file if defined in :todo-log
    (if (contains? project :todo-log)
      (eval-summary project namespaces true))))

(defn- eval-summary [project namespaces & todo-log]
  (if todo-log
    (eval-in-project project
                     `(do
                        (require '~'clj-todo)
                        (apply require '~namespaces)
                        (clj-todo/todo-summary-file ~(:todo-log project))))
    (eval-in-project project
                     `(do
                        (require '~'clj-todo)
                        (apply require '~namespaces)
                        (clj-todo/todo-summary)))))
