package maze.model;

import java.awt.Point;

/**
 * Represents the coordinates of one cell/box of the maze. <br>
 * The upper left is x = 1, y = 1.
 */
public final class MazeCell
{
   private final int x;
   private final int y;

   public MazeCell( int xCoordinate, int yCoordinate )
   {
      if ( xCoordinate <= 0 )
         throw new IllegalArgumentException( "X coordinate must be greater than 0." );
      if ( yCoordinate <= 0 )
         throw new IllegalArgumentException( "Y coordinate must be greater than 0." );

      this.x = xCoordinate;
      this.y = yCoordinate;
   }

   public int getX()
   {
      return this.x;
   }

   public int getY()
   {
      return this.y;
   }

   public int getXZeroBased()
   {
      return this.x - 1;
   }

   public int getYZeroBased()
   {
      return this.y - 1;
   }

   public Point getPoint()
   {
      return new Point( this.x, this.y );
   }

}
