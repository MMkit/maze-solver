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

import maze.model.Maze;

public class Robot
{
   //The robot's knowledge of the maze
   int[] explored;
   int[][] distance;
   Maze knownMaze;

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

   //The robot's possible algorithms
   public static final int LEFT_WALL_FOLLOWER = 0;
   public static final int RIGHT_WALL_FOLLOWER = 1;
   public static final int FRENCHIE = 2;
   public static final int FLOODFILL = 3;
   public static final int MODIFIED_FLOODFILL = 4;
   public static final int TELLY = 5;
   public static final int NUMBER_OF_ALGORITHMS = 5;
   //It's the number of the last algorithm
   private int algorithm;

   //Some model other related variables
   private boolean speedRun;
   private Maze actualMaze;

   //A model related variable that will allow the user to track movements
   private Vector<Integer> moveQueue;
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
   }

   public void reset()
   {
      //Place yourself idling at the start
      x = 0;
      y = 0;
      dir = Maze.NORTH;

      moveQueue.clear();

      //Forget everything you know about the maze
      knownMaze.clearMaze();
      for ( int i = 0; i < Maze.SIZE; i++ )
      {
         explored[i] = 0;
         for ( int j = 0; j < Maze.SIZE; j++ )
         {
            distance[i][j] = 0;
         }
      }
   }

   public void setAlgorithm( int alg )
   {
      if ( ( alg >= 0 ) && ( alg <= NUMBER_OF_ALGORITHMS ) )
      {
         algorithm = alg;
      }
   }

   public void setSpeedRun( boolean speed )
   {
      speedRun = speed;
   }

   public void setMaze( Maze m )
   {
      //Calling program should ensure that m.isLegal() is true
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

   private void loadQueue()
   {
      boolean unexplored = false;
      do
      {
         checkWalls();
         int nextDir = lookForBestMove();
         orient( nextDir );
         goForward( nextDir );
         if ( getExplored() == false )
         {
            setExplored();
            unexplored = true;
         }
      }
      while ( unexplored == false );
   }

   private void setExplored()
   {
      // TODO Auto-generated method stub
   }

   private boolean getExplored()
   {
      // TODO Auto-generated method stub
      return false;
   }

   private int lookForBestMove()
   {
      // TODO Auto-generated method stub
      return 0;
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
      if ( ( ( nextDir - dir ) == 1 ) || ( ( nextDir == Maze.WEST ) && ( dir == Maze.NORTH ) ) )
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
      if ( nextDir == dir )
      {
         moveQueue.add( AHEAD_ONE );
      }
      else
      {
         moveQueue.add( BACK_ONE );
      }

      //Actually move the robot there
      if ( nextDir == Maze.NORTH )
      {
         y++;
      }
      else if ( nextDir == Maze.EAST )
      {
         x++;
      }
      else if ( nextDir == Maze.SOUTH )
      {
         y--;
      }
      else
      {
         x--;
      }
   }

};