# Version 4.0

- [DO] Change the “motion” function to make it as “pure” as possible.


# Version 3.0

- Updated the code to remove unnecessary atoms, put the atoms used for loops in the functions directly to avoid potential bugs and try to include "pure" functions.

- Change the reading of the input file so that it can read a series of instructions.

- updating unit tests


# Version 2.0

- Learn to use Git and integrate it properly into my program. Create a “dev” branch.

- Break down my code with functions and use a structure.

- Learn and use unit tests. Using core_test.clj and lein test to lauch. I changed a few functions to make it work better for testing.

- Incorporate file reading and unit tests.

- Each interface window is put in functions.

- Create the interface for reading the data_test.txt file.


# Version 1.0

# Clojure_Robot
The application is a simulation of a toy robot moving on a square tabletop, of dimensions 5 units x 5 units.

Screen shots :

![terminal1](https://github.com/MattRabouam/Clojure_Robot/assets/165649816/c83f4353-9d3c-48f7-91ac-ff9e61de0ff9)

![terminal2](https://github.com/MattRabouam/Clojure_Robot/assets/165649816/6d84852c-f009-40e0-97ac-6e64442fd700)

![terminal3](https://github.com/MattRabouam/Clojure_Robot/assets/165649816/4e025873-55cf-426c-8886-b1e031e4c156)

Code Design: 

When I had to do this exercise, I did not know Clojure. 
So, I looked up how to use this language with VSCode. 
I installed Leiningen. I created a new project that I launch with lein run. 
I started creating the program using println to display text and read-line to read an input but I didn’t find it practical for a game. 
So, I found the clojure-lanterna module which allows opening a window and reading a key press with get-key-blocking which allowed me to make a more interactive game. 
I used while loops to verify that the inputs were correct (for example 0, 1, 2,3 and 4 for the initial coordinate input). 
I used the cond function to guide the program towards the different possible choices. 
I defined my variables as atoms, I don’t know if it was the best choice but it allowed me to have precise control over them. 
Once the tests and bug fixes were done, I added this code to GitHub. (I’m not used to developing on GitHub)

Conclusion: 

I had a lot of fun coding this. 
Clojure is quite atypical but once the logic is understood it’s quite pleasant and you can do a lot of things with just one line of code. 
To understand its operation that I did not know, I proceeded by the method of trial and error to understand how each function reacted to different situations, which allowed me to be comfortable with these functions now. 
I’m not used to developing on GitHub. 
I usually go there to retrieve programs or to give me code ideas but this is the first program that I post here, thank you for this opportunity 😉.

Original exercice :

Toy Robot Simulator

Description

● The application is a simulation of a toy robot moving on a square tabletop, of
dimensions 5 units x 5 units.

● There are no other obstructions on the table surface.

● The robot is free to roam around the surface of the table, but must be
prevented from falling to destruction. Any movement that would result in the
robot falling from the table must be prevented, however further valid
movement commands must still be allowed.

Create an application that can read in (from file or standard input) commands of the
following form:

PLACE X,Y,F

MOVE

LEFT

RIGHT

REPORT

● PLACE will put the toy robot on the table in position X,Y and facing NORTH,
SOUTH, EAST or WEST.

● The origin (0,0) can be considered to be the SOUTH WEST most corner.

● The first valid command to the robot is a PLACE command, after that, any
sequence of commands may be issued, in any order, including another
PLACE command. The application should discard all commands in the
sequence until a valid PLACE command has been executed.

● MOVE will move the toy robot one unit forward in the direction it is currently
facing.

● LEFT and RIGHT will rotate the robot 90 degrees in the specified direction
without changing the position of the robot.

● REPORT will announce the X,Y and F of the robot. This can be in any form,
but standard output is sufficient.

● A robot that is not on the table can choose the ignore the MOVE, LEFT,
RIGHT and REPORT commands.

● Input can be from a file, or from standard input, as the developer chooses.

● Provide test data to exercise the application.

Constraints

● The toy robot must not fall off the table during movement. This also includes
the initial placement of the toy robot.

● Any move that would cause the robot to fall must be ignored.

Example Input and Output

Example a

PLACE 0,0,NORTH

MOVE

REPORT

Expected output:

0,1,NORTH

Example b

PLACE 0,0,NORTH

LEFT

REPORT

Expected output:

0,0,WEST

Example c

PLACE 1,2,EAST

MOVE

MOVE

LEFT

MOVE

REPORT

Expected output

3,3,NORTH

Deliverables

Please provide your source code, and any test code/data you are using in
developing your solution.

Please engineer your solution to a standard you consider suitable for production. It is
not required to provide any graphical output showing the movement of the toy robot.
