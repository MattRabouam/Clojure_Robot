(ns getting-started.core
  (:gen-class))

;; To display a terminal and read only one character from the keyboard
(require '[lanterna.terminal :as t])

;; To read data.txt
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(def name_data_text (atom "data_test.txt"))
(def in_data_text (atom nil))
(def keep_data_text (atom false))

;; For the player's name, only letters are allowed
(def alphabet (atom "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàâäéèêëïîôöùûüÿçÀÂÄÉÈÊËÏÎÔÖÙÛÜŸÇ"))

;; Used to delete a line in the terminal
(def empty_string (atom "                                          "))

;; Valid number in X and Y
(def valid_X_Y (atom #{"0" "1" "2" "3" "4"}))

;; Valid Orientation
(def valid_orientation (atom #{"NORTH" "WEST" "EAST" "SOUTH"}))

;; Keyword structure to manage player information
(def player-map (atom {:name "" :position_x nil :position_y nil :nswe nil}))
(def quitloop (atom false))
(def moveloop (atom false))
(def reloop (atom false))
(def presskey (atom nil))
(def to_display (atom nil))

;; Read the data_test.txt file
(defn read_data_txt [] (if (.exists (io/file @name_data_text))
                         [(reset! in_data_text ["File exist"])
                          (with-open [rdr (io/reader @name_data_text)]
                            ;;  The function (some) allows you to exit the loop as soon as "PLACE" is found
                            (some (fn [line]
                                    (let [tokens (str/split line #",")]
                                      (when (= "PLACE" (first tokens))
                                        [(reset! in_data_text tokens) true])))
                                  (line-seq rdr)))]
                         (reset! in_data_text ["File don't exist" @name_data_text])))

;; Testing the contents of data.txt
(defn test_in_data_text [] (cond
                             ;; The file doesn't exist
                             (= (nth @in_data_text 0) "File don't exist") (reset! in_data_text (str "File " (nth @in_data_text 1) " don't exist"))
                             ;; PLACE isn't the 1st element
                             (= @in_data_text ["File exist"]) (reset! in_data_text "PLACE isn't found at start of data")
                             ;; There aren't 4 elements
                             (not= (count @in_data_text) 4) (reset! in_data_text "Incorrect element number")
                             ;; The X coordinate isn't equal to 0, 1, 2, 3 or 4
                             (not (contains? @valid_X_Y (nth @in_data_text 1))) (reset! in_data_text "Element X incorrect")
                             ;; The Y coordinate isn't equal to 0, 1, 2, 3 or 4
                             (not (contains? @valid_X_Y (nth @in_data_text 2))) (reset! in_data_text "Element Y incorrect")
                             ;; Orientation doesn't equal NORTH, WEST, EAST or SOUTH
                             (not (contains? @valid_orientation (nth @in_data_text 3))) (reset! in_data_text "Element orientation incorrect")
                             ;; data OK
                             :else true
                             ))

;; Moves the cursor to a specific coordinate and displays text on the terminal
(defn text_display [term tab_text] (doseq [i tab_text]
                                     (if (string? i)
                                       (t/put-string term i)
                                       (t/move-cursor term (get-in i [0]) (get-in i [1])))))

;; Choose an orientation according to the selected key
(defn select_orientation [] (cond
                              (or (= @presskey \z) (= @presskey :up)) (swap! player-map assoc :nswe "NORTH")
                              (or (= @presskey \q) (= @presskey :left)) (swap! player-map assoc :nswe "WEST")
                              (or (= @presskey \d) (= @presskey :right)) (swap! player-map assoc :nswe "EAST")
                              (or (= @presskey \s) (= @presskey :down)) (swap! player-map assoc :nswe "SOUTH")))

;; Move the robot forward one space or display the message "Impossible your robot is near to board"
(defn move_forward [] (cond
                        (= (:nswe @player-map) "NORTH") (if (< (:position_y @player-map) 4)
                                                          [(swap! player-map update :position_y inc) (reset! moveloop true)]
                                                          (str "watch out edge"))
                        (= (:nswe @player-map) "EAST") (if (< (:position_x @player-map) 4)
                                                         [(swap! player-map update :position_x inc) (reset! moveloop true)]
                                                         (str "watch out edge"))
                        (= (:nswe @player-map) "SOUTH") (if (> (:position_y @player-map) 0)
                                                          [(swap! player-map update :position_y dec) (reset! moveloop true)]
                                                          (str "watch out edge"))
                        (= (:nswe @player-map) "WEST") (if (> (:position_x @player-map) 0)
                                                         [(swap! player-map update :position_x dec) (reset! moveloop true)]
                                                         (str "watch out edge"))))

;; the robot turns to the left
(defn turn_left [] (cond
                     (= (:nswe @player-map) "NORTH") [(swap! player-map assoc :nswe "WEST") (reset! moveloop true)]
                     (= (:nswe @player-map) "WEST") [(swap! player-map assoc :nswe "SOUTH") (reset! moveloop true)]
                     (= (:nswe @player-map) "SOUTH") [(swap! player-map assoc :nswe "EAST") (reset! moveloop true)]
                     (= (:nswe @player-map) "EAST") [(swap! player-map assoc :nswe "NORTH") (reset! moveloop true)]))

;; the robot turns to the right
(defn turn_right [] (cond
                      (= (:nswe @player-map) "NORTH") [(swap! player-map assoc :nswe "EAST") (reset! moveloop true)]
                      (= (:nswe @player-map) "WEST") [(swap! player-map assoc :nswe "NORTH") (reset! moveloop true)]
                      (= (:nswe @player-map) "SOUTH") [(swap! player-map assoc :nswe "WEST") (reset! moveloop true)]
                      (= (:nswe @player-map) "EAST") [(swap! player-map assoc :nswe "SOUTH") (reset! moveloop true)]))

;; Updated player name
(defn update_name_player [] (cond
                              ;; Added a letter to the player's name
                              (contains? (set @alphabet) @presskey)  (swap! player-map (fn [m] (assoc m :name (str (:name m) @presskey))))
                              ;; Remove the last letter from the player's name
                              (= @presskey :backspace) (swap! player-map (fn [m] (assoc m :name (subs (:name m) 0 (- (count (:name m)) 1)))))
                              ;; Validation
                              (and (= @presskey :enter) (not= (:name @player-map) "")) (reset! quitloop true)
                              ;; Else
                              () (reset! quitloop "no")))

;; Name selection window
(defn name_selection_window [term] [(text_display term [[1 1] "Hello, Player!" [1 3] "What's your Name ?" [1 5]])
                                    (reset! quitloop false)
                                    (while (not= @quitloop true)
                                      (do
                                        (let [key (t/get-key-blocking term)] (reset! presskey key))
                                        (update_name_player)
                                      ;; Reset the player name line if it has been modified
                                        (if (and (not= @quitloop "no") (not= @presskey :enter))
                                          (text_display term [[1 5] @empty_string
                                                              [1 5] (:name @player-map)]) ())))])

;; Data_test.txt file reading window
(defn file_reading_window [term] [(read_data_txt)
                                  (reset! quitloop false)
                                  (while (= @quitloop false)
                                    (do
                                      (t/clear term)
                                      (text_display term [[1 1] (str "Reading " @name_data_text) [1 3]])
                                      (if (= (test_in_data_text) true)
                                        [(text_display term ["Data found ... " [1 5] (str "X -> " (nth @in_data_text 1))
                                                             (str "   Y -> " (nth @in_data_text 2))
                                                             [1 6] (str "You look to the " (nth @in_data_text 3)) [1 8]
                                                             "Do you want to keep this data? (o/n)" [1 10]])
                                         (let [key (t/get-key-blocking term)] (reset! presskey key))
                                         (cond
                                           (or (= @presskey \o) (= @presskey \O)) [(swap! player-map assoc :position_x (Integer/parseInt (nth @in_data_text 1)))
                                                                                   (swap! player-map assoc :position_y (Integer/parseInt (nth @in_data_text 2)))
                                                                                   (swap! player-map assoc :nswe (nth @in_data_text 3))
                                                                                   (reset! quitloop true)
                                                                                   (reset! keep_data_text true)]
                                           (or (= @presskey \n) (= @presskey \N)) (reset! quitloop true))]
                                        [(text_display term [@in_data_text [1 5] "The correct syntax is  PLACE,X,Y,Orientation"
                                                             [1 6] "X -> 0...4" [1 7] "Y -> 0...4" 
                                                             [1 8] "Orientation -> NORTH,WEST,EAST or SOUTH"
                                                             [1 10] "Ex: PLACE,1,2,NORTH" [1 12] "Press Enter to continue"])
                                         (let [key (t/get-key-blocking term)] (reset! presskey key))
                                         (if (or (= @presskey :enter) (= @presskey :return)) (reset! quitloop true) ())])))])

;; Choose the starting coordinates window
(defn starting_coordinates_window [term] [(t/clear term)
                                          (text_display term [[1 1] (str "Welcome to Toy Robot Simulator " (:name @player-map))
                                                              [1 3] "To start the game," [1 4] "choose the starting coordinates"
                                                              [1 5] "between 0 and 4 on X and Y for your robot" [1 7] "in X :" [1 9]])
                                          (reset! reloop false)
                                          (while (= @reloop false)
                                            (do
                                              (let [key (t/get-key-blocking term)] (reset! presskey key))
                                              (if (contains? #{\0 \1 \2 \3 \4} @presskey) [(text_display term [[1 9] @empty_string [1 9] (str @presskey)])
                                                                                           (swap! player-map (fn [m] (assoc m :position_x (- (int @presskey) (int \0)))))
                                                                                           (reset! reloop true)]
                                                  (text_display term [(str "Bad Coordinate " (:name @player-map) "  Retry") [1 9]]))))
                                          (text_display term [[1 11] "in Y :" [1 13]])
                                          (reset! reloop false)
                                          (while (= @reloop false)
                                            (do
                                              (let [key (t/get-key-blocking term)] (reset! presskey key))
                                              (if (contains? #{\0 \1 \2 \3 \4} @presskey) [(text_display term [[1 13] @empty_string [1 13] (str @presskey)])
                                                                                           (swap! player-map (fn [m] (assoc m :position_y (- (int @presskey) (int \0)))))
                                                                                           (reset! reloop true)]
                                                  (text_display term [(str "Bad Coordinate " (:name @player-map) "  Retry") [1 13]]))))
                                          (text_display term [[1 15] "what orientation (z or up to NORTH) (q or left to WEST) (d or right to EAST) (s or down to SOUTH) :"
                                                              [1 17]])
                                          (reset! reloop false)
                                          (while (= @reloop false)
                                            (do
                                              (let [key (t/get-key-blocking term)] (reset! presskey key))
                                              (if (contains? #{\z \q \d \s :up :left :right :down} @presskey)
                                                [(text_display term [[1 17] @empty_string [1 17] (str @presskey)])
                                                 (select_orientation)
                                                 (reset! reloop true)]
                                                [(t/put-string term (str "Bad orientation " (:name @player-map) "  Retry")) (t/move-cursor term 1 17)])))])

;; Robot movement window
(defn robot_movement_window [term] [(t/clear term)
                                    (text_display term [[1 1] "Press Z or UP to MOVE" [1 2] "Press Q or LEFT to turn LEFT"
                                                        [1 3] "Press D or RIGHT to turn RIGHT"
                                                        [1 4] "Press BACKTAB to change initial coordinates"
                                                        [1 5] "Press X or Escape to quit the game"
                                                        [1 7] (str "You look to the " (:nswe @player-map))
                                                        [1 8] (str "in X:" (:position_x @player-map) " Y:" (:position_y @player-map)) [1 10]])
                                    (reset! moveloop false)
                                    (while (= @moveloop false)
                                      (do
                                        (let [key (t/get-key-blocking term)] (reset! presskey key))
                                        (text_display term [[1 10] @empty_string [1 10]])
                                        (cond
                                          (or (= @presskey \z) (= @presskey :up))
                                          [(reset! to_display (move_forward)) (if (= @to_display "watch out edge")
                                                                                (t/put-string term "Impossible your robot is near to board") ())]
                                          (or (= @presskey \q) (= @presskey :left)) (turn_left)
                                          (or (= @presskey \d) (= @presskey :right)) (turn_right)
                                          (= @presskey :backspace) [(reset! moveloop true) (reset! reloop true)]
                                          (or (= @presskey \x) (= @presskey :escape)) [(reset! moveloop true) (reset! reloop true) (reset! quitloop true)])))])

;; Exit window
(defn exit_window [term] [(t/clear term)
                           (text_display term [[1 1] (str "Thanks for playing " (:name @player-map)) [1 5] "Press any button to exit"])
                           (t/get-key-blocking term)])

;; Main Fonction
(defn game [term]
  ;; #### Reading player name ####
  (name_selection_window term)
  ;; #### Reading the data_test.txt file for starting coordinates ####
  (file_reading_window term)
  (reset! quitloop false)
  (while (= @quitloop false)
    (do
      ;; #### Request starting coordinates ####
      (if (= @keep_data_text false) (starting_coordinates_window term) (reset! keep_data_text false))
      
      (reset! reloop false)
      (while (= @reloop false)
          ;; #### Motion loop ####
          (robot_movement_window term))
      )
    )
  (exit_window term)
  )


;; Start fonction
(defn -main
  [& args]

  (println "Hello, Player!")

  (let [term (t/get-terminal :swing)]
    (t/in-terminal term
                   (game term)))

  (println "Bye, Player!"))

