(ns datoms-say.spec
  (:require [clojure.spec.alpha :as s])
  (:import (datomic.db Datum)
           (java.util Date)
           (java.net URI)
           (datomic.query EntityMap)))

(def byte-array-class (type (byte-array 0)))

(s/def ::long      #(instance? Long %))
(s/def ::number      number?)
(s/def ::entity-id ::number)

;; Value types. See http://docs.datomic.com/schema.html
(s/def ::keyword keyword?)
(s/def ::string  string?)
(s/def ::boolean boolean?)
(s/def ::bigint  #(instance? BigInteger %))
(s/def ::float   #(instance? Float %))
(s/def ::double  #(instance? Double %))
(s/def ::bigdec  #(instance? BigDecimal %))
(s/def ::ref     ::entity-id)
(s/def ::instant #(instance? Date %))
(s/def ::uuid    uuid?)
(s/def ::uri     #(instance? URI %))
(s/def ::bytes   #(instance? byte-array-class %))

(s/def ::attribute-id       ::entity-id)
(s/def ::transaction-id     ::entity-id)
(s/def ::entity-map #(instance? EntityMap %))

(s/def ::value              (s/or :keyword ::keyword :string ::string :boolean ::boolean :long
                                   ::long :bigint ::bigint :float ::float :double ::double
                                   :bigdec ::bigdec :ref ::ref :instant ::instant
                                   :uuid ::uuid :uri ::uri :bytes ::bytes :valset ::valset
                                  :entity ::entity-map))
(s/def ::added              boolean?)

(s/def ::datum #(instance? Datum %))
(s/def ::datoms-in          (s/coll-of ::datum))

(s/def ::referrer           (s/tuple ::entity-id ::attribute-id))
(s/def ::referent           ::entity-id)
(s/def ::reference          (s/tuple ::referrer ::referent))
(s/def ::references         (s/* ::reference))

(s/def ::asserted-value     ::value)
(s/def ::retracted-value    ::value)
(s/def ::singlevalue-change (s/keys :opt-un [::asserted-value ::retracted-value]))
(s/def ::valset             (s/coll-of ::value :kind set?))
(s/def ::before-set         ::valset)
(s/def ::after-set          ::valset)
(s/def ::multivalue-change  (s/keys :opt-un [::before-set ::after-set]))
(s/def ::ident              ::keyword)
(s/def ::value-type         ::keyword)
(s/def ::cardinality        #{:db.cardinality/one :db.cardinality/many})
(s/def ::attribute          (s/keys :req-un [::entity-id ::value-type ::ident ::cardinality] :opt-un [::value ::singlevalue-change ::multivalue-change]))
(s/def ::attributes         (s/* ::attribute))
(s/def ::partition          ::keyword)
(s/def ::entity             (s/keys :req-un [::entity-id ::attributes] :opt-un [::partition]))
(s/def ::entities           (s/* ::entity))
(s/def ::analyzed           (s/keys :req-un [::entities ::references]))

(s/def ::nodes              (s/coll-of (s/cat :label string? :text (s/? string?))))
(s/def ::edges              (s/coll-of (s/cat :from string? :to string?)))
(s/def ::graph              (s/keys :req-un [::nodes ::edges]))
