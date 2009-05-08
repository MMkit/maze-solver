package maze.model;

/**
 * Represents each of the 4 locations a wall can be on each cell.
 */
public enum Direction
{
   //These should match those values set in Maze.java

   North( 0 ),
   South( 2 ),
   East( 1 ),
   West( 3 ),
   Directionless( 4 );
   private final int index;

   private Direction( int index )
   {
      this.index = index;
   }

   public int getIndex()
   {
      return this.index;
   }

   /**
    * Gets the direction directly to the left of this one.
    */
   public Direction getLeft()
   {
      switch ( this )
      {
         case North :
            return West;
         case East :
            return North;
         case South :
            return East;
         case West :
            return South;
         default :
            throw new RuntimeException( "Unkown Direction: " + this );
      }
   }

   /**
    * Gets the direction directly to the right of this one.
    */
   public Direction getRight()
   {
      switch ( this )
      {
         case North :
            return East;
         case East :
            return South;
         case South :
            return West;
         case West :
            return North;
         default :
            throw new RuntimeException( "Unkown Direction: " + this );
      }
   }

   public Direction getOpposite()
   {
      switch ( this )
      {
         case North :
            return South;
         case East :
            return West;
         case South :
            return North;
         case West :
            return East;
         default :
            throw new RuntimeException( "Unkown Direction: " + this );
      }
   }
}
