# Introduction #

## Team Micromouse Sim: ##
  * Norwit Veun
  * Luke Last
  * Vincent Frey
  * John Smith

## Assigned Work ##

  * Norwit - Menus and related JDialogs
  * Vince  - Models for Maze, Mouse, Display for Statistics
  * Luke   - Maze Rendering and Animations, Script editor
  * John   - Maze Builder with templates

# Maze Features #

## Maze Build Display ##
Swing Classes: JPanel, JToolbar, JToolTip, OverlayLayout, MouseListener, JColorChooser

  * 2D Graphics custom drawings
  * Mouse press select wall - mouse press listener
  * Maze templates to drag into maze build
  * Have tools in toolbar

## Maze Test AI Tisplay ##
Swing Classes: JButton, JSpinner, JColorChooser, JPanel

  * Show timer
  * Show mouse animation based on display settings
  * Show current number of steps/moves taken by the AI
  * 10 min max run
  * Stop and start button
  * Auto stop when finish by reaching center

## Save/Load Map and AI ##
Swing Classes: JMenuBar, JFileChooser, FileFilter

  * Save maze map as binary

## Maze Statistics ##
Swing Classes: JTable, JSplitPane, JPanel

  * For the current maze, statistics will be shown covering each of the supplied algorithms and the most recently loaded custom algorithm
  * The statistics covered will be:
    * Number of Unique Cells Traversed - this is the metric is important because this number is used to determine the winner of a competition if nobody solves the maze at that competition.
    * Total Number of Moves needed to reach the center the first time
    * Number of Moves needed to reach the center on the fastest run
    * Total Number of Moves until the fastest run is completed
  * These statistics will be in regard to the algorithm only so they will ignore the timer limitations of the competition
  * If an algorithm will not solve the maze then the last three sets of statistics will be ignored

## Mouse AI Options ##
Swing Classes: JMenuBar, JCheckBox, JRadioButtons

  * Choose preset algorithms - 6 defaults
  * Options to change display settings
    * Speed runs - after first run, all timed runs will only go through previously explored places
    * Turbo mode - visually goes faster through explored cells
    * Fog of war - shows the maze as the robot knows it so that the user may see how the robot learns
    * Skip everything
    * Show path(s) - show the course that the robot took
  * Have user input how fast the mouse can go for timer considerations

## Mouse AI Script Editor ##
Swing Classes: JTextArea

  * In a text area
  * Script to modify mouse AI
  * Ability to view the Java source code of the built in Algorithms
  * Save/load Python AI scripts

# Model Description #

## Maze ##
  * This will model a Mircomouse competition maze.  It will provide functionality such as getWall or setWall given a location.  It will also provide functionality to determine if the contents represent a legal Micromouse maze.

## Robot ##
  * This model will represent the physical attributes of a robot in the maze.  It will track the robot's current location, it's past history, and provide a method that will allow the robot to be programatically traversed through the maze.  The past history will include remembering where it has been and will remember what the maze looks like where it has been.

## AI ##
  * This will model how the Robot will traverse the Maze.  Six static algorithms will be showcased as well as a way to allow the user to design their own.  The AI will have an interface that it must follow.  An example method would be nextStep in which the algorithm will determine what step the Robot should take next and return it. The six current static methods are as follows:
    * **Left-Wall Follower:** This is a simple implementation a maze algorithm where you can consider the Robot keeping its "left hand" on the wall.
    * **Right-Wall Follower:** This is a simple implementation a maze algorithm where you can consider the Robot keeping its "right hand" on the wall.
    * **Tremaux's Method:** This is a slightly more complicated algorithm that appears to be a right-wall follower, except when it approaches an intersection it has previously visited it retraces its steps to the previous intersection with unexplored options.  It effectively does a depth-first search until it reaches its goal.
    * **Floodfill:** This is the standard in Micromouse algorithms.  Comparable to Dijkstra's algorithm, this does a breadth-first search until it finds all cells reachable from the goal and uses this knowledge to traverse towards the goal.
    * **Modified Floodfill:** This is a modified version of floodfill that tries to minimize the amount of "reflooding" during the exploration of the maze.
    * **Telly:** This is a custom algorithm that searches out all accessible, unexplored territory.  In theory it should converge so that the second run through the maze will yield the best run possible because the first run will do much more exploration than is necessary.

# Overview of GUI #

## Tabs ##
  * **Maze Builder:** This tab will allow the user to design a maze that can be saved for later or used to watch different algorithms responses immediately in the Micromouse Testing Area or the Statistics Area
  * **Micromouse Testing Area:** This tab will allow the user to watch a Micromouse traverse the maze using the algorithm selected from the other areas of the program
  * **Statistics Area:** This tab will display relevant statistics related to how the Micromouse would respond to the current maze.  Statistics will include, but are not limited to, how many squares the Micromouse will traverse, how many moves until the mouse solves the maze the first time, how many moves will the mouse take to solve on its best run and how many total moves will the mouse take before it completes this best run.
  * **Script Editor:** This tab will display the algorithm used by the Micromouse to traverse the maze.  It is a hope that this tab will also allow the users to design their own algorithms for testing.

  * Listeners will disable menu items based upon which tab is currently visible
or switch to the relevant tab when the menu item is chosen

## Menu ##
  * **File**
    * Save..
    * Load..
    * Exit
  * **Maze Options**
    * Load Maze
    * Save Maze
    * Maze Templetes
  * **Mouse Options**
    * Load AI
    * Save AI
    * Algorithms
    * Mouse Speed
    * Display Settings

# Swing Classes #

  * MouseListener
  * JFileChooser
  * FileFilter
  * JCheckBox
  * JMenuBar
  * JMenu
  * JMenuItem
  * JTabbedPane
  * JToolBar
  * JColorChooser
  * JRadioButton
  * JPanel - custom drawings
  * JSlider
  * JComboBox
  * JTable
  * JTextArea
  * JSplitPane
  * JTextField
  * JOptionPane
  * JButton
  * JLabel
  * JToolTip
  * OverlayLayout - for maze template pieces
  * JSpinner