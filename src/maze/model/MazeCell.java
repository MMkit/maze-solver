package maze.model;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Represents the coordinates of one cell/box of the maze. <br>
 * The upper left is x = 1, y = 1.<br>
 * Each instance is immutable.
 */
public final class MazeCell implements Comparable<MazeCell>
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
   public MazeCell(int xCoordinate, int yCoordinate)
   {
      if (xCoordinate <= 0)
         throw new IllegalArgumentException("X coordinate must be greater than 0.");
      if (yCoordinate <= 0)
         throw new IllegalArgumentException("Y coordinate must be greater than 0.");

      this.x = xCoordinate;
      this.y = yCoordinate;
   }

   /**
    * Creates a new <code>MazeCell</code> with its x value changed by the given
    * amount.
    */
   public MazeCell plusX(int x)
   {
      return new MazeCell(this.x + x, this.y);
   }

   /**
    * Creates a new <code>MazeCell</code> with its y value changed by the given
    * amount.
    */
   public MazeCell plusY(int y)
   {
      return new MazeCell(this.x, this.y + y);
   }

   /**
    * Sorts in order from (x,y) (1,1),(2,1),(3,1),(1,2),(2,2),(3,2). In other
    * words from left to right, top to bottom.
    */
   @Override
   public int compareTo(MazeCell other)
   {
      if (this.y != other.y)
      {
         return other.y - this.y;
      }
      else
      //y's are equal so compare x.
      {
         return other.x - this.x;
      }
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof MazeCell)
      {
         MazeCell other = (MazeCell) obj;
         if (this.x == other.x && this.y == other.y)
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      return this.x | (this.y << 16); //Merge the 2 coordinates.
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
      return new Point(this.x, this.y);
   }

   /**
    * Test to see if this maze cell is within the coordinate range of the given
    * maze dimension.
    */
   public boolean isInRange(Dimension mazeSize)
   {
      return (this.x <= mazeSize.width && this.y <= mazeSize.height);
   }

}
