(ns getting-started.core
  (:gen-class))

;; Pour afficher un terminal et lire qu'un caractère du clavier
(require '[lanterna.terminal :as t])

;; For the player's name, only letters are allowed
(def alphabet (atom "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàâäéèêëïîôöùûüÿçÀÂÄÉÈÊËÏÎÔÖÙÛÜŸÇ"))
;; Used to delete a line in the terminal
(def empty_string (atom "                                          "))
;; Keyword structure to manage player information
(def player-map (atom {:name "" :position_x nil :position_y nil :nswe nil}))
(def quitloop (atom false))
(def moveloop (atom false))
(def reloop (atom false))
(def presskey (atom nil))

;; Moves the cursor to a specific coordinate and displays text on the terminal
(defn text_display [term tab_text] (doseq [i tab_text]
                                     (if (string? i) (t/put-string term i) (t/move-cursor term (get-in i [0]) (get-in i [1])))))

;; Choose an orientation according to the selected key
(defn select_orientation [] (cond
                              (or (= @presskey \z) (= @presskey :up)) (swap! player-map assoc :nswe "NORTH")
                              (or (= @presskey \q) (= @presskey :left)) (swap! player-map assoc :nswe "WEST")
                              (or (= @presskey \d) (= @presskey :right)) (swap! player-map assoc :nswe "EAST")
                              (or (= @presskey \s) (= @presskey :down)) (swap! player-map assoc :nswe "SOUTH")))

;; Move the robot forward one space or display the message "Impossible your robot is near to board"
(defn move_forward [term] (cond
                            (= (:nswe @player-map) "NORTH") (if (< (:position_y @player-map) 4) [(swap! player-map update :position_y inc) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))
                            (= (:nswe @player-map) "EAST") (if (< (:position_x @player-map) 4) [(swap! player-map update :position_x inc) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))
                            (= (:nswe @player-map) "SOUTH") (if (> (:position_y @player-map) 0) [(swap! player-map update :position_y dec) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))
                            (= (:nswe @player-map) "WEST") (if (> (:position_x @player-map) 0) [(swap! player-map update :position_x dec) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))))

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


;; Main Fonction
(defn game [term]
  ;; #### Lecture du nom du joueur ####
  (text_display term [[1 1] "Hello, Player!" [1 3] "What's your Name ?" [1 5]])
  (reset! quitloop false)
  (while (= @quitloop false)
    (do
      (let [key (t/get-key-blocking term)] (reset! presskey key))
      (cond
        (contains? (set @alphabet) @presskey) [(swap! player-map (fn [m] (assoc m :name (str (:name m) @presskey)))) (t/put-character term @presskey)]
        (and (= @presskey :enter) (not= (:name @player-map) "")) (reset! quitloop true)
        (= @presskey :backspace) [(swap! player-map (fn [m] (assoc m :name (subs (:name m) 0 (- (count (:name m)) 1))))) (text_display term [[1 5] @empty_string [1 5] (:name @player-map)])])))
  ;; #### Demande des coordonnées de départ ####
  (reset! quitloop false)
  (while (= @quitloop false)
    (do
      (t/clear term)
      (text_display term [[1 1] (str "Welcome to Toy Robot Simulator " (:name @player-map)) [1 3] "To start the game," [1 4] "choose the starting coordinates"
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
      (text_display term [[1 15] "what orientation (z or up to NORTH) (q or left to WEST) (d or right to EAST) (s or down to SOUTH) :" [1 17]])
      (reset! reloop false)
      (while (= @reloop false)
        (do
          (let [key (t/get-key-blocking term)] (reset! presskey key))
          (if (contains? #{\z \q \d \s :up :left :right :down} @presskey)
            [(text_display term [[1 17] @empty_string [1 17] (str @presskey)])
             (select_orientation)
             (reset! reloop true)]
            [(t/put-string term (str "Bad orientation " (:name @player-map) "  Retry")) (t/move-cursor term 1 17)])))
      ;; #### Boucle de mouvement ####
      (reset! reloop false)
      (while (= @reloop false)
        (do
          (t/clear term)
          (text_display term [[1 1] "Press Z or UP to MOVE" [1 2] "Press Q or LEFT to turn LEFT" [1 3] "Press D or RIGHT to turn RIGHT"
                              [1 4] "Press BACKTAB to change initial coordinates" [1 5] "Press X or Escape to quit the game"
                              [1 7] (str "You look to the " (:nswe @player-map)) [1 8] (str "in X:" (:position_x @player-map) " Y:" (:position_y @player-map)) [1 10]])
          (reset! moveloop false)
          (while (= @moveloop false)
            (do
              (let [key (t/get-key-blocking term)] (reset! presskey key))
              (text_display term [[1 10] @empty_string [1 10]])
              (cond
                (or (= @presskey \z) (= @presskey :up)) (move_forward term)
                (or (= @presskey \q) (= @presskey :left)) (turn_left)
                (or (= @presskey \d) (= @presskey :right)) (turn_right)
                (= @presskey :backspace) [(reset! moveloop true) (reset! reloop true)]
                (or (= @presskey \x) (= @presskey :escape)) [(reset! moveloop true) (reset! reloop true) (reset! quitloop true)])))))))
  (t/clear term)
  (text_display term [[1 1] (str "Thanks for playing " (:name @player-map)) [1 5] "Press any button to exit"])
  (t/get-key-blocking term))


;; Start fonction
(defn -main
  [& args]

  (println "Hello, Player!")

  (let [term (t/get-terminal :swing)]
    (t/in-terminal term
                   (game term)))

  (println "Bye, Player!"))

