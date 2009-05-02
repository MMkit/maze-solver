package maze.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.PegLocation;
import maze.model.MazeModel.MazeWall;

/**
 * A view of the maze model. This swing component will render a GUI of a maze
 * model displaying the maze, allowing the maze to be edited, and animating the
 * robot in the maze.
 * @author Luke Last
 */
public class MazeView extends JPanel
{
   //Temporary model.
   //private MazeModel model = new maze.model.MazeModelStub();
   /**
    * The maze model that stores the configuration of the maze.
    */
   private MazeModel model = new Maze();
   /**
    * This holds the sizes of the cells and walls.
    */
   private final CellSizeModel csm = new CellSizeModel();
   /**
    * Holds the active cell that the mouse is hovering over.
    */
   private MazeCell active;

   public MazeModel getModel()
   {
      return model;
   }

   public void setModel( MazeModel model )
   {
      this.model = model;
   }

   /**
    * Constructor.
    */
   public MazeView()
   {
      //Create an event handler to handle mouse events.
      MouseAdapter mouseAdapter = new MouseAdapter()
      {
         @Override
         public void mouseMoved( MouseEvent e )
         {
            try
            {
               active = getHostMazeCell( e.getPoint() );
               repaint();
            }
            catch ( Exception ex )
            {
            }
         }

         @Override
         public void mouseDragged( MouseEvent e )
         {
            try
            {
               MazeWall wall = getWall( e.getPoint() );
               if ( SwingUtilities.isLeftMouseButton( e ) )
               {
                  wall.set( true );
               }
               else if ( SwingUtilities.isRightMouseButton( e ) )
               {
                  wall.set( false );
               }
               repaint();
            }
            catch ( Exception ex )
            {
            }
         }

         @Override
         public void mousePressed( MouseEvent e )
         {
            try
            {
               final MazeWall wall = getWall( e.getPoint() );
               //Flip the status of the wall.
               wall.set( !wall.isSet() );
               repaint();
            }
            catch ( Exception ex )
            {
            }
         }
      };
      this.addMouseListener( mouseAdapter );
      this.addMouseMotionListener( mouseAdapter );
   }

