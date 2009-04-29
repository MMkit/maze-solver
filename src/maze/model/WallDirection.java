package maze.model;

/**
 * Represents each of the 4 locations a wall can be on each cell.
 */
public enum WallDirection
{
   //These should match those values set in Maze.java
   North( 0 ),
   South( 2 ),
   East( 1 ),
   West( 3 );

   private final int index;

   private WallDirection( int index )
   {
      this.index = index;
   }

   public int getIndex()
   {
      return this.index;
   }
}
