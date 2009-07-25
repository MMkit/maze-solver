package maze.model;

public final class MazeCellPeg implements Comparable<MazeCellPeg>
{
   private final int x;
   private final int y;

   public MazeCellPeg(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   /**
    * Creates a Peg of the North-West Peg of the Cell.
    * @param cell The Maze Cell to create a Peg from.
    */
   public MazeCellPeg(MazeCell cell)
   {
      this.x = cell.getX();
      this.y = cell.getY();
   }

   @Override
   public int compareTo(MazeCellPeg o)
   {
      if (this.x == o.x)
         return this.y - o.y;
      else
         return this.x - o.x;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (! (obj instanceof MazeCellPeg))
         return false;
      MazeCellPeg other = (MazeCellPeg) obj;
      if (x != other.x)
         return false;
      if (y != other.y)
         return false;
      return true;
   }

   /**
    * Get the maze cell that this peg belongs to.
    * @return The maze cell that has this peg as its South-West peg.
    */
   public MazeCell getMazeCell()
   {
      try
      {
         return new MazeCell(this.x, this.y);
      }
      catch (IllegalArgumentException e)
      {
         return new MazeCell(1, 1);
      }
   }

   public int getX()
   {
      return x;
   }

   public int getY()
   {
      return y;
   }

   @Override
   public int hashCode()
   {
      return this.x & (y << 16);
   }
}