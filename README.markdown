# Clojure to do annotation

The `clj-todo` lib is designed for easily annotating Clojure programs with todo items.
The lib generates a todo summary which includes code fragments to make it easy to extract all todos without having to read trough the entire source code.
As Clojure code (idiomatically) is written as small atomic components these reports should be perfect for code reviews.

## Installation

`clj-todo` version `0.5.0` is uploaded to [clojars](http://clojars.org).
To use it, simply add it to your lein dependencies as

    [micrub/clj-todo "0.5.0"]

The current version `0.5.0` is targeted at Clojure `1.6.0`.
[![Clojars Project](http://clojars.org/micrub/clj-todo/latest-version.svg)](http://clojars.org/micrub/clj-todo)

## Example usage


*At the moment, running `lein todo` will remove all compiled files as the program has to expand macros to build the todo log.*

Here is a full example of using the `todo` macro: (also included in [source](http://github.com/micrub/clj-todo/blob/master/src/clj_todo/example.clj))

```
(ns clj-todo.example
  (:use clj-todo))

(defn lousy-function
  [param-1 param-2]
  (do
    (println "doing one thing here")
    (todo
     "This part looks ugly"
     (map param-1 (repeat param-2))
     )
    (println "a third thing here")))

(todo
 "I don't like how this function works at all. It could be O(1)."
 (defn range-sum
   [n]
   (reduce + (range n))))
```

To get a summary of the todos use `lein todo`.
Uses `project.clj` `:source-paths` map property to get source folders, find namespaces in all folders defined.
For example:
```
:source-paths ["src" "tests"]
```

By default it will search for namespaces in `src/` folder,which is default source-path defined for leiningen.


If `lein todo` is given a list of namespaces it will print the todos of these.
If none is given it will use all namespaces in the project.

```
Summary of todos:

clj-todo.example clj_todo/example.clj {:line 10, :column 6}
  This part looks ugly

(map param-1 (repeat param-2))

clj-todo.example clj_todo/example.clj {:line 15, :column 2}
  I don't like how this function works at all. It could be O(1).

(defn range-sum [n] (reduce + (range n)))
```


If `:todo-log` is given in `project.clj`, todos will also be written to that file.
See [`project.clj`](http://github.com/micrub/clj-todo/blob/master/project.clj) for an example of this.
