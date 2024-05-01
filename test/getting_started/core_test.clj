(ns getting-started.core-test
  (:require [clojure.test :refer :all]
            [getting-started.core :refer :all]))

(deftest test-functions
  (testing "Test functions"
    (do
      (let [player-map (atom {:name "Rob" :position_x 0 :position_y 0 :nswe "EAST"}) error_message (atom "")]
        (motion player-map "LEFT")
        (is (= {:name "Rob", :position_x 0, :position_y 0, :nswe "NORTH"} @player-map))
        (motion player-map "RIGHT")
        (is (= {:name "Rob", :position_x 0, :position_y 0, :nswe "EAST"} @player-map))
        (motion player-map "MOVE")
        (is (= {:name "Rob", :position_x 1, :position_y 0, :nswe "EAST"} @player-map))
        (reset! player-map {:name "Rob" :position_x 4 :position_y 0 :nswe "EAST"})
        (motion player-map "MOVE")
        (is (= {:name "Rob", :position_x 4, :position_y 0, :nswe "EAST"} @player-map))
        (reset! player-map {:name "Rob" :position_x 4 :position_y 4 :nswe "NORTH"})
        (motion player-map "MOVE")
        (is (= {:name "Rob", :position_x 4, :position_y 4, :nswe "NORTH"} @player-map))
        ;; With data_test.txt -> PLACE,1,1,NORTH
        ;;                       MOVE
        ;;                       LEFT
        ;;                       MOVE
        ;;                       RIGHT
        ;;                       MOVE
        (read_data_txt player-map error_message)
        (is (= "no_error" @error_message))
        (is (= {:name "Rob", :position_x 0, :position_y 3, :nswe "NORTH"} @player-map))
        ))))
