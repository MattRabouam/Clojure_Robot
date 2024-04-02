(defproject getting-started "1.0.0-Clojure_Robot"
  :description "The application is a simulation of a toy robot moving on a square tabletop, of dimensions 5 units x 5 units."
  :url "https://github.com/MattRabouam/Clojure_Robot"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"][clojure-lanterna "0.9.7"]]
  :main ^:skip-aot getting-started.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
