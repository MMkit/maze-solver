package maze.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/**
 * Represents the coordinates of one cell/box of the maze. <br>
 * The upper left is x = 1, y = 1.<br>
 * Each instance is immutable.
 */
public final class MazeCell implements Comparable<MazeCell>, Serializable
{
   /**
    * Cache for commonly used objects. First index is the X coordinate, second
    * index is the Y coordinate.
    */
   private static transient final MazeCell[][] cache = new MazeCell[16][16];
   private static final long serialVersionUID = -3456709665929349825L;

   static
   {
      // Initialize the cache.
      for (int x = 1; x < cache.length; x++)
      {
         for (int y = 1; y < cache[0].length; y++)
         {
            cache[x][y] = new MazeCell(x, y);
         }
      }
   }

   /**
    * Get an instance of a maze cell.
    * @param xCoordinate The X/horizontal cell coordinate starting with 1 from
    *           the left.
    * @param yCoordinate The Y/vertical cell coordinate starting with 1 from the
    *           top.
    * @return The maze cell instance.
    * @throws IllegalArgumentException If a given coordinate value is less than
    *            1.
    */
   public static MazeCell valueOf(int xCoordinate, int yCoordinate)
   {
      if (xCoordinate <= 0)
         throw new IllegalArgumentException("X coordinate must be greater than 0.");
      if (yCoordinate <= 0)
         throw new IllegalArgumentException("Y coordinate must be greater than 0.");

      if (xCoordinate < cache.length && yCoordinate < cache[0].length)
      {
         return cache[xCoordinate][yCoordinate];
      }
      else
      {
         // Coordinates are out of range of the cached values.
         return new MazeCell(xCoordinate, yCoordinate);
      }
   }

   /**
    * The column or X coordinate where this cell lives, starting from the left
    * with 1.
    */
   private final int x;

   /**
    * The row or Y coordinate where this cell lives in the maze, starting from
    * the top with 1.
    */
   private final int y;

   /**
    * Constructor.
    * @param xCoordinate The X/horizontal cell coordinate starting with 1 from
    *           the left.
    * @param yCoordinate The Y/vertical cell coordinate starting with 1 from the
    *           top.
    */
   private MazeCell(int xCoordinate, int yCoordinate)
   {
      this.x = xCoordinate;
      this.y = yCoordinate;
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
         return this.y - other.y;
      }
      else
      { // y's are equal so compare x.
         return this.x - other.x;
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

   /**
    * Get the x and y coordinates in the form of a <code>Point</code>.
    * @return A point with the same coordinates.
    */
   public Point getPoint()
   {
      return new Point(this.x, this.y);
   }

   /**
    * Get the X coordinate.
    * @return The X coordinate, starting from 1.
    */
   public int getX()
   {
      return this.x;
   }

   /**
    * Get the X coordinate.
    * @return The X coordinate starting from 0.
    */
   public int getXZeroBased()
   {
      return this.x - 1;
   }

   /**
    * Get the Y coordinate.
    * @return The Y coordinate, starting from 1.
    */
   public int getY()
   {
      return this.y;
   }

   /**
    * Get the Y coordinate.
    * @return The Y coordinate starting from 0.
    */
   public int getYZeroBased()
   {
      return this.y - 1;
   }

   @Override
   public int hashCode()
   {
      return this.x | (this.y << 16); //Merge the 2 coordinates.
   }

   /**
    * Test to see if this maze cell is within the coordinate range of the given
    * maze dimension. The maze dimension gives the number of columns and rows in
    * a maze.
    */
   public boolean isInRange(Dimension mazeSize)
   {
      return (this.x <= mazeSize.width && this.y <= mazeSize.height);
   }

   /**
    * Create a new <code>MazeCell</code> that has been moved one cell in the
    * given direction.
    * @param direction The direction of the neighbor you want.
    * @return A new cell.
    */
   public MazeCell neighbor(Direction direction)
   {
      switch (direction)
      {
         case North :
            return this.plusY(-1);
         case South :
            return this.plusY(1);
         case East :
            return this.plusX(1);
         case West :
            return this.plusX(-1);
         default :
            throw new IllegalArgumentException("Unkown direction: " + direction);
      }
   }

   /**
    * Creates a new <code>MazeCell</code> with its x value changed by the given
    * amount.
    */
   public MazeCell plusX(int x)
   {
      return MazeCell.valueOf(this.x + x, this.y);
   }

   /**
    * Creates a new <code>MazeCell</code> with its y value changed by the given
    * amount.
    */
   public MazeCell plusY(int y)
   {
      return MazeCell.valueOf(this.x, this.y + y);
   }

   @Override
   public String toString()
   {
      return "(" + this.x + "," + this.y + ")";
   }

}