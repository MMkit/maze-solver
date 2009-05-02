package maze.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

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
public class Maze extends MazeModel
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
      rwalls = new int[SIZE];
      cwalls = new int[SIZE];
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

   public boolean isPegLegal( int x, int y )
   {
      //This function is designed to give a lower-level view to the isLegal
      //method by telling about a specific peg

      //The peg location is based off of the cell that it is in the NORTHEAST
      //corner of.  Peg (0,0) is the only peg touching cell (0,0)
      if ( ( x < 0 ) || ( y < 0 ) || ( x >= ( SIZE - 1 ) ) || ( y >= ( SIZE - 1 ) ) )
      {
         return false;
      }
      //Every peg should have at least one wall attached to it
      if ( ( getWall( x, y, NORTH ) == true ) ||
           ( getWall( x, y, EAST ) == true ) ||
           ( getWall( x + 1, y, NORTH ) == true ) ||
           ( getWall( x, y + 1, EAST ) == true ) )
      {
         //There are only three cases with walls that must be checked
         //First is the starting square
         if ( ( x == 0 ) && ( y == 0 ) )
         {
            if ( ( getWall( 0, 0, EAST ) == true ) && ( getWall( 0, 0, NORTH ) == false ) )
            {
               return true;
            }
            else
            {
               return false;
            }
         } //Second is that it is not the center peg
         else if ( ( x == ( SIZE / 2 - 1 ) ) && ( y == ( SIZE / 2 - 1 ) ) )
         {
            return false;
         } //Third is the pegs surrounding the center square
         else if ( ( ( x <= SIZE / 2 ) && ( y <= SIZE / 2 ) ) &&
                   ( ( x >= ( SIZE / 2 - 2 ) ) && ( y >= ( SIZE / 2 - 2 ) ) ) )
         {

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

            //All of the center pegs are connected
            if ( walls == 7 )
            {
               return true;
            }

         } //There are no more reasons to check the walls
         else
         {
            return true;
         }
      }
      else
      {
         //The only exception to no wall is the center peg
         if ( ( x == ( SIZE / 2 - 1 ) ) && ( y == ( SIZE / 2 - 1 ) ) )
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      return false;
   }

   public Vector<Point> whereIllegal()
   {
      //This function is basically isLegal with the added capability of
      //returning the peg locations that are bad

      //Can return multiple instances of a Point if that Point is bad for
      //More than one reason.
      //Example: if somehow there were no walls off of peg(0,0) then the
      //Point would be included since

      Vector<Point> badPoints = new Vector<Point>();
      //There are rules that must be upheld to be a valid maze
      //Rule 1: There is a wall next to the starting square
      if ( ( getWall( 0, 0, EAST ) == false ) || ( getWall( 0, 0, NORTH ) == true ) )
      {
         badPoints.add( new Point() );
      }
      //Rule 2: There is one and only one way into the center of the maze
      //Even though I've coded this to accept a variable size in the future
      //I shall still assume here that the total size will always be even
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
         badPoints.add( new Point( ( square2 - 1 ), ( square2 - 1 ) ) );
         badPoints.add( new Point( ( square2 ), ( square2 - 1 ) ) );
         badPoints.add( new Point( ( square1 ), ( square2 - 1 ) ) );
         badPoints.add( new Point( ( square2 - 1 ), ( square2 ) ) );
         badPoints.add( new Point( ( square1 ), ( square2 ) ) );
         badPoints.add( new Point( ( square2 - 1 ), ( square1 ) ) );
         badPoints.add( new Point( ( square2 ), ( square1 ) ) );
         badPoints.add( new Point( ( square1 ), ( square1 ) ) );
      } //All of the center pegs are connected

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if ( ( getWall( square1, square1, SOUTH ) == true ) ||
           ( getWall( square1, square1, WEST ) == true ) ||
           ( getWall( square2, square2, NORTH ) == true ) ||
           ( getWall( square2, square2, EAST ) == true ) )
      {
         badPoints.add( new Point( square2, square2 ) );
      }

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for ( int i = 0; i < SIZE; i++ )
      {
         for ( int j = 0; j < SIZE; j++ )
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

            badPoints.add( new Point( i, j ) );
         }
      }
      return badPoints;
   }

   @Override
   public void setSize( Dimension size )
   {
      throw new RuntimeException( "Not implemented" );
   }

   @Override
   public MazeWall getWall( final MazeCell cell, final Direction direction )
   {
      return new MazeWall()
      {

         @Override
         public boolean isSet()
         {
            return getWall( cell.getXZeroBased(), SIZE - cell.getY(), direction.getIndex() );
         }

         @Override
         public void set( boolean value )
         {
            if ( value )
            {
               setWall( cell.getXZeroBased(), SIZE - cell.getY(), direction.getIndex() );
            }
            else
            {
               clearWall( cell.getXZeroBased(), SIZE - cell.getY(), direction.getIndex() );
            }
         }
      };
   }
}