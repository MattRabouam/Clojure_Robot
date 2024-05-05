(ns getting-started.core
   (:gen-class))

;; To display a terminal and read only one character from the keyboard
 (require '[lanterna.terminal :as t])

;; To read data.txt
 (require '[clojure.java.io :as io])
 (require '[clojure.string :as str])

 
;; global variables
 (def name_data_text "data_test.txt")
 (def alphabet (set "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàâäéèêëïîôöùûüÿçÀÂÄÉÈÊËÏÎÔÖÙÛÜŸÇ"))
 (def empty_string "                                          ")

 
;; Moves the cursor to a specific coordinate and displays text on the terminal
 (defn text_display [term tab_text]
   (doseq [i tab_text]
     (if (string? i)
       (t/put-string term i)
       (t/move-cursor term (get-in i [0]) (get-in i [1])))))

;; Choose an orientation according to the selected key
 (defn select_orientation [key player-map]
   (cond
     (or (= key \z) (= key :up)) (swap! player-map assoc :nswe "NORTH")
     (or (= key \q) (= key :left)) (swap! player-map assoc :nswe "WEST")
     (or (= key \d) (= key :right)) (swap! player-map assoc :nswe "EAST")
     (or (= key \s) (= key :down)) (swap! player-map assoc :nswe "SOUTH")))

;; Move or turn the robot
 (defn motion [player-map action]
   (cond
     (= action "LEFT") (cond
                         (= (:nswe @player-map) "NORTH") (swap! player-map assoc :nswe "WEST")
                         (= (:nswe @player-map) "WEST") (swap! player-map assoc :nswe "SOUTH")
                         (= (:nswe @player-map) "SOUTH") (swap! player-map assoc :nswe "EAST")
                         (= (:nswe @player-map) "EAST") (swap! player-map assoc :nswe "NORTH"))
     (= action "RIGHT") (cond
                          (= (:nswe @player-map) "NORTH") (swap! player-map assoc :nswe "EAST")
                          (= (:nswe @player-map) "WEST") (swap! player-map assoc :nswe "NORTH")
                          (= (:nswe @player-map) "SOUTH") (swap! player-map assoc :nswe "WEST")
                          (= (:nswe @player-map) "EAST") (swap! player-map assoc :nswe "SOUTH"))
     (= action "MOVE") (cond
                         (and (= (:nswe @player-map) "NORTH") (< (:position_y @player-map) 4)) (swap! player-map update :position_y inc)
                         (and (= (:nswe @player-map) "EAST") (< (:position_x @player-map) 4)) (swap! player-map update :position_x inc)
                         (and (= (:nswe @player-map) "SOUTH") (> (:position_y @player-map) 0)) (swap! player-map update :position_y dec)
                         (and (= (:nswe @player-map) "WEST") (> (:position_x @player-map) 0)) (swap! player-map update :position_x dec))))

;; Updated player name
 (defn update_name_player [key nameloop player-map]
   [;; For the player's name, only letters are allowed
    (cond
      ;; Added a letter to the player's name
      (contains? alphabet key)  (swap! player-map (fn [m] (assoc m :name (str (:name m) key))))
      ;; Remove the last letter from the player's name
      (= key :backspace) (swap! player-map (fn [m] (assoc m :name (subs (:name m) 0 (- (count (:name m)) 1)))))
      ;; Validation
      (and (= key :enter) (not= (:name @player-map) "")) (reset! nameloop true)
      ;; Else
      () ())])

;; Name selection window
 (defn name_selection_window [term player-map]
   [(let [nameloop (atom false)]
      (text_display term [[1 1] "Hello, Player!" [1 3] "What's your Name ?" [1 5]])
      (while (not= @nameloop true)
        (let [key (t/get-key-blocking term)]
          (update_name_player key nameloop player-map)
          ;; Reset the player name line if it has been modified
          (if (not= key :enter)
            (text_display term [[1 5] empty_string
                                [1 5] (:name @player-map)]) ()))))])

;; Testing the contents of data.txt
 (defn test_place [tokens error_message]
   (cond
     ;; The file doesn't exist
     (= (nth tokens 0) "File don't exist") (reset! error_message (str "File " (nth tokens 1) " don't exist"))
     ;; PLACE isn't the 1st element
     (= tokens ["File exist"]) (reset! error_message "PLACE isn't found at start of data")
     ;; There aren't 4 elements
     (not= (count tokens) 4) (reset! error_message "Incorrect element number")
     ;; The X coordinate isn't equal to 0, 1, 2, 3 or 4
     (not (contains? #{"0" "1" "2" "3" "4"} (nth tokens 1))) (reset! error_message "Element X incorrect")
     ;; The Y coordinate isn't equal to 0, 1, 2, 3 or 4
     (not (contains? #{"0" "1" "2" "3" "4"} (nth tokens 2))) (reset! error_message "Element Y incorrect")
     ;; Orientation doesn't equal NORTH, WEST, EAST or SOUTH
     (not (contains? #{"NORTH" "WEST" "EAST" "SOUTH"} (nth tokens 3))) (reset! error_message "Element orientation incorrect")
     ;; data OK
     :else (reset! error_message "no_error")))

;; Read the data_test.txt file
 (defn read_data_txt [player-map error_message]
   (if (.exists (io/file name_data_text))
     (with-open [rdr (io/reader name_data_text)]
       ;;  The function (some) allows you to exit the loop as soon as "PLACE" is found
       (doseq [line (line-seq rdr)]
         (let [tokens (str/split line #",")]
           (cond
             (= "PLACE" (first tokens)) [(test_place tokens error_message)
                                         (if (= @error_message "no_error")
                                           [(swap! player-map assoc :nswe (nth tokens 3))
                                            (swap! player-map assoc :position_x (Integer/parseInt (nth tokens 1)))
                                            (swap! player-map assoc :position_y (Integer/parseInt (nth tokens 2)))] ())]
             (and (= "MOVE" (first tokens)) (= @error_message "no_error")) (motion player-map "MOVE")
             (and (= "LEFT" (first tokens)) (= @error_message "no_error")) (motion player-map "LEFT")
             (and (= "RIGHT" (first tokens)) (= @error_message "no_error")) (motion player-map "RIGHT")))))
     (reset! error_message ["File don't exist" name_data_text])))

;; Data_test.txt file reading window
 (defn file_reading_window [term keep_data_text player-map]
   (let [error_message (atom "") readloop (atom false)]
     (read_data_txt player-map error_message)
     (t/clear term)
     (while (= @readloop false)
       (if (= @error_message "no_error")
        ;; no error
         [(text_display term [[1 1] (str "Reading " name_data_text) [1 3]
                              "Data found ... " [1 5] (str "X -> " (:position_x @player-map))
                              (str "   Y -> " (:position_y @player-map))
                              [1 6] (str "You look to the " (:nswe @player-map)) [1 8]
                              "Do you want to keep this data? (o/n)" [1 10]])
          (let [key (t/get-key-blocking term)]
            (cond
              (or (= key \o) (= key \O)) [(reset! readloop true) (reset! keep_data_text true)]
              (or (= key \n) (= key \N)) (reset! readloop true)))]
        ;; error reading data
         [(text_display term [@error_message [1 5] "The correct syntax is  PLACE,X,Y,Orientation"
                              [1 6] "X -> 0...4" [1 7] "Y -> 0...4"
                              [1 8] "Orientation -> NORTH,WEST,EAST or SOUTH"
                              [1 10] "Ex: PLACE,1,2,NORTH" [1 12] "Press Enter to continue"])
          (let [key (t/get-key-blocking term)]
            (if (or (= key :enter) (= key :return)) (reset! readloop true) ()))]))))

;; Choose the starting coordinates window
 (defn starting_coordinates_window [term player-map]
   [(t/clear term)
    (text_display term [[1 1] (str "Welcome to Toy Robot Simulator " (:name @player-map))
                        [1 3] "To start the game," [1 4] "choose the starting coordinates"
                        [1 5] "between 0 and 4 on X and Y for your robot" [1 7] "in X :" [1 9]])
    (let [startloop (atom false)]
      (while (= @startloop false)
        (let [key (t/get-key-blocking term)]
          (if (contains? #{\0 \1 \2 \3 \4} key) [(text_display term [[1 9] empty_string [1 9] (str key)])
                                                 (swap! player-map (fn [m] (assoc m :position_x (- (int key) (int \0)))))
                                                 (reset! startloop true)]
              (text_display term [(str "Bad Coordinate " (:name @player-map) "  Retry") [1 9]])))))
    (text_display term [[1 11] "in Y :" [1 13]])
    (let [startloop (atom false)]
      (while (= @startloop false)
        (let [key (t/get-key-blocking term)]
          (if (contains? #{\0 \1 \2 \3 \4} key) [(text_display term [[1 13] empty_string [1 13] (str key)])
                                                 (swap! player-map (fn [m] (assoc m :position_y (- (int key) (int \0)))))
                                                 (reset! startloop true)]
              (text_display term [(str "Bad Coordinate " (:name @player-map) "  Retry") [1 13]])))))
    (text_display term [[1 15] "what orientation (z or up to NORTH) (q or left to WEST) (d or right to EAST) (s or down to SOUTH) :"
                        [1 17]])
    (let [startloop (atom false)]
      (while (= @startloop false)
        (let [key (t/get-key-blocking term)]
          (if (contains? #{\z \q \d \s :up :left :right :down} key)
            [(text_display term [[1 17] empty_string [1 17] (str key)])
             (select_orientation key player-map)
             (reset! startloop true)]
            [(t/put-string term (str "Bad orientation " (:name @player-map) "  Retry")) (t/move-cursor term 1 17)]))))])

;; Robot movement window
 (defn robot_movement_window [term player-map generaloop motionloop]
   [(t/clear term)
    (text_display term [[1 1] "Press Z or UP to MOVE" [1 2] "Press Q or LEFT to turn LEFT"
                        [1 3] "Press D or RIGHT to turn RIGHT"
                        [1 4] "Press BACKTAB to change initial coordinates"
                        [1 5] "Press X or Escape to quit the game"
                        [1 7] (str "You look to the " (:nswe @player-map))
                        [1 8] (str "in X:" (:position_x @player-map) " Y:" (:position_y @player-map)) [1 10]])
    (let [moveloop (atom false)]
      (while (= @moveloop false)
        (let [key (t/get-key-blocking term)]
          (text_display term [[1 10] empty_string [1 10]])
          (cond
            (or (= key \z) (= key :up)) [(let [test_coor (str (:position_x @player-map) (:position_y @player-map))]
                                           (motion player-map "MOVE")
                                           (if (= test_coor (str (:position_x @player-map) (:position_y @player-map)))
                                             (t/put-string term "Impossible your robot is near to board") (reset! moveloop true)))]
            (or (= key \q) (= key :left)) [(motion player-map "LEFT") (reset! moveloop true)]
            (or (= key \d) (= key :right)) [(motion player-map "RIGHT") (reset! moveloop true)]
            (= key :backspace) [(reset! moveloop true) (reset! motionloop true)]
            (or (= key \x) (= key :escape)) [(reset! moveloop true) (reset! motionloop true) (reset! generaloop true)]))))])

;; Exit window
 (defn exit_window [term player-map]
   [(t/clear term)
    (text_display term [[1 1] (str "Thanks for playing " (:name @player-map)) [1 5] "Press any button to exit"])
    (t/get-key-blocking term)])

;; Main Fonction
 (defn game [term]
   (let [player-map (atom {:name "" :position_x nil :position_y nil :nswe nil}) ;; Keyword structure to manage player information
         keep_data_text (atom false)]
    ;; #### Reading player name ####
     (name_selection_window term player-map)
    ;; #### Reading the data_test.txt file for starting coordinates ####
     (file_reading_window term keep_data_text player-map)
    ;; #### General loop -> reset starting coordinates + robot movement
     (let [generaloop (atom false)]
       (while (= @generaloop false)
         (do
      ;; #### Request starting coordinates ####
           (if (= @keep_data_text false) (starting_coordinates_window term player-map) (reset! keep_data_text false))
      ;; #### Motion loop ####
           (let [motionloop (atom false)]
             (while (= @motionloop false)
               (robot_movement_window term player-map generaloop motionloop))))))
     (exit_window term player-map)))


;; Start fonction
 (defn -main
   [& args]

   (println "Hello, Player!")

   (let [term (t/get-terminal :swing)]
     (t/in-terminal term
                    (game term)))

   (println "Bye, Player!"))
