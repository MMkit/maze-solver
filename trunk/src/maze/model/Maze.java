package maze.model;

/*
 * Maze.java This program is meant to act as a model for the maze in a
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
 * Day 2: Added more functional bits Found a site with maze images:
 * http://www.tcp4me.com/mmr/mazes/ I can't find any of the Seoul mazes that I
 * love so much though
 */

public class Maze
{
   int[] rwalls;
   int[] cwalls;
   public static final int SIZE = 16; //A maze is generally 16 x 16
   public static final int NUM_ELEMENTS = ( SIZE - 1 ); //# of elements per column
   public static final int NORTH = 0;
   public static final int EAST = 1;
   public static final int SOUTH = 2;
   public static final int WEST = 3;

   public Maze()
   {
      rwalls = new int[NUM_ELEMENTS];
      cwalls = new int[NUM_ELEMENTS];
      cwalls[0] = 1; //This is the only wall that is guaranteed
   }

   //Another constructor should be made to pass in a predetermined maze
   public Maze( int[] wholeMaze )
   {
      rwalls = new int[NUM_ELEMENTS];
      cwalls = new int[NUM_ELEMENTS];
      for ( int i = 0; i < NUM_ELEMENTS; i++ )
      {
         rwalls[i] = wholeMaze[i];
         cwalls[i] = wholeMaze[i + NUM_ELEMENTS];
      }
   }

   public void setWall( int x, int y, int dir )
   {
      //Check for the boundary conditions
      if ( ( ( dir == NORTH ) && ( y == ( SIZE - 1 ) ) ) ||
           ( ( dir == EAST ) && ( x == ( SIZE - 1 ) ) ) ||
           ( ( dir == SOUTH ) && ( y == 0 ) ) ||
           ( ( dir == WEST ) && ( x == 0 ) ) )
      {
         return;
      }
      int bitmask = 1;
      if ( ( dir == NORTH ) || ( dir == SOUTH ) )
      {
         bitmask = bitmask << y;
         if ( dir == SOUTH )
         {
            bitmask = bitmask >> 1;
         }
         rwalls[x] = rwalls[x] | bitmask;
      }
      else
      {
         bitmask = bitmask << x;
         if ( dir == WEST )
         {
            bitmask = bitmask >> 1;
         }
         cwalls[y] = cwalls[y] | bitmask;
      }
   }

   public boolean getWall( int x, int y, int dir )
   {
      //Check for the boundary conditions
      if ( ( ( dir == NORTH ) && ( y == ( SIZE - 1 ) ) ) ||
           ( ( dir == EAST ) && ( x == ( SIZE - 1 ) ) ) ||
           ( ( dir == SOUTH ) && ( y == 0 ) ) ||
           ( ( dir == WEST ) && ( x == 0 ) ) )
      {
         return true;
      }
      int bitmask = 1;
      if ( ( dir == NORTH ) || ( dir == SOUTH ) )
      {
         bitmask = bitmask << y;
         if ( dir == SOUTH )
         {
            bitmask = bitmask >> 1;
         }
         int flag = rwalls[x] & bitmask;
         if ( flag == 0 )
         {
            return false;
         }
         else
         {
            return true;
         }
      }
      else
      {
         bitmask = bitmask << x;
         if ( dir == WEST )
         {
            bitmask = bitmask >> 1;
         }
         int flag = cwalls[y] & bitmask;
         if ( flag == 0 )
         {
            return false;
         }
         else
         {
            return true;
         }
      }
   }

   public void clearWall( int x, int y, int dir )
   {
      //Check for the boundary conditions
      if ( ( ( dir == NORTH ) && ( y == ( SIZE - 1 ) ) ) ||
           ( ( dir == EAST ) && ( x == ( SIZE - 1 ) ) ) ||
           ( ( dir == SOUTH ) && ( y == 0 ) ) ||
           ( ( dir == WEST ) && ( x == 0 ) ) )
      {
         return;
      }
      int bitmask = 1;
      int bitmask2 = 0xFFFF;
      if ( ( dir == NORTH ) || ( dir == SOUTH ) )
      {
         bitmask = bitmask << y;
         if ( dir == SOUTH )
         {
            bitmask = bitmask >> 1;
         }
         bitmask = bitmask ^ bitmask2;
         rwalls[x] = rwalls[x] & bitmask;
      }
      else
      {
         bitmask = bitmask << x;
         if ( dir == WEST )
         {
            bitmask = bitmask >> 1;
         }
         bitmask = bitmask ^ bitmask2;
         cwalls[y] = cwalls[y] & bitmask;
      }
   }

   public int[] getMaze()
   {
      int[] wholeMaze = new int[2 * NUM_ELEMENTS];
      for ( int i = 0; i < NUM_ELEMENTS; i++ )
      {
         wholeMaze[i] = rwalls[i];
         wholeMaze[i + NUM_ELEMENTS] = cwalls[i];
      }
      return wholeMaze;
   }

   public boolean isLegal()
   {
      //There are three rules that must be upheld to be a valid maze
      //Rule 1: There is a wall next to the starting square
      if ( getWall( 0, 0, EAST ) == false )
      {
         return false;
      }
      //Rule 2: There is one and only one way into the center of the maze
      //Even though I've coded this to accept a variable size in the future
      //I shall still assume here that the goal is always two by two and the
      //total size will always be even
      int walls = 0;
      int square1 = SIZE / 2;
      int square2 = square1 - 1;
      if ( getWall( square1, square1, NORTH ) == true )
      {
         walls += 1;
      }
      if ( getWall( square1, square1, EAST ) == true )
      {
         walls += 1;
      }
      if ( getWall( square2, square1, WEST ) == true )
      {
         walls += 1;
      }
      if ( getWall( square2, square1, NORTH ) == true )
      {
         walls += 1;
      }
      if ( getWall( square1, square2, SOUTH ) == true )
      {
         walls += 1;
      }
      if ( getWall( square1, square2, EAST ) == true )
      {
         walls += 1;
      }
      if ( getWall( square2, square2, SOUTH ) == true )
      {
         walls += 1;
      }
      if ( getWall( square2, square2, WEST ) == true )
      {
         walls += 1;
      }
      if ( walls != 7 )
      {
         return false;
      }

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for ( int i = 0; i < NUM_ELEMENTS; i++ )
      {
         for ( int j = 0; j < NUM_ELEMENTS; j++ )
         {
            if ( getWall( i, j, NORTH ) == true )
            {
               continue;
            }
            if ( getWall( i, j, EAST ) == true )
            {
               continue;
            }
            if ( getWall( i + 1, j + 1, SOUTH ) == true )
            {
               continue;
            }
            if ( getWall( i + 1, j + 1, WEST ) == true )
            {
               continue;
            }
            //Check for center peg
            if ( ( i == square2 ) && ( j == square2 ) )
            {
               continue;
            }
            return false;
         }
      }

      return true;
   }

   public void clearMaze()
   {
      for ( int i = 0; i < NUM_ELEMENTS; i++ )
      {
         rwalls[i] = 0;
         cwalls[i] = 0;
      }
      cwalls[0] = 1;
   }
};