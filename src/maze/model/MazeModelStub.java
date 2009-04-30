package maze.model;

import java.awt.Dimension;

/**
 * This is a temporary maze model stub used for testing the gui stuff before we
 * have a real maze model.
 * @author Luke Last
 */
public class MazeModelStub implements MazeModelWriteable
{
   //Holds vertical wall segments.
   WallStatus[][] horizontalRows = new WallStatus[16][15];
   //Holds horizontal wall segments.
   WallStatus[][] verticalRows = new WallStatus[16][15];

   public MazeModelStub()
   {
      this.initRows( this.horizontalRows );
      this.initRows( this.verticalRows );
      this.getWallStatus( new MazeCell( 1, this.getSize().height ), WallDirection.East ).value = true;
      MazeCell cell = new MazeCell( this.getSize().width / 2, this.getSize().height / 2 );
      this.getWallStatus( cell, WallDirection.West ).value = true;
      this.getWallStatus( cell, WallDirection.North ).value = true;
      cell = cell.plusX( 1 );
      this.getWallStatus( cell, WallDirection.North ).value = true;
      this.getWallStatus( cell, WallDirection.East ).value = true;
      cell = cell.plusY( 1 );
      this.getWallStatus( cell, WallDirection.East ).value = true;
      this.getWallStatus( cell, WallDirection.South ).value = true;
      cell = cell.plusX( -1 );
      this.getWallStatus( cell, WallDirection.South ).value = true;
      this.getWallStatus( cell, WallDirection.West ).value = true;
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

   @Override
   public boolean isWall( MazeCell cell, WallDirection wall )
   {
      final WallStatus foundWall = this.getWallStatus( cell, wall );
      if ( foundWall != null )
      {
         return foundWall.value;
      }
      return true;
   }

   @Override
   public void clearWall( MazeCell cell, WallDirection wall )
   {
      this.setWall( cell, wall, false );
   }

   @Override
   public void enableWall( MazeCell cell, WallDirection wall )
   {
      this.setWall( cell, wall, true );
   }

   private void setWall( MazeCell cell, WallDirection wall, boolean set )
   {
      final WallStatus foundWall = this.getWallStatus( cell, wall );
      if ( foundWall != null )
      {
         foundWall.value = set;
      }
   }

   private WallStatus getWallStatus( MazeCell cell, WallDirection wall )
   {
      if ( wall == WallDirection.North )
      {
         if ( cell.getY() > 1 )
            return this.verticalRows[cell.getXZeroBased()][cell.getYZeroBased() - 1];
      }
      else if ( wall == WallDirection.South )
      {
         if ( cell.getYZeroBased() < this.verticalRows[0].length )
            return this.verticalRows[cell.getXZeroBased()][cell.getYZeroBased()];
      }
      else if ( wall == WallDirection.West )
      {
         if ( cell.getXZeroBased() > 0 )
            return this.horizontalRows[cell.getYZeroBased()][cell.getXZeroBased() - 1];
      }
      else if ( wall == WallDirection.East )
      {
         if ( cell.getXZeroBased() < this.horizontalRows[0].length )
            return this.horizontalRows[cell.getYZeroBased()][cell.getXZeroBased()];
      }
      return null; //No valid wall was found.
   }

   static class WallStatus
   {
      public boolean value = false;
   }

   @Override
   public boolean isPegLegal( MazeCell cell, PegLocation peg )
   {
      throw new RuntimeException( "Not implemented." );
   }

}