   /**
    * This is where the graphics of this component gets painted.
    */
   @Override
   protected void paintComponent( Graphics arg )
   {
      this.csm.setCellWidth( this.getSize().width / this.model.getSize().width );
      this.csm.setCellHeight( this.getSize().height / this.model.getSize().height );
      this.csm.setWallWidth( this.csm.getCellWidth() / 4 );
      this.csm.setWallHeight( this.csm.getCellHeight() / 4 );
      Graphics2D g = (Graphics2D) arg;
      g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
      g.setColor( Color.white );
      g.fillRect( 0, 0, 1024, 768 );

      final int mazeWidth = this.model.getSize().width * this.csm.getCellWidth();
      final int mazeHeight = this.model.getSize().height * this.csm.getCellHeight();
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

            g.setColor( Color.black );
            g.fill( this.getPegRegion( cell, PegLocation.BottomRight ) );

            final EnumSet<Direction> wallsToPaint = EnumSet.of( Direction.South, Direction.East );

            //We are painting the first cell in the row.
            if ( cell.getX() == 1 )
            {
               wallsToPaint.add( Direction.West );
               g.setColor( Color.black );
               g.fill( this.getPegRegion( cell, PegLocation.TopLeft ) );
               //We in the bottom left corner cell.
               if ( cell.getY() == this.model.getSize().height )
               {
                  g.fill( this.getPegRegion( cell, PegLocation.BottomLeft ) );
               }
            }

            //We are painting the top horizontal row.
            if ( cell.getY() == 1 )
            {
               wallsToPaint.add( Direction.North );
               g.setColor( Color.black );
               g.fill( this.getPegRegion( cell, PegLocation.TopRight ) );
            }
            for ( Direction wall : wallsToPaint )
            {
               if ( this.model.getWall( cell, wall ).isSet() )
               {
                  g.setPaint( new GradientPaint( new Point( 0, 0 ),
                                                 new Color( 0, 94, 189 ),
                                                 new Point( mazeWidth / 2, mazeHeight / 2 ),
                                                 new Color( 0, 56, 112 ),
                                                 true ) );
               }
               else
               {
                  //No wall is set so paint the light colored wall segment.
                  final Color lightBlue = new Color( 229, 236, 255 );
                  final Color blue = new Color( 204, 218, 255 );
                  g.setPaint( new GradientPaint( new Point( 0, 0 ),
                                                 lightBlue,
                                                 new Point( mazeWidth / 2, mazeHeight / 2 ),
                                                 blue,
                                                 true ) );
               }
               g.fill( this.getWallLocation( cell, wall ) );
            }

            if ( this.active != null && this.active.equals( cell ) )
            {
               g.setColor( Color.YELLOW );
               g.fillRect( cell.getXZeroBased() *
                                 this.csm.getCellWidth() +
                                 this.csm.getWallWidthHalf(),
                           cell.getYZeroBased() *
                                 this.csm.getCellHeight() +
                                 this.csm.getWallHeightHalf(),
                           this.csm.getCellWidth() - this.csm.getWallWidth(),
                           this.csm.getCellHeight() - this.csm.getWallHeight() );
            }

         } //End y loop.
      } //End x loop.
   } //End paintComponent().

   /**
    * Turns a maze cell into global coordinates for the center of the cell.
    */
   private Point getCellCenter( MazeCell cell )
   {
      return new Point( ( cell.getXZeroBased() * this.csm.getCellWidth() ) +
                              this.csm.getCellWidthHalf(),
                        ( cell.getYZeroBased() * this.csm.getCellHeight() ) +
                              this.csm.getCellHeightHalf() );
   }

   /**
    * Get a rectangle covering the wall segment with component relative
    * coordinates.
    */
   private Rectangle getWallLocation( MazeCell cell, Direction wall )
   {
      Point center = this.getCellCenter( cell );
      if ( wall == Direction.North )
      {
         return new Rectangle( center.x -
                                     ( this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf() ),
                               center.y -
                                     ( this.csm.getCellHeightHalf() + this.csm.getWallHeightHalf() ),
                               this.csm.getCellWidth() - this.csm.getWallWidth(),
                               this.csm.getWallHeight() );
      }
      else if ( wall == Direction.South )
      {
         return new Rectangle( center.x -
                                     ( this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf() ),
                               center.y +
                                     ( this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf() ),
                               this.csm.getCellWidth() - this.csm.getWallWidth(),
                               this.csm.getWallHeight() );
      }
      else if ( wall == Direction.East )
      {
         return new Rectangle( center.x +
                                     ( this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf() ),
                               center.y -
                                     ( this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf() ),
                               this.csm.getWallWidth(),
                               this.csm.getCellHeight() - this.csm.getWallHeight() );
      }
      else if ( wall == Direction.West )
      {
         return new Rectangle( center.x -
                                     ( this.csm.getCellWidthHalf() + this.csm.getWallWidthHalf() ),
                               center.y -
                                     ( this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf() ),
                               this.csm.getWallWidth(),
                               this.csm.getCellHeight() - this.csm.getWallHeight() );
      }
      return new Rectangle(); //should never get here.
   }

   /**
    * Get the absolute region of a peg with respect to the given maze cell.
    */
   private Rectangle getPegRegion( MazeCell cell, PegLocation peg )
   {
      if ( peg == PegLocation.TopLeft )
      {
         return new Rectangle( ( cell.getX() * this.csm.getCellWidth() ) -
                                     this.csm.getWallWidthHalf() -
                                     this.csm.getCellWidth(),
                               ( cell.getY() * this.csm.getCellHeight() ) -
                                     this.csm.getCellHeight() -
                                     this.csm.getWallHeightHalf(),
                               this.csm.getWallWidth(),
                               this.csm.getWallHeight() );
      }
      else if ( peg == PegLocation.TopRight )
      {
         return new Rectangle( cell.getX() * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
                               ( cell.getY() * this.csm.getCellHeight() ) -
                                     this.csm.getCellHeight() -
                                     this.csm.getWallHeightHalf(),
                               this.csm.getWallWidth(),
                               this.csm.getWallHeight() );
      }
      else if ( peg == PegLocation.BottomRight )
      {
         return new Rectangle( cell.getX() * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
                               cell.getY() *
                                     this.csm.getCellHeight() -
                                     this.csm.getWallHeightHalf(),
                               this.csm.getWallWidth(),
                               this.csm.getWallHeight() );
      }
      else if ( peg == PegLocation.BottomLeft )
      {
         return new Rectangle( cell.getXZeroBased() *
                                     this.csm.getCellWidth() -
                                     this.csm.getWallWidthHalf(),
                               cell.getY() *
                                     this.csm.getCellHeight() -
                                     this.csm.getWallHeightHalf(),
                               this.csm.getWallWidth(),
                               this.csm.getWallHeight() );
      }
      return new Rectangle(); //Should never get here.
   }

   /**
    * Converts a mouse pointer position into the maze cell that it is in.
    * @throws Exception If the pointer location is not within the maze.
    */
   private MazeCell getHostMazeCell( Point pointerLocation ) throws Exception
   {
      MazeCell cell = new MazeCell( ( pointerLocation.x / this.csm.getCellWidth() ) + 1,
                                    ( pointerLocation.y / this.csm.getCellHeight() ) + 1 );
      if ( cell.isInRange( this.model.getSize() ) )
      {
         return cell;
      }
      else
      {
         throw new Exception( "The pointer location is outside of the current maze." );
      }
   }

   /**
    * @param cell
    * @param mouseLocation
    * @return
    * @throws java.lang.Exception If the mouse pointer isn't actually on a wall.
    */
   private Direction getWallDirection( MazeCell cell, Point mouseLocation ) throws Exception
   {
      for ( Direction direction : Direction.values() )
      {
         if ( getWallLocation( cell, direction ).contains( mouseLocation ) )
         {
            return direction;
         }
      }
      throw new Exception( "Not inside a wall." );
   }

   /**
    * Converts a mouse pointer location into an actual maze wall object.
    * @param mouseLocation
    * @return
    * @throws java.lang.Exception If the mouse pointer isn't actually on a wall.
    */
   private MazeWall getWall( Point mouseLocation ) throws Exception
   {
      final MazeCell cell = getHostMazeCell( mouseLocation );
      return model.getWall( cell, getWallDirection( cell, mouseLocation ) );
   }

   /**
    * This model stores the sizes of the cells and wall segments that are drawn
    * to the screen.
    */
   static class CellSizeModel
   {
      private int cellWidth = 44;
      private int cellHeight = 50;
      private int wallWidth = 10;
      private int wallHeight = 10;

      public void setCellWidth( int cellWidth )
      {
         this.cellWidth = cellWidth;
         if ( ( this.cellWidth & 1 ) == 1 )
            this.cellWidth--;
      }

      public void setCellHeight( int cellHeight )
      {
         this.cellHeight = cellHeight;
         if ( ( this.cellHeight & 1 ) == 1 )
            this.cellHeight--;
      }

      public void setWallWidth( int wallWidth )
      {
         this.wallWidth = wallWidth;
         if ( ( this.wallWidth & 1 ) == 1 )
            this.wallWidth++;
      }

      public void setWallHeight( int wallHeight )
      {
         this.wallHeight = wallHeight;
         if ( ( this.wallHeight & 1 ) == 1 )
            this.wallHeight++;
      }

      public int getCellWidth()
      {
         return cellWidth;
      }

      public int getCellHeight()
      {
         return cellHeight;
      }

      public int getWallWidth()
      {
         return wallWidth;
      }

      public int getWallHeight()
      {
         return wallHeight;
      }

      public int getCellWidthHalf()
      {
         return cellWidth / 2;
      }

      public int getCellHeightHalf()
      {
         return cellHeight / 2;
      }

      public int getWallWidthHalf()
      {
         return wallWidth / 2;
      }

      public int getWallHeightHalf()
      {
         return wallHeight / 2;
      }
   }
}
