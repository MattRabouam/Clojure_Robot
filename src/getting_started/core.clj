(ns getting-started.core
  (:gen-class))

;; Pour afficher un terminal et lire qu'un caractère du clavier
(require '[lanterna.terminal :as t])

(def alphabet (atom "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàâäéèêëïîôöùûüÿçÀÂÄÉÈÊËÏÎÔÖÙÛÜŸÇ"))
(def empty_string (atom "                                          "))
(def NamePlayer (atom ""))
(def xplayer (atom nil))
(def yplayer (atom nil))
(def nsweplayer (atom nil))
(def quitloop (atom false))
(def moveloop (atom false))
(def reloop (atom false))
(def presskey (atom nil))

(defn -main
  [& args]

  (println "Hello, Player!")

  (let [term (t/get-terminal :swing)]
    (t/in-terminal term
                  ;;  Lecture du nom du joueur
                   (t/move-cursor term 1 1)
                   (t/put-string term "Hello, Player!")
                   (t/move-cursor term 1 3)
                   (t/put-string term "What's your Name ?")
                   (t/move-cursor term 1 5)
                   (reset! quitloop false)
                   (while (= @quitloop false)
                     (do
                       (let [key (t/get-key-blocking term)] (reset! presskey key))
                       (cond
                         (contains? (set @alphabet) @presskey) [(swap! NamePlayer #(str % @presskey)) (t/put-character term @presskey)]
                         (and (= @presskey :enter) (not= @NamePlayer "")) (reset! quitloop true)
                         (= @presskey :backspace) [(swap! NamePlayer #(subs % 0 (dec (count %)))) (t/move-cursor term 1 5) (t/put-string term @empty_string) (t/move-cursor term 1 5) (t/put-string term @NamePlayer)])))
                  ;;  Demande des coordonnées de départ
                   (reset! quitloop false)
                   (while (= @quitloop false)
                     (do
                       (t/clear term)
                       (t/move-cursor term 1 1)
                       (t/put-string term (str "Welcome to Toy Robot Simulator " @NamePlayer))
                       (t/move-cursor term 1 3)
                       (t/put-string term "To start the game,")
                       (t/move-cursor term 1 4)
                       (t/put-string term "choose the starting coordinates")
                       (t/move-cursor term 1 5)
                       (t/put-string term "between 0 and 4 on X and Y for your robot")
                       (t/move-cursor term 1 7)
                       (t/put-string term "in X :")
                       (t/move-cursor term 1 9)
                       (reset! reloop false)
                       (while (= @reloop false)
                         (do
                           (let [key (t/get-key-blocking term)] (reset! presskey key))
                           (if (contains? #{\0 \1 \2 \3 \4} @presskey) [(t/move-cursor term 1 9) (t/put-string term @empty_string)
                                                                        (t/move-cursor term 1 9) (t/put-string term (str @presskey)) (reset! xplayer (- (int @presskey) (int \0))) (reset! reloop true)]
                               [(t/put-string term (str "Bad Coordinate " @NamePlayer "  Retry")) (t/move-cursor term 1 9)])))
                       (t/move-cursor term 1 11)
                       (t/put-string term "in Y :")
                       (t/move-cursor term 1 13)
                       (reset! reloop false)
                       (while (= @reloop false)
                         (do
                           (let [key (t/get-key-blocking term)] (reset! presskey key))
                           (if (contains? #{\0 \1 \2 \3 \4} @presskey) [(t/move-cursor term 1 13) (t/put-string term @empty_string)
                                                                        (t/move-cursor term 1 13) (t/put-string term (str @presskey)) (reset! yplayer (- (int @presskey) (int \0))) (reset! reloop true)]
                               [(t/put-string term (str "Bad Coordinate " @NamePlayer "  Retry")) (t/move-cursor term 1 13)])))
                       (t/move-cursor term 1 15)
                       (t/put-string term "what orientation (z or up to NORTH) (q or left to WEST) (d or right to EAST) (s or down to SOUTH) :")
                       (t/move-cursor term 1 17)
                       (reset! reloop false)
                       (while (= @reloop false)
                         (do
                           (let [key (t/get-key-blocking term)] (reset! presskey key))
                           (if (contains? #{\z \q \d \s :up :left :right :down} @presskey)
                             [(t/move-cursor term 1 17) (t/put-string term @empty_string) (t/move-cursor term 1 17)
                              (t/put-string term (str @presskey))
                              (cond
                                (or (= @presskey \z) (= @presskey :up)) (reset! nsweplayer "NORTH")
                                (or (= @presskey \q) (= @presskey :left)) (reset! nsweplayer "WEST")
                                (or (= @presskey \d) (= @presskey :right)) (reset! nsweplayer "EAST")
                                (or (= @presskey \s) (= @presskey :down)) (reset! nsweplayer "SOUTH"))
                              (reset! reloop true)]
                             [(t/put-string term (str "Bad orientation " @NamePlayer "  Retry")) (t/move-cursor term 1 17)])))
                  ;;  Boucle de mouvement
                       (reset! reloop false)
                       (while (= @reloop false)
                         (do
                           (t/clear term)
                           (t/move-cursor term 1 1)
                           (t/put-string term "Press Z or UP to MOVE")
                           (t/move-cursor term 1 2)
                           (t/put-string term "Press Q or LEFT to turn LEFT")
                           (t/move-cursor term 1 3)
                           (t/put-string term "Press D or RIGHT to turn RIGHT")
                           (t/move-cursor term 1 4)
                           (t/put-string term "Press BACKTAB to change initial coordinates")
                           (t/move-cursor term 1 5)
                           (t/put-string term "Press X or Escape to quit the game")
                           (t/move-cursor term 1 7)
                           (t/put-string term (str "You look to the " @nsweplayer))
                           (t/move-cursor term 1 8)
                           (t/put-string term (str "in X:" @xplayer " Y:" @yplayer))
                           (t/move-cursor term 1 10)
                           (reset! moveloop false)
                           (while (= @moveloop false)
                             (do
                               (let [key (t/get-key-blocking term)] (reset! presskey key))
                               (t/move-cursor term 1 10)
                               (t/put-string term @empty_string)
                               (t/move-cursor term 1 10)
                               (cond
                                 (or (= @presskey \z) (= @presskey :up)) [(cond
                                                                            (= @nsweplayer "NORTH") (if (< @yplayer 4) [(swap! yplayer inc) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))
                                                                            (= @nsweplayer "EAST") (if (< @xplayer 4) [(swap! xplayer inc) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))
                                                                            (= @nsweplayer "SOUTH") (if (> @yplayer 0) [(swap! yplayer dec) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board"))
                                                                            (= @nsweplayer "WEST") (if (> @xplayer 0) [(swap! xplayer dec) (reset! moveloop true)] (t/put-string term "Impossible your robot is near to board")))]
                                 (or (= @presskey \q) (= @presskey :left)) [(cond
                                                                              (= @nsweplayer "NORTH") [(reset! nsweplayer "WEST") (reset! moveloop true)]
                                                                              (= @nsweplayer "WEST") [(reset! nsweplayer "SOUTH") (reset! moveloop true)]
                                                                              (= @nsweplayer "SOUTH") [(reset! nsweplayer "EAST") (reset! moveloop true)]
                                                                              (= @nsweplayer "EAST") [(reset! nsweplayer "NORTH") (reset! moveloop true)])]
                                 (or (= @presskey \d) (= @presskey :right)) [(cond
                                                                               (= @nsweplayer "NORTH") [(reset! nsweplayer "EAST") (reset! moveloop true)]
                                                                               (= @nsweplayer "WEST") [(reset! nsweplayer "NORTH") (reset! moveloop true)]
                                                                               (= @nsweplayer "SOUTH") [(reset! nsweplayer "WEST") (reset! moveloop true)]
                                                                               (= @nsweplayer "EAST") [(reset! nsweplayer "SOUTH") (reset! moveloop true)])]
                                 (= @presskey :backspace) [(reset! moveloop true) (reset! reloop true)]
                                 (or (= @presskey \x) (= @presskey :escape)) [(reset! moveloop true) (reset! reloop true) (reset! quitloop true)])))))))
                   (t/clear term)
                   (t/move-cursor term 1 1)
                   (t/put-string term (str "Thanks for playing " @NamePlayer))
                   (t/move-cursor term 1 5)
                   (t/put-string term "Press any button to exit")
                   (t/get-key-blocking term)))

                   (println "Bye, Player!"))

