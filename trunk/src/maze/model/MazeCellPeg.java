package maze.model;

public final class MazeCellPeg implements Comparable<MazeCellPeg>
{
   private final int x;
   private final int y;

   /**
    * Creates a Peg of the North-West Peg of the Cell.
    * @param cell The Maze Cell to create a Peg from.
    */
   public MazeCellPeg(MazeCell cell)
   {
      this.x = cell.getX();
      this.y = cell.getY();
   }

   public MazeCellPeg(int x, int y)
   {
      this.x = x;
      this.y = y;
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
   public int compareTo(MazeCellPeg o)
   {
      if (this.x == o.x)
         return this.y - o.y;
      else
         return this.x - o.x;
   }

   @Override
   public int hashCode()
   {
      return 31 * this.x + (y << 16);
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

}
