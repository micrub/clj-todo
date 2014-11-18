(ns leiningen.todo
  (:use [leiningen.compile :only (eval-in-project)])
  (:use [clojure.java.io :only [file]])
  (:use [clojure.tools.namespace.find :only [find-namespaces-in-dir]]))

(defn delete-recursively [fname]
  (let [func (fn [func f]
               (when (.isDirectory f)
                 (doseq [f2 (.listFiles f)]
                   (func func f2)))
               (clojure.java.io/delete-file f))]
    (func func (clojure.java.io/file fname))))

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
