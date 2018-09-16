(ns re-frame-todo.db)

(def default-db
  {:items-by-added-at {12345 {:checked? true, :text "hi", :added-at 12345}}
   :item-list '(12345)
   :input-value ""
   :sort-by-desc-added-at? true})
