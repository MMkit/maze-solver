package maze.ai;

/*
 * Robot.java This program is meant to act as a model for the robot in a
 * MicroMouse competition.
 * 
 * Authors: Vincent Frey
 * 
 * 
 * 
 * 
 * Date initiated: 4-24-09
 * 
 * 
 * Programming Notes:
 * 
 * Day 1: Implementing a few of the functional bits
 * 
 * Generic Note: along with running continuously we could let the user step
 * through operation
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*; //only used in the run function

import maze.model.Maze;

public class Robot extends RobotBase
{
   //The robot's knowledge of the maze
   int[] explored;
   int[][] distance;
   private Maze knownMaze;
   private static final int USELESS = 1024;

   //Some constants that'll be accessed from maze
   //public static final int NORTH = 0;
   //public static final int EAST = 1;
   //public static final int SOUTH = 2;
   //public static final int WEST = 3;
   //public static final int SIZE = 16; //A maze is generally 16 x 16
   //public static final int NUM_ELEMENTS = SIZE - 1;

   //The robot's knowledge about itself
   int x;
   int y;
   int dir;
   boolean goal;
   //The goal determines whether you are heading towards the middle
 	//or towards the start
   private static final boolean TO_CENTER = true;
   private static final boolean TO_START = false;

   //The robot's possible algorithms
   public static final int LEFT_WALL_FOLLOWER = 0;
   public static final int RIGHT_WALL_FOLLOWER = 1;
   public static final int TREMAUX = 2;
   public static final int FLOODFILL = 3;
   public static final int MODIFIED_FLOODFILL = 4;
   public static final int TELLY = 5;
   public static final int NUMBER_OF_ALGORITHMS = 5;
   //It's the number of the last algorithm
   private int algorithm;

   //Some model other related variables
   private boolean speedRun;
   private boolean speedRunCapable;
   private Maze actualMaze;

   //A model related variable that will allow the user to track movements
   private Vector<Integer> moveQueue;
   public static final int FAST = 32;
   public static final int SPEED_MASK = 31;
   public static final int AHEAD_ONE = 0;
   public static final int RIGHT_TURN = 1;
   public static final int LEFT_TURN = 2;
   public static final int BACK_ONE = 3;

   public Robot( Maze m )
   {
      actualMaze = m;
      knownMaze = new Maze();
      explored = new int[Maze.SIZE];
      distance = new int[Maze.SIZE][Maze.SIZE];
      algorithm = TELLY;
      speedRun = false;
      moveQueue = new Vector<Integer>();
      
      reset(); //initializes many variables
   }

   public void reset()
   {
		//Place yourself idling at the start
		x = 1;
		y = Maze.SIZE;
		dir = Maze.NORTH;
		goal = TO_CENTER;

		//Forget Everything you knew about the maze
		purgeKnowledge();
   }

   public void setAlgorithm( int alg )
   {
	 //Sets the algorithm used by the robot.  The list of algorithms is:
	 //LEFT_WALL_FOLLOWER = 0;
	 //RIGHT_WALL_FOLLOWER = 1;
	 //TREMAUX = 2;
	 //FLOODFILL = 3;
	 //MODIFIED_FLOODFILL = 4;
	 //TELLY = 5;
     if ( ( alg >= 0 ) && ( alg <= NUMBER_OF_ALGORITHMS ) )
      {
         algorithm = alg;
      }
   }

   public void setSpeedRun( boolean speed )
   {
		//Determines if the robot will do speed runs in the more intellegent
		//algorithms.  A speed run is a run that only considers the previously
		//explored territory.
      speedRun = speed;
   }

   public void setMaze( Maze m )
   {
      //Calling program should ensure that m.isLegal() is true
	   //There are useful reasons for not doing it here such as allowing for the
 		//handling of "Phantom Walls" around the goal
      actualMaze = m;
   }

   public int takeNextStep()
   {
      //This function is designed to return step by step what the robot does
      //It does this by popping the head of the queue when it's not empty
      //Otherwise it loads the queue up until it gets to an unexplored spot
      if ( moveQueue.isEmpty() == false )
      {
         return moveQueue.remove( 0 );
      }
      else
      {
         loadQueue();
         return moveQueue.remove( 0 );
      }
   }

   @Override
   public RobotStep nextStep()
   {
      switch ( this.takeNextStep() )
      {
         case AHEAD_ONE :
            return RobotStep.MoveForward;
         case BACK_ONE :
            return RobotStep.MoveBackward;
         case RIGHT_TURN :
            return RobotStep.RotateRight;
         case LEFT_TURN :
            return RobotStep.RotateLeft;
         default :
            return RobotStep.MoveForward;
      }
   }

   @Override
   public boolean isInTurboMode()
   {
      return this.speedRun;
   }

   private void loadQueue()
   {
      boolean unexplored = false;
      do
      {
         checkWalls();
         int nextDir = lookForBestMove();
         orient( nextDir );
         goForward( nextDir );
         if ( getExplored(x,y) == false )
         {
            setExplored();
            unexplored = true;
         }
      }
      while (  (unexplored == false) && (atGoal() == false)
  			&& (moveQueue.size() <= 100)  );
   }

   private void setExplored()
   {
		//This function remembers where the mouse has been
		int bitmask = 1;
	    bitmask = bitmask << (y - 1);
	    explored[x-1] = explored[x-1] | bitmask;
   }

   private boolean getExplored(int i, int j)
   {
		//This function tells the mouse where it has been
		//(i,j) are the (x,y) coordinate in question
		int bitmask = 1;
	    bitmask = bitmask << (i - 1);
		int flag = explored[j-1] & bitmask;
		if( flag == 0 ) {
		  return false;
		}
		else {
		  return true;
		}
	}

   private boolean getNeighborExplored(int i, int j, int k) {
		//This function tells the mouse where it has been
		//(i,j) are the (x,y) coordinate and k is the direction
		switch (k) {
		  case Maze.NORTH :
		    if(j>1) {
			  return getExplored(i,j-1);
			}
			break;
		  case Maze.SOUTH :
		    if(j<Maze.SIZE) {
			  return getExplored(i,j+1);
			}
			break;
		  case Maze.EAST :
		    if(i<Maze.SIZE) {
			  return getExplored(i+1,j);
			}
			break;
		  default : //Maze.WEST
		    if(i>1) {
			  return getExplored(i-1,j);
			}
		}
		return false;
	  }
   
   private void purgeKnowledge() {
		//This function is designed to let the alorigthms have a system to tell
			//the model that the model knows information that contradicts that the
			//maze can be solved.  This can be solved by having the mouse shutdown
			//or can assume that a "phantom wall" was seen and try to start over
		moveQueue.clear();

		//Forget everything you know about the maze
		knownMaze.clearMaze();
		speedRunCapable = false;
		for(int i = 0; i<Maze.SIZE; i++){
		  explored[i] = 0;
		  for(int j = 0; j<Maze.SIZE; j++){
			distance[i][j] = USELESS;
		  }
		}

		//We do need to start collecting information again though
		checkWalls();
	  }

	  private boolean atGoal() {
		if( goal == TO_START ) {
		  if( (x == 1) && (y == Maze.SIZE) ) {
			return true;
		  }
		  else {
			return false;
		  }
		}
		else {
		  int target1 = Maze.SIZE/2;
		  int target2 = target1 + 1;
		  if( ( (x == target1) || (x ==target2) )
		      && ( (y == target1) || (y == target2) ) ) {
			return true;
		  }
		  else {
			return false;
		  }
		}
	  }

	  public int getX() {
		  return x;
	  }

	  public int getY() {
		  return y;
	  }

	  private void setDistance(int i, int j, int value) {
		//Sets the distance of cell (i,j) to value
		distance[i-1][j-1] = value;
	  }

	  public int getDistance(int i, int j) {
			return distance[i-1][j-1];
		  }

		  private int getNeighborDistance(int i, int j, int k) {
			//This function is to determine the contents of the distance array given:
				//An (x,y) coordinate in (i,j) respectively as the current cell locale
				//The direction of the neighbor in question in k
			if( (k == Maze.NORTH) && (j > 1)) {
			  return distance[i-1][j-2];
			}
			else if( (k == Maze.SOUTH) && (j < Maze.SIZE)) {
			  return distance[i-1][j];
			}
			else if( (k == Maze.EAST) && (i < Maze.SIZE)) {
			  return distance[i][j-1];
			}
			else if( (k == Maze.WEST) && (i > 1)) {
			  return distance[i-2][j-1];
			}
			else {
			  return USELESS;
			}
		  }

		  private int lookForBestMove() {
			//This function multiplexes all of the different algorithms together and
			  //and returns the direction that the next move should be taken in.  This
			  //assumes that moves are only one cell at a time.  Diagonals, S-turn and
			  //such are ignored for the simplicity of the model.
			  //LEFT_WALL_FOLLOWER = 0;
			  //RIGHT_WALL_FOLLOWER = 1;
			  //FRENCHIE = 2;
			  //FLOODFILL = 3;
			  //MODIFIED_FLOODFILL = 4;
			  //TELLY = 5;

			switch (algorithm) {
			  case LEFT_WALL_FOLLOWER :
			    return leftWallFollower();
			  case RIGHT_WALL_FOLLOWER :
			    return rightWallFollower();
			  case TREMAUX :
			    return tremaux();
			  case FLOODFILL :
			    return floodfill();
			  case MODIFIED_FLOODFILL :
			    System.out.println("Should add this algorithm before you try it out");
			  case TELLY :
			    System.out.println("Should add this algorithm before you try it out");
			}
			return 0;
		  }

		  private int leftWallFollower() {
			//This is the algorithm for a left-wall following mouse

			//If the left turn is open then take it
			if ( knownMaze.getWall(x,y,((dir + 3)%4)) == false ) {
			  return (dir + 3) % 4;
			}
			//Else if straight is open then take it
			else if ( knownMaze.getWall(x,y,dir) == false ) {
			  return dir;
			}
			//Else take the right turn
			else {
			  return (dir + 1) % 4;
			}
		  }
		  private int rightWallFollower() {
			//This is the algorithm for a left-wall following mouse

			//If the left turn is open then take it
			if ( knownMaze.getWall(x,y,((dir + 1)%4)) == false ) {
			  return (dir + 1) % 4;
			}
			//Else if straight is open then take it
			else if ( knownMaze.getWall(x,y,dir) == false ) {
			  return dir;
			}
			//Else take the right turn
			else {
			  return (dir + 3) % 4;
			}
		  }

		  private int tremaux() {
			//This is the algorithm designed by M. Tremaux

			//So now we'll set up our ball of string a.k.a. a bread crumb trail
			if( distance[x-1][y-1] == USELESS ) {
			  //The way back is to follow the direction you came in reverse
			  distance[x-1][y-1] = dir ^ 2;
			}

			//Tremaux only said to get to the goal, not to get back
			//So we'll just do victory laps
			if( atGoal() == true) {
			  moveQueue.add(AHEAD_ONE);
			  return (dir + 1) % 4;
			}
			else {//bias to the right
			  if ( (knownMaze.getWall(x,y,((dir+1)%4)) == false)
			  		&& (getNeighborDistance(x,y,(dir+1)%4) == USELESS) ) {
				return (dir + 1) % 4;
			  }//then bias to the straight
			  else if ( (knownMaze.getWall(x,y,dir) == false)
			  		&& (getNeighborDistance(x,y,dir) == USELESS) ) {
				return dir;
			  }//then bias to the left
			  else if ( (knownMaze.getWall(x,y,(dir+3)%4) == false)
			  		&& (getNeighborDistance(x,y,(dir+3)%4) == USELESS) ) {
				return (dir+3)%4;
			  }//otherwise we're at a dead end for exploration
			  else {
				return distance[x-1][y-1];
				//Remember that for this implementation we've been putting the "string"
				//direction in the distance
			  }

			}

		  }

		  private int floodfill() {
			//This is a basic floodfill that will bias towards going straight
			int bestDistance = USELESS;
			int bestDirection = Maze.NORTH;

			//We need to check to see if we need to update the distance based off of
				//our position.  The x==1 and y==SIZE case is to initialize the distances
			if ( (atGoal() == true) || ( (x==1) && (y==Maze.SIZE) ) ) {
			  if( atGoal() == true) {
				if( goal == TO_CENTER ) {
				  goal = TO_START;
				}
				else {
				  goal = TO_CENTER;
				}
			  }
			  floodfillRoutine();
			}

			//Speed Run capabilities would be added here, but I'll try adding them
			//into the floodfill algorithm itself first

			//We look at each neighbor to see which direction we should take
			//Note that first we bias towards a straight path
			if ( (knownMaze.getWall(x,y,dir) == false)
					&& ( bestDistance > getNeighborDistance(x,y,dir) ) ) {
			  bestDistance = getNeighborDistance(x,y,dir);
			  bestDirection = dir;
			}

			if ( (knownMaze.getWall(x,y,Maze.NORTH) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.NORTH) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.NORTH);
			  bestDirection = Maze.NORTH;
			}

			if ( (knownMaze.getWall(x,y,Maze.SOUTH) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.SOUTH) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.SOUTH);
			  bestDirection = Maze.SOUTH;
			}

			if ( (knownMaze.getWall(x,y,Maze.EAST) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.EAST) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.EAST);
			  bestDirection = Maze.EAST;
			}

			if ( (knownMaze.getWall(x,y,Maze.WEST) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.WEST) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.WEST);
			  bestDirection = Maze.WEST;
			}

			//If there is a good move then take it
			if (bestDistance < distance[x-1][y-1]) {
			  return bestDirection;
			}


			//If there are no good moves then we might have a problem

			//First step to solving that problem is to see if reflooding is enough
			floodfillRoutine();

			//Then we'll check to see if the above check works out now

			//We look at each neighbor to see which direction we should take
			//Note that first we bias towards a straight path
			if ( (knownMaze.getWall(x,y,dir) == false)
					&& ( bestDistance > getNeighborDistance(x,y,dir) ) ) {
			  bestDistance = getNeighborDistance(x,y,dir);
			  bestDirection = dir;
			}

			if ( (knownMaze.getWall(x,y,Maze.NORTH) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.NORTH) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.NORTH);
			  bestDirection = Maze.NORTH;
			}

			if ( (knownMaze.getWall(x,y,Maze.SOUTH) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.SOUTH) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.SOUTH);
			  bestDirection = Maze.SOUTH;
			}

			if ( (knownMaze.getWall(x,y,Maze.EAST) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.EAST) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.EAST);
			  bestDirection = Maze.EAST;
			}

			if ( (knownMaze.getWall(x,y,Maze.WEST) == false)
					&& ( bestDistance > getNeighborDistance(x,y,Maze.WEST) ) ) {
			  bestDistance = getNeighborDistance(x,y,Maze.WEST);
			  bestDirection = Maze.WEST;
			}

			//If there is a good move then take it
			if (bestDistance < distance[x-1][y-1]) {
			  return bestDirection;
			}

			//If there are still no good moves then we have a big problem
			//This means that the maze cannot be solved.  We could except this or we
			//could assume that we stored a "phantom wall" and start over
			purgeKnowledge();
			return floodfill();

		  }

		  private boolean floodfillRoutine(){
			//First re-initialize everything to useless

			//Determine the current goal and push that into the queue
			//Set the distance of the goal to zero

			//LOOP:
			//Pop the head of the queue
			//For each of heads neighbors
			//Determine if they are accessible
			//Determine if they have a useful distance already
			//If accessible and have a useless distance
			//Then change the distance to one greater than the head's
			//And also push the applicable neighbors into the queue
			//Repeat the LOOP until the queue is empty

			//If the current location of the mouse has a useless distance still
			//Then return false
			//Otherwise return true

			Vector<Point> queue = new Vector<Point>();
			Point head;
			int i;
			int j;
			//First re-initialize everything to useless
			for(i = 0; i < Maze.SIZE; i++) {
			  for(j = 0; j < Maze.SIZE; j++) {
				distance[i][j] = USELESS;
			  }
			}

			//Determine the current goal and push that into the queue
			//Set the distance of the goal to zero
			if( goal == TO_CENTER ) {
			  i = Maze.SIZE/2;
			  j = i - 1;
			  distance[i][i] = 0;
			  distance[j][i] = 0;
			  distance[i][j] = 0;
			  distance[j][j] = 0;

			  j = i + 1;
			  queue.add(new Point(i,i));
			  queue.add(new Point(i,j));
			  queue.add(new Point(j,i));
			  queue.add(new Point(j,j));
			}
			else {
			  setDistance(1,Maze.SIZE,0);
			  queue.add(new Point(1,Maze.SIZE));
			}

			//LOOP:
			do {
			  //Pop the head of the queue
			  head = queue.remove(0);
			  i = (int)head.getX();
			  j = (int)head.getY();
			  //For each of heads neighbors
			  //Determine if they are accessible
			  //Determine if they have a useful distance already
			  //If accessible and have a useless distance
			  //Then change the distance to one greater than the head's
			  //And also push the applicable neighbors into the queue
			  if( knownMaze.getWall(i,j,Maze.NORTH) == false ) {
			  	if( getDistance(i,j-1) == USELESS ){
				  queue.add(new Point(i,j-1));
				  setDistance(i,j-1,getDistance(i,j)+1);
				}
			  }
			  if( knownMaze.getWall(i,j,Maze.SOUTH) == false ) {
			  	if( getDistance(i,j+1) == USELESS ){
				  queue.add(new Point(i,j+1));
				  setDistance(i,j+1,getDistance(i,j)+1);
				}
			  }
			  if( knownMaze.getWall(i,j,Maze.EAST) == false ) {
			  	if( getDistance(i+1,j) == USELESS ){
				  queue.add(new Point(i+1,j));
				  setDistance(i+1,j,getDistance(i,j)+1);
				}
			  }
			  if( knownMaze.getWall(i,j,Maze.WEST) == false ) {
			  	if( getDistance(i-1,j) == USELESS ){
				  queue.add(new Point(i-1,j));
				  setDistance(i-1,j,getDistance(i,j)+1);
				}
			  }
			  //Repeat the LOOP until the queue is empty
			} while (queue.isEmpty() != true);

			//If the current location of the mouse has a useless distance still
			//Then return false
			if(distance[x-1][y-1] == USELESS){
			  return false;
			}
			//Otherwise return true
			return true;
		  }

   private void checkWalls()
   {
      if ( actualMaze.getWall( x, y, Maze.NORTH ) == true )
      {
         knownMaze.setWall( x, y, Maze.NORTH );
      }
      if ( actualMaze.getWall( x, y, Maze.EAST ) == true )
      {
         knownMaze.setWall( x, y, Maze.EAST );
      }
      if ( actualMaze.getWall( x, y, Maze.SOUTH ) == true )
      {
         knownMaze.setWall( x, y, Maze.SOUTH );
      }
      if ( actualMaze.getWall( x, y, Maze.WEST ) == true )
      {
         knownMaze.setWall( x, y, Maze.WEST );
      }
   }

   private void orient( int nextDir )
   {
      //Proper direction already
      if ( nextDir == dir )
      {
         return;
      }
      //Should reverse, not turn
      if ( ( nextDir ^ dir ) == 2 )
      {
         return;
      }
      //Should make a left
      if ( ( ( dir - nextDir ) == 1 ) || ( ( nextDir == Maze.WEST ) && ( dir == Maze.NORTH ) ) )
      {
         moveQueue.add( LEFT_TURN );
      }
      else
      {
         moveQueue.add( RIGHT_TURN );
      }
      dir = nextDir;
   }

   private void goForward( int nextDir )
   {
		//Figure out what our move will be
		int move;
	    if( getNeighborExplored(x,y,nextDir) == true ) {
		  move = FAST;
	    }
	    else {
		  move = 0;
		}

		boolean flag = knownMaze.getWall(x,y,nextDir);
		if( nextDir == dir ) {
		  move = move | AHEAD_ONE;
		}
		else {
		  move = move | BACK_ONE;
		}

		if (flag == false) {

		  moveQueue.add(move);

		  //Actually move the robot there
		  if( nextDir == Maze.NORTH ){
			y--;
		  }
		  else if( nextDir == Maze.EAST ){
			x++;
		  }
		  else if( nextDir == Maze.SOUTH ){
			y++;
		  }
		  else {
			x--;
		  }
		}
   }

   public static void main(String args[]) {
	    Runnable runner = new Runnable() {
	      public void run() {

	        Robot mouse = new Robot(new Maze());

//	        mouse.setAlgorithm(Robot.LEFT_WALL_FOLLOWER);
//	        mouse.setAlgorithm(Robot.RIGHT_WALL_FOLLOWER);
//	        mouse.setAlgorithm(Robot.TREMAUX);
			mouse.setAlgorithm(Robot.FLOODFILL);

	        for( int i =0; i<100; i++) {
			  int j = mouse.takeNextStep();
			  String direction="blank";
			  //Track the mouse position
	/*		  switch (mouse.getDir()) {
			    case Maze.NORTH :
			      direction = "NORTH";
			      break;
			    case Maze.EAST :
			      direction = "EAST";
			      break;
			    case Maze.SOUTH :
			      direction = "SOUTH";
			      break;
			    case Maze.WEST :
			      direction = "WEST";
			  }

			  System.out.println("Step " + String.valueOf(i) + ": x= "
			  		+ String.valueOf(mouse.getX()) + ", y= "
			  		+ String.valueOf(mouse.getY()) + ", dir= "
			  		+ direction);//*/
			  //Track the mouse movements
			  j = j & Robot.SPEED_MASK;
			  switch (j) {
				case Robot.LEFT_TURN :
				  direction = "Turn Left";
				  break;
				case Robot.RIGHT_TURN :
				  direction = "Turn Right";
				  break;
				case Robot.AHEAD_ONE :
				  direction = "Forward";
				  break;
				case Robot.BACK_ONE :
				  direction = "Reverse";
			  }
			  System.out.println("Step " + String.valueOf(i) + ": x= "
				  		+ String.valueOf(mouse.getX()) + ", y= "
				  		+ String.valueOf(mouse.getY()) + ", dir= "
				  		+ direction);
			}
	      }
	    };
	    EventQueue.invokeLater(runner);
	  }

};