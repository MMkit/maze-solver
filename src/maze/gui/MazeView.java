package maze.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.EnumSet;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.MazeModelWriteable;
import maze.model.WallDirection;

/**
 * A view of the maze model. This swing component will render a GUI of a maze
 * model displaying the maze, allowing the maze to be edited, and animating the
 * robot in the maze.
 * @author Luke Last
 */
public class MazeView extends JPanel
{
   //Temporary model.
   //private MazeModelWriteable model = new maze.model.MazeModelStub();
   private MazeModelWriteable model = new Maze();

   private int cellWidth = 44;
   private int cellHeight = 44;
   private int wallWidth = 14;

   private MazeCell active;

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

   /**
    * Constructor.
    */
   public MazeView()
   {

      this.addMouseMotionListener( new MouseMotionAdapter()
      {
         @Override
         public void mouseMoved( MouseEvent e )
         {
            active = getHostMazeCell( e.getPoint() );
            //System.out.println( e.getModifiers() );
            repaint();
         }

         @Override
         public void mouseDragged( MouseEvent e )
         {
            //System.out.println( e.getModifiers() );
         }
      } );

      this.addMouseListener( new MouseInputAdapter()
      {
         @Override
         public void mousePressed( MouseEvent e )
         {
            MazeCell cell = getHostMazeCell( e.getPoint() );
            for ( WallDirection wall : WallDirection.values() )
            {
               if ( getWallLocation( cell, wall ).contains( e.getPoint() ) )
               {
                  if ( model.isWall( cell, wall ) )
                  {
                     model.clearWall( cell, wall );
                  }
                  else
                  {
                     model.enableWall( cell, wall );
                  }
                  repaint();
                  break;
               }
            }

         }
      } );
   }

   @Override
   protected void paintComponent( Graphics arg )
   {
      //final Color emptyWall = new Color( 220, 220, 220 );
      Graphics2D g = (Graphics2D) arg;
      g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
      g.setColor( Color.white );
      g.fillRect( 0, 0, 1024, 768 );

      final int mazeWidth = this.model.getSize().width * this.cellWidth;
      final int mazeHeight = this.model.getSize().height * this.cellHeight;
      g.setPaint( new GradientPaint( new Point( 0, 0 ),
                                     Color.WHITE,
                                     new Point( mazeWidth / 2, mazeHeight / 2 ),
                                     new Color( 229, 236, 255 ),
                                     true ) );
      g.fillRect( 0, 0, mazeWidth, mazeHeight );

      for ( int x = 0; x < this.model.getSize().width; x++ )
      {
         for ( int y = 0; y < this.model.getSize().height; y++ )
         {
            final MazeCell cell = new MazeCell( x + 1, y + 1 );
            final int locX = ( x + 1 ) * this.cellWidth - ( this.wallWidth / 2 );
            final int locY = ( y + 1 ) * this.cellWidth - ( this.wallWidth / 2 );

            g.setColor( Color.black );
            g.fillRect( locX, locY, this.wallWidth, this.wallWidth );
            g.fill( this.getTopLeft( cell ) );

            EnumSet<WallDirection> wallsToPaint = EnumSet.of( WallDirection.South,
                                                              WallDirection.East );
            if ( cell.getX() == 1 )
            {
               wallsToPaint.add( WallDirection.West );
            }
            if ( cell.getY() == 1 )
            {
               wallsToPaint.add( WallDirection.North );
            }
            for ( WallDirection wall : wallsToPaint )
            {
               if ( this.model.isWall( cell, wall ) )
               {
                  //g.setColor( Color.RED );
                  g.setPaint( new GradientPaint( new Point( 0, 0 ),
                                                 new Color( 0, 94, 189 ),
                                                 new Point( mazeWidth / 2, mazeHeight / 2 ),
                                                 new Color( 0, 56, 112 ),
                                                 true ) );
               }
               else
               //No wall here.
               {
                  final Color lightBlue = new Color( 229, 236, 255 );
                  final Color blue = new Color( 204, 218, 255 );
                  //g.setColor( emptyWall );
                  g.setPaint( new GradientPaint( new Point( 0, 0 ),
                                                 lightBlue,
                                                 new Point( mazeWidth / 2, mazeHeight / 2 ),
                                                 blue,
                                                 true ) );
               }

               //Composite def = g.getComposite();
               //g.setComposite( AlphaComposite.getInstance( AlphaComposite.XOR, 0.1f ) );
               g.fill( this.getWallLocation( cell, wall ) );
               //g.setComposite( def );
            }

            if ( this.active != null && this.active.equals( cell ) )
            {
               g.setColor( Color.YELLOW );
               //g.fill( this.getWallNorth( cell ) );
               g.fillRect( cell.getXZeroBased() * this.cellWidth + this.getHalfWallWidth(),
                           cell.getYZeroBased() * this.cellHeight + this.getHalfWallWidth(),
                           this.cellWidth - this.wallWidth,
                           this.cellHeight - this.wallWidth );
            }

         }
      }
   }

   private Point getCellCenter( MazeCell cell )
   {
      return new Point( ( cell.getXZeroBased() * this.cellWidth ) + this.cellWidth / 2,
                        ( cell.getYZeroBased() * this.cellHeight ) + this.cellHeight / 2 );
   }

   private Rectangle getWallLocation( MazeCell cell, WallDirection wall )
   {
      Point center = this.getCellCenter( cell );
      if ( wall == WallDirection.North )
      {
         return new Rectangle( center.x - ( this.getHalfCellWidth() - this.getHalfWallWidth() ),
                               center.y - ( this.getHalfCellHeight() + this.getHalfWallWidth() ),
                               this.cellWidth - this.wallWidth,
                               this.wallWidth );
      }
      else if ( wall == WallDirection.South )
      {
         return new Rectangle( center.x - ( this.getHalfCellWidth() - this.getHalfWallWidth() ),
                               center.y + ( this.getHalfCellHeight() - this.getHalfWallWidth() ),
                               this.cellWidth - this.wallWidth,
                               this.wallWidth );
      }
      else if ( wall == WallDirection.East )
      {
         return new Rectangle( center.x + ( this.getHalfCellWidth() - this.getHalfWallWidth() ),
                               center.y - ( this.getHalfCellHeight() - this.getHalfWallWidth() ),
                               this.wallWidth,
                               this.cellWidth - this.wallWidth );
      }
      else if ( wall == WallDirection.West )
      {
         return new Rectangle( center.x - ( this.getHalfCellWidth() + this.getHalfWallWidth() ),
                               center.y - ( this.getHalfCellHeight() - this.getHalfWallWidth() ),
                               this.wallWidth,
                               this.cellWidth - this.wallWidth );
      }

      return new Rectangle();
   }

   private Rectangle getTopLeft( MazeCell cell )
   {
      Point center = this.getCellCenter( cell );
      return new Rectangle( center.x - ( this.getHalfCellWidth() + this.getHalfWallWidth() ),
                            center.y - ( this.getHalfCellHeight() + this.getHalfWallWidth() ),
                            this.wallWidth,
                            this.wallWidth );
   }

   private MazeCell getHostMazeCell( Point pointerLocation )
   {
      return new MazeCell( ( pointerLocation.x / this.cellWidth ) + 1,
                           ( pointerLocation.y / this.cellHeight ) + 1 );
   }

   static class CellSizeModel
   {
      private int cellWidth = 40;
      private int cellHeight = 40;
      private int wallWidth = 14;
      private int wallHeight = 14;
   }
}
