package maze.model;

import java.awt.Dimension;
import java.util.Observable;

/**
 * This is a potential new design for the model API.
 * @author Luke Last
 */
public abstract class MazeModel extends Observable
{
   protected int width = 16;
   protected int height = 16;

   /**
    * The maze model size defined by the number of cells/boxes/squares in each
    * dimension.
    * @return The size of the maze model.
    */
   public Dimension getSize()
   {
      return new Dimension( this.width, this.height );
   }

   public void setSize( Dimension size )
   {
      this.width = size.width;
      this.height = size.height;
   }

   public abstract MazeWall getWall( MazeCell cell, Direction direction );

   public MazeWall getWall( int x, int y, Direction direction )
   {
      return this.getWall( new MazeCell( x, y ), direction );
   }

   public static interface MazeWall
   {
      public boolean isSet();

      public void set( boolean value );
   }
}
