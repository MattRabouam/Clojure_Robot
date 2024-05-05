(ns getting-started.core-test
  (:require [clojure.test :refer :all]
            [getting-started.core :refer :all]))

(deftest test-functions
  (testing "Test functions"
    (do
      (let [player-map (atom {:name "Rob" :position_x 0 :position_y 0 :nswe "EAST"}) error_message (atom "")]
        ;; Test motion "LEFT"
        (is (= {:name "Rob", :position_x 0, :position_y 0, :nswe "NORTH"} (motion player-map "LEFT")))
        ;; Test motion "RIGHT"
        (is (= {:name "Rob", :position_x 0, :position_y 0, :nswe "SOUTH"} (motion player-map "RIGHT")))
        ;; Test motion "MOVE"
        (is (= {:name "Rob", :position_x 1, :position_y 0, :nswe "EAST"} (motion player-map "MOVE")))
        ;; Test motion "MOVE" on board on x
        (reset! player-map {:name "Rob" :position_x 4 :position_y 0 :nswe "EAST"})
        (is (= {:name "Rob", :position_x 4, :position_y 0, :nswe "EAST"} (motion player-map "MOVE")))
        ;; Test motion "MOVE" on board on y
        (reset! player-map {:name "Rob" :position_x 4 :position_y 4 :nswe "NORTH"})
        (is (= {:name "Rob", :position_x 4, :position_y 4, :nswe "NORTH"} (motion player-map "MOVE")))
        ;; Test With data_test.txt -> PLACE,1,1,NORTH
        ;;                       MOVE
        ;;                       LEFT
        ;;                       MOVE
        ;;                       RIGHT
        ;;                       MOVE
        (read_data_txt player-map error_message)
        (is (= "no_error" @error_message))
        (is (= {:name "Rob", :position_x 0, :position_y 3, :nswe "NORTH"} @player-map))))))
