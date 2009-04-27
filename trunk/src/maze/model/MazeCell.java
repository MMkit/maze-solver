package maze.model;

import java.awt.Point;

/**
 * Represents the coordinates of one cell/box of the maze. <br>
 * The upper left is x = 1, y = 1.<br>
 * Each instance is immutable.
 */
public final class MazeCell
{
   private final int x;
   private final int y;

   /**
    * Constructor.
    * @param xCoordinate The X/horizontal cell coordinate starting with 1 from
    *           the left.
    * @param yCoordinate The Y/vertical cell coordinate starting with 1 from the
    *           top.
    */
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
