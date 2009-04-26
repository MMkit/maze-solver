package maze.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import maze.model.MazeCell;
import maze.model.MazeModelReadonly;
import maze.model.MazeModelStub;

public class MazeView extends JPanel
{
   private MazeModelReadonly model = new MazeModelStub();
   private int cellWidth = 40;
   private int cellHeight = 40;
   private int wallWidth = 12;

   private int getHalfCellWidth()
   {
      return this.cellWidth / 2;
   }

   private int getHalfCellHeight()
   {
      return this.cellHeight / 2;
   }

   private int getHalfWallWidth()
   {
      return this.wallWidth / 2;
   }

   public MazeView()
   {
      // TODO Auto-generated constructor stub
   }

   @Override
   protected void paintComponent( Graphics arg )
   {
      Graphics2D g = (Graphics2D) arg;
      g.setColor( Color.white );
      g.fillRect( 0, 0, 1024, 768 );

      for ( int x = 0; x < this.model.getSizeX(); x++ )
      {
         for ( int y = 0; y < this.model.getSizeY(); y++ )
         {
            final MazeCell cell = new MazeCell( x + 1, y + 1 );
            final int locX = ( x + 1 ) * this.cellWidth - ( this.wallWidth / 2 );
            final int locY = ( y + 1 ) * this.cellWidth - ( this.wallWidth / 2 );
            final int wallLength = this.cellWidth - this.wallWidth;

            g.setColor( Color.gray );
            g.fillRect( locX, locY - wallLength, this.wallWidth, wallLength );

            g.setColor( Color.gray );
            g.fillRect( locX - wallLength, locY, wallLength, this.wallWidth );

            g.setColor( Color.black );
            g.fillRect( locX, locY, this.wallWidth, this.wallWidth );
            g.fill( this.getTopLeft( cell ) );

            g.setColor( Color.GREEN );
            g.fill( this.getWallNorth( cell ) );

         }
      }
   }

   private Point getCellCenter( MazeCell cell )
   {
      return new Point( ( cell.getXZeroBased() * this.cellWidth ) + this.cellWidth / 2,
                        ( cell.getYZeroBased() * this.cellHeight ) + this.cellHeight / 2 );
   }

   private Rectangle getWallNorth( MazeCell cell )
   {
      Point center = this.getCellCenter( cell );
      return new Rectangle( center.x - ( this.getHalfCellWidth() - this.getHalfWallWidth() ),
                            center.y - ( this.getHalfCellHeight() + this.getHalfWallWidth() ),
                            this.cellWidth - this.wallWidth,
                            this.wallWidth );

   }

   private Rectangle getTopLeft( MazeCell cell )
   {
      Point center = this.getCellCenter( cell );
      return new Rectangle( center.x - ( this.getHalfCellWidth() + this.getHalfWallWidth() ),
                            center.y - ( this.getHalfCellHeight() + this.getHalfWallWidth() ),
                            this.wallWidth,
                            this.wallWidth );
   }
}
