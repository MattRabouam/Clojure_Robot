(ns getting-started.core-test
  (:require [clojure.test :refer :all]
            [getting-started.core :refer :all]))

(deftest test-read_data_txt
  (testing "Test read_data_txt function"
    (do
      (read_data_txt)
      (is (= ["PLACE" "1" "1" "NORTH"] @in_data_text))

      (reset! name_data_text "data_dont_exist.txt")
      (read_data_txt)
      (is (= ["File don't exist" @name_data_text] @in_data_text))

      (reset! name_data_text "data_test.txt")
      (read_data_txt)
      (is (= true (test_in_data_text)))

      (reset! in_data_text ["File exist"])
      (test_in_data_text)
      (is (= "PLACE isn't found at start of data" @in_data_text))

      (reset! in_data_text ["PLACE" "1" "NORTH"])
      (test_in_data_text)
      (is (= "Incorrect element number" @in_data_text))

      (reset! in_data_text ["PLACE" "5" "1" "NORTH"])
      (test_in_data_text)
      (is (= "Element X incorrect" @in_data_text))

      (reset! in_data_text ["PLACE" "1" "8" "NORTH"])
      (test_in_data_text)
      (is (= "Element Y incorrect" @in_data_text))

      (reset! in_data_text ["PLACE" "1" "1" "NORD"])
      (test_in_data_text)
      (is (= "Element orientation incorrect" @in_data_text)))))


(deftest test-select_orientation
  (testing "Test select_orientation function"
    (do 
      (reset! presskey \z)
      (select_orientation)
      (is (= "NORTH" (:nswe @player-map)))

      (reset! presskey \q)
      (select_orientation)
      (is (= "WEST" (:nswe @player-map)))

      (reset! presskey :right)
      (select_orientation)
      (is (= "EAST" (:nswe @player-map)))

      (reset! presskey :down)
      (select_orientation)
      (is (= "SOUTH" (:nswe @player-map))))))


(deftest test-move_forward
  (testing "Test move_forward function"
    (do
      (swap! player-map assoc :nswe "NORTH")
      (swap! player-map assoc :position_y 1)
      (move_forward)
      (is (= 2 (:position_y @player-map)))

      (swap! player-map assoc :nswe "NORTH")
         (swap! player-map assoc :position_y 4)
         (is (= "watch out edge" (move_forward)))
      
      (swap! player-map assoc :nswe "WEST")
      (swap! player-map assoc :position_x 2)
      (move_forward)
      (is (= 1 (:position_x @player-map)))
      
      (swap! player-map assoc :nswe "WEST")
      (swap! player-map assoc :position_x 0)
      (is (= "watch out edge" (move_forward)))
      
      (swap! player-map assoc :nswe "SOUTH")
      (swap! player-map assoc :position_y 4)
      (move_forward)
      (is (= 3 (:position_y @player-map)))
      
      (swap! player-map assoc :nswe "SOUTH")
      (swap! player-map assoc :position_y 0)
      (is (= "watch out edge" (move_forward)))
         
      (swap! player-map assoc :nswe "EAST")
      (swap! player-map assoc :position_x 3)
      (move_forward)
      (is (= 4 (:position_x @player-map)))
      
      (swap! player-map assoc :nswe "EAST")
      (swap! player-map assoc :position_x 4)
      (is (= "watch out edge" (move_forward))))))

(deftest test-turn_left
  (testing "Test turn_left function"
    (do
      (swap! player-map assoc :nswe "NORTH")
      (turn_left)
      (is (= "WEST" (:nswe @player-map)))

      (swap! player-map assoc :nswe "WEST")
      (turn_left)
      (is (= "SOUTH" (:nswe @player-map)))

      (swap! player-map assoc :nswe "SOUTH")
      (turn_left)
      (is (= "EAST" (:nswe @player-map)))

      (swap! player-map assoc :nswe "EAST")
      (turn_left)
      (is (= "NORTH" (:nswe @player-map))))))

(deftest test-turn_right
  (testing "Test turn_right function"
    (do
      (swap! player-map assoc :nswe "NORTH")
      (turn_right)
      (is (= "EAST" (:nswe @player-map)))

      (swap! player-map assoc :nswe "WEST")
      (turn_right)
      (is (= "NORTH" (:nswe @player-map)))
      
      (swap! player-map assoc :nswe "SOUTH")
      (turn_right)
      (is (= "WEST" (:nswe @player-map)))
      
      (swap! player-map assoc :nswe "EAST")
      (turn_right)
      (is (= "SOUTH" (:nswe @player-map))))))

(deftest test-update_name_player
  (testing "Test update_name_player function"
    (do
      (swap! player-map assoc :name "Victo")
      (reset! presskey \r)
      (update_name_player)
      (is (= "Victor" (:name @player-map)))
      
      (reset! presskey :backspace)
      (update_name_player)
      (is (= "Victo" (:name @player-map)))
    )))