package maze.model;

import java.awt.Dimension;

/**
 * This is a temporary maze model stub used for testing the GUI stuff before we
 * have a real maze model.
 * @author Luke Last
 */
public class MazeModelStub extends MazeModel
{
   private static final int SIZE = 16;
   //Holds vertical wall segments.
   WallStatus[][] horizontalRows = new WallStatus[SIZE][SIZE - 1];
   //Holds horizontal wall segments.
   WallStatus[][] verticalRows = new WallStatus[SIZE][SIZE - 1];

   public MazeModelStub()
   {
      this.initRows( this.horizontalRows );
      this.initRows( this.verticalRows );
      this.getWallStatus( new MazeCell( 1, this.getSize().height ), Direction.East ).value = true;
      MazeCell cell = new MazeCell( this.getSize().width / 2, this.getSize().height / 2 );
      this.getWallStatus( cell, Direction.West ).value = true;
      this.getWallStatus( cell, Direction.North ).value = true;
      cell = cell.plusX( 1 );
      this.getWallStatus( cell, Direction.North ).value = true;
      this.getWallStatus( cell, Direction.East ).value = true;
      cell = cell.plusY( 1 );
      this.getWallStatus( cell, Direction.East ).value = true;
      this.getWallStatus( cell, Direction.South ).value = true;
      cell = cell.plusX( -1 );
      this.getWallStatus( cell, Direction.South ).value = true;
      this.getWallStatus( cell, Direction.West ).value = true;
   }

   private void initRows( WallStatus[][] array )
   {
      for ( WallStatus[] w : array )
      {
         for ( int i = 0; i < w.length; i++ )
         {
            w[i] = new WallStatus();
         }
      }
   }

   @Override
   public Dimension getSize()
   {
      return new Dimension( this.horizontalRows.length, this.verticalRows.length );
   }

   @Override
   public void setSize( Dimension size )
   {
   }

   private WallStatus getWallStatus( MazeCell cell, Direction wall )
   {
      if ( wall == Direction.North )
      {
         if ( cell.getY() > 1 )
         {
            return this.verticalRows[cell.getXZeroBased()][cell.getYZeroBased() - 1];
         }
      }
      else if ( wall == Direction.South )
      {
         if ( cell.getYZeroBased() < this.verticalRows[0].length )
         {
            return this.verticalRows[cell.getXZeroBased()][cell.getYZeroBased()];
         }
      }
      else if ( wall == Direction.West )
      {
         if ( cell.getXZeroBased() > 0 )
         {
            return this.horizontalRows[cell.getYZeroBased()][cell.getXZeroBased() - 1];
         }
      }
      else if ( wall == Direction.East )
      {
         if ( cell.getXZeroBased() < this.horizontalRows[0].length )
         {
            return this.horizontalRows[cell.getYZeroBased()][cell.getXZeroBased()];
         }
      }
      return null; //No valid wall was found.
   }

   static class WallStatus
   {
      boolean value = false;
   }

   @Override
   public MazeWall getWall( final MazeCell cell, final Direction direction )
   {
      return new MazeWall()
      {

         private final WallStatus status = getWallStatus( cell, direction );

         @Override
         public boolean isSet()
         {
            if ( status != null )
               return this.status.value;
            else
               return true;
         }

         @Override
         public void set( boolean value )
         {
            if ( status != null )
               this.status.value = value;
         }
      };
   }
}
