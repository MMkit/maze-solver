package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import maze.model.CellSizeModel;
import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotPathModel;
import maze.util.Listener;

/**
 * This swing component displays a graphical view of a maze. It also has the
 * capability to draw different things onto the maze like an image for the
 * robots location.<br />
 * Painting is handled by a UI delegate {@link maze.gui.MazePainter} that allows
 * different themes to be used.
 * @author Luke Last
 */
public class MazeView2 extends JComponent implements ComponentListener, Listener<MazeCell>,
      MazeViewInterface
{
   private static final long serialVersionUID = 3249468255178771818L;
   private static final int WALL_SIZE_DIVIDER = 6;
   /**
    * The maze model that stores the configuration of the maze.
    */
   private MazeModel model;
   /**
    * The background image holds the rendered maze cells. When cells are
    * invalidated they are redrawn on this background image. When the screen is
    * resized that reference is set to null so a newly sized image can be
    * created.
    */
   private BufferedImage backgroundImage;
   /**
    * Stores a reference to the graphics object for the background image. This
    * prevents is from having to request it from the image every time.
    */
   private Graphics2D backgroundGraphics;
   /**
    * Stores the sizes of a cell and its walls.
    */
   private final CellSizeModel csm = new CellSizeModel(false);
   /**
    * UI delegate used for drawing each maze component.
    */
   private MazePainter painter = new MazePainterDefault();
   /**
    * The current location of the robot while it is animating.
    */
   private volatile Point robotLocation = null;
   /**
    * The current rotation of the robot in radians.
    */
   private volatile double robotRotation = 0.0;
   /**
    * This is null unless an animation is running and then the
    * <code>RobotAnimator</code> will populate it. This stores information about
    * the robots paths and history.
    */
   private RobotPathModel robotPathModel;
   /**
    * A flag for redrawing everything.
    */
   private boolean repaintAll = true;
   /**
    * Stores a set of maze cells that have been invalidated and need to be
    * redrawn. ALL access to this set should be synchronized on the object
    * itself.
    */
   private final Set<MazeCell> invalidatedCells = new HashSet<MazeCell>();
   private int[][] understandingInt = null;
   private Direction[][] understandingDir = null;
   private boolean drawPathCurrent = true;
   private boolean drawPathFirst = true;
   private boolean drawPathBest = true;
   private boolean drawUnderstanding = true;
   private boolean drawFog = true;

   /**
    * Constructor.
    */
   public MazeView2()
   {
      // We maintain our own background image buffer so we can turn this off.
      this.setDoubleBuffered(false);
      // For catching resize events.
      this.addComponentListener(this);
   }

   @Override
   public void componentHidden(final ComponentEvent e)
   {}

   @Override
   public void componentMoved(final ComponentEvent e)
   {}

   /**
    * When the component is resized we have to resize the cells and walls and
    * resize the background image.
    */
   @Override
   public void componentResized(final ComponentEvent e)
   {
      if (model != null)
      {
         this.backgroundImage = null;
         this.repaintAll = true;
         this.csm.setCellWidth( (this.getWidth() - this.csm.getWallWidth()) /
                               this.model.getSize().width);
         this.csm.setCellHeight( (this.getHeight() - this.csm.getWallHeight()) /
                                this.model.getSize().height);

         final int wallSize = Math.min(csm.getCellWidth(), csm.getCellHeight()) / WALL_SIZE_DIVIDER;
         this.csm.setWallWidth(wallSize);
         this.csm.setWallHeight(wallSize);
         this.painter.setMazeSize(this.getMazeSize());
         this.repaint();
      }
   }

   @Override
   public void componentShown(final ComponentEvent e)
   {}

   /**
    * Calls the draw cell method on every cell in the maze.
    * @param g What do draw on.
    */
   private void drawAllCells(final Graphics2D g)
   {
      if (g != null && this.model != null)
      {
         //Loop through each cell in the maze.
         for (int x = 1; x <= this.model.getSize().width; x++)
         {
            for (int y = 1; y <= this.model.getSize().height; y++)
            {
               final MazeCell cell = new MazeCell(x, y);
               this.drawCell(g, cell);
            } //End y loop.
         } //End x loop.
      }
   }

   /**
    * Draws an arrow graphic.
    * @param g What do draw on.
    * @param local Direction to point the arrow.
    * @param x Horizontal pixel location to draw arrow.
    * @param y Vertical pixel location to draw arrow.
    */
   private void drawArrow(final Graphics2D g, final Direction local, final int x, final int y)
   {
      //Draws an arrow in the direction of "local" centered on the point (x,y)
      if (local.equals(Direction.North))
      {
         final int[] ys =
         {
            y + this.csm.getCellHeight() * 3 / 8, y, y, y - this.csm.getCellHeight() * 3 / 8, y, y
         };
         final int[] xs =
         {
            x, x - this.csm.getCellWidth() / 8, x - this.csm.getCellWidth() / 4, x,
            x + this.csm.getCellWidth() / 4, x + this.csm.getCellWidth() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
      if (local.equals(Direction.South))
      {
         final int[] ys =
         {
            y - this.csm.getCellHeight() * 3 / 8, y, y, y + this.csm.getCellHeight() * 3 / 8, y, y
         };
         final int[] xs =
         {
            x, x - this.csm.getCellWidth() / 8, x - this.csm.getCellWidth() / 4, x,
            x + this.csm.getCellWidth() / 4, x + this.csm.getCellWidth() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
      if (local.equals(Direction.West))
      {
         final int[] xs =
         {
            x + this.csm.getCellWidth() * 3 / 8, x, x, x - this.csm.getCellWidth() * 3 / 8, x, x
         };
         final int[] ys =
         {
            y, y - this.csm.getCellHeight() / 8, y - this.csm.getCellHeight() / 4, y,
            y + this.csm.getCellHeight() / 4, y + this.csm.getCellHeight() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
      if (local.equals(Direction.East))
      {
         final int[] xs =
         {
            x - this.csm.getCellWidth() * 3 / 8, x, x, x + this.csm.getCellWidth() * 3 / 8, x, x
         };
         final int[] ys =
         {
            y, y - this.csm.getCellHeight() / 8, y - this.csm.getCellHeight() / 4, y,
            y + this.csm.getCellHeight() / 4, y + this.csm.getCellHeight() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
   }

   /**
    * The primary draw method for a cell. This should draw all aspects of a
    * cell.
    * @param g Where to draw.
    * @param cell The cell in question.
    */
   private void drawCell(final Graphics2D g, final MazeCell cell)
   {
      this.painter.drawCellBackground(g, this.getCellAreaInner(cell));

      if (this.model.getWall(cell, Direction.East).isSet())
      {
         this.painter.drawWallSet(g, this.getWallArea(cell, Direction.East));
      }
      else
      {
         this.painter.drawWallEmpty(g, this.getWallArea(cell, Direction.East));
      }
      if (this.model.getWall(cell, Direction.South).isSet())
      {
         this.painter.drawWallSet(g, this.getWallArea(cell, Direction.South));
      }
      else
      {
         this.painter.drawWallEmpty(g, this.getWallArea(cell, Direction.South));
      }
      this.painter.drawPeg(g, this.getPegArea(cell));
      if (this.robotPathModel != null)
      {
         //Draw the fog of war.
         if (this.drawFog && !this.robotPathModel.hasCellBeenVisited(cell))
         {
            final Rectangle area = this.getCellArea(cell);
            final MazeCell east = cell.neighbor(Direction.East);
            final MazeCell south = cell.neighbor(Direction.South);
            if (east.isInRange(this.model.getSize()) &&
                this.robotPathModel.hasCellBeenVisited(east))
            {
               area.width -= this.csm.getWallWidth();
            }
            if (south.isInRange(this.model.getSize()) &&
                this.robotPathModel.hasCellBeenVisited(south))
            {
               area.height -= this.csm.getWallHeight();
            }
            this.painter.drawFog(g, area);
         }
         // Draw a current path of dots.
         if (this.drawPathCurrent && this.robotPathModel.getPathRecent().contains(cell))
         {
            final EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);
            for (final Direction dir : this.getAdjacentDirections(cell))
            {
               if (!this.model.getWall(cell, dir).isSet() &&
                   this.robotPathModel.getPathRecent().contains(cell.neighbor(dir)))
               {
                  directions.add(dir);
               }
            }
            //this.paints.drawRunCurrent(g, this.getCellAreaInner(cell), directions);
         }
      }
   }

   /**
    * Draws all cells that have been invalidated.
    * @param g Where to draw.
    */
   private void drawInvalidatedCells(final Graphics2D g)
   {
      synchronized (this.invalidatedCells)
      {
         for (final MazeCell cell : this.invalidatedCells)
         {
            this.drawCell(g, cell);
            //System.out.println("Drew cell: " + cell);
         }
         this.invalidatedCells.clear();
      }
   }

   /**
    * Draws the top and left outside walls as these don't fall inside of any
    * cells.
    * @param g Where to draw.
    */
   private void drawOutsideWalls(final Graphics2D g)
   {
      if (this.model != null && this.painter != null)
      {
         final Rectangle pegArea = new Rectangle(0,
                                                 0,
                                                 this.csm.getWallWidth(),
                                                 this.csm.getWallHeight());
         final Rectangle wallArea = new Rectangle(this.csm.getWallWidth(),
                                                  0,
                                                  this.csm.getCellWidthInner(),
                                                  this.csm.getWallHeight());

         for (int i = 0; i <= this.model.getSize().width; i++)
         {
            this.painter.drawPeg(g, pegArea);
            pegArea.x += this.csm.getCellWidth();
            // We draw more pegs than walls.
            if (i == 0)
            {
               continue;
            }
            this.painter.drawWallSet(g, wallArea);
            wallArea.x += this.csm.getCellWidth();
         }

         pegArea.setLocation(0, this.csm.getCellHeight());
         wallArea.setSize(this.csm.getWallWidth(), this.csm.getCellHeightInner());
         wallArea.setLocation(0, this.csm.getWallHeight());
         // Draw the left side column of pegs and walls.
         for (int i = 1; i <= this.model.getSize().height; i++)
         {
            this.painter.drawPeg(g, pegArea);
            pegArea.y += this.csm.getCellHeight();
            this.painter.drawWallSet(g, wallArea);
            wallArea.y += this.csm.getCellHeight();
         }
      }
   }

   /**
    * Get the cell that the given point is located in. This gives us a means to
    * translate between coordinate positions and cells.
    * @param location A coordinate point in the maze area.
    * @return The cell or null if the point is not inside a valid cell.
    */
   @SuppressWarnings("unused")
   private MazeCell getHostCell(Point location)
   {
      try
      {
         MazeCell cell = new MazeCell( ( (location.x - csm.getWallWidth()) / csm.getCellWidth()) + 1,
                                      ( (location.y - csm.getWallHeight()) / csm.getCellHeight()) + 1);
         if (cell.isInRange(model.getSize()))
         {
            return cell;
         }
      }
      catch (Exception e)
      {}
      return null;
   }

   /**
    * Draw a robot path onto the maze. The path will have the width of the cell
    * walls.
    * @param g Where to draw.
    * @param path The cell to cell path to draw.
    * @param offset Shift the path by the given amount so that you can draw
    *           multiple paths without them overlapping. A value of 0 draws to
    *           the center of the cell.
    * @param trimTail If set to true the tail of the path will be trimmed to the
    *           location of the robot. This allows the current path to finish
    *           right on the current robot position.
    */
   private void drawPath(final Graphics2D g, final List<MazeCell> path, final int offset,
         boolean trimTail)
   {
      if (path != null && !path.isEmpty())
      {
         MazeCell here = path.get(0);
         MazeCell there;
         int x, y;
         int width, height;
         for (int i = 1; i < path.size(); i++)
         {
            there = path.get(i);
            final Point center = this.getCellCenterInner(here);
            if (here.getX() < there.getX())
            {
               //here is west of there, going east.
               x = center.x + this.csm.getWallWidthHalf();
               y = center.y - this.csm.getWallHeightHalf();
               width = this.csm.getCellWidth();
               height = this.csm.getWallHeight();
            }
            else if (here.getX() > there.getX())
            {
               //here is east of there, going west.
               x = center.x - this.csm.getWallWidthHalf() - this.csm.getCellWidth();
               y = center.y - this.csm.getWallHeightHalf();
               width = this.csm.getCellWidth();
               height = this.csm.getWallHeight();
            }
            else if (here.getY() > there.getY())
            {
               //here is south of there, going north.
               x = center.x - this.csm.getWallWidthHalf();
               y = center.y - this.csm.getWallHeightHalf() - this.csm.getCellHeight();
               width = this.csm.getWallWidth();
               height = this.csm.getCellHeight();
            }
            else
            {
               //here is north of there, going south.
               x = center.x - this.csm.getWallWidthHalf();
               y = center.y + this.csm.getWallHeightHalf();
               width = this.csm.getWallWidth();
               height = this.csm.getCellHeight();
            }
            // If we are at the last cell and we are trimming the tail up to the robot.
            if (i == path.size() - 1 && trimTail && this.robotLocation != null)
            {
               if (here.getX() < there.getX())
               {
                  //here is west of there, going east.
                  width = Math.abs(x - this.robotLocation.x);
               }
               else if (here.getX() > there.getX())
               {
                  //here is east of there, going west.
                  width -= this.robotLocation.x - x;
                  x = this.robotLocation.x;

               }
               else if (here.getY() > there.getY())
               {
                  //here is south of there, going north.
                  height -= this.robotLocation.y - y;
                  y = this.robotLocation.y;
               }
               else
               {
                  //here is north of there, going south.
                  height = this.robotLocation.y - y;
               }
            }
            g.fillRect(x - offset, y + offset, width, height);
            here = there;
         }
      }
   }

   /**
    * Draws the very top of the maze view. This is the most frequently called
    * draw method and is called every time the view is repainted. Because of
    * this it should run as fast as possible. It is much better to draw in the
    * <code>drawCell()</code> method.
    * @param g What to draw on.
    */
   private void drawTopLayer(final Graphics2D g)
   {
      this.setRenderingQualityLow(g);
      if (this.robotPathModel != null)
      {
         if (this.drawPathFirst)
         {
            g.setPaint(this.painter.getRunFirst());
            this.drawPath(g, this.robotPathModel.getPathFirst(), this.csm.getWallWidth(), false);
         }
         if (this.drawPathBest)
         {
            g.setPaint(this.painter.getRunBest());
            this.drawPath(g, this.robotPathModel.getPathBest(), -this.csm.getWallWidth(), false);
         }
         if (this.drawPathCurrent)
         {
            g.setPaint(this.painter.getRunCurrent());
            this.drawPath(g, this.robotPathModel.getPathRecent(), 0, true);
         }
      }
      if (this.drawUnderstanding)
      {
         this.drawUnderstanding(g);
      }
      if (this.getRobotLocation() != null)
      {
         this.setRenderingQualityHigh(g);
         this.painter.drawRobot(g,
                                this.getRobotLocation(),
                                this.getRobotRotation(),
                                this.csm.getCellWidthInner(),
                                this.csm.getCellHeightInner());
      }
   }

   /**
    * Set the rendering hints to low quality.
    * @param g The graphics object to set the rendering hints on.
    */
   private void setRenderingQualityLow(Graphics2D g)
   {
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
   }

   /**
    * Set the rendering hints to high quality.
    * @param g The graphics object to set the rendering hints on.
    */
   private void setRenderingQualityHigh(Graphics2D g)
   {
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   }

   /**
    * Draws the arrows and numbers on the maze.
    * @param g What to draw on.
    */
   private void drawUnderstanding(final Graphics2D g)
   {
      MazeCell here;
      if (understandingInt != null)
      {
         int local;
         for (int i = 1; i <= model.getSize().width; i++)
         {
            for (int j = 1; j <= model.getSize().height; j++)
            {
               here = new MazeCell(i, j);
               g.setColor(Color.BLACK);
               final Point center = this.getCellCenterInner(here);
               local = understandingInt[i - 1][j - 1];
               g.drawString(String.valueOf(local), center.x - 6, center.y + 2);
            }
         }
      }
      else if (understandingDir != null)
      {
         Direction local;
         for (int i = 1; i <= model.getSize().width; i++)
         {
            for (int j = 1; j <= model.getSize().height; j++)
            {
               here = new MazeCell(i, j);
               final Point center = this.getCellCenterInner(here);
               g.setColor(Color.BLACK);
               local = understandingDir[i - 1][j - 1];
               if (local != null)
               {
                  drawArrow(g, local, center.x, center.y);
               }
            }
         }
      }
   }

   /**
    * This event is triggered to invalidate a cell that needs to be repainted.
    */
   @Override
   public void eventFired(final MazeCell cell)
   {
      this.invalidateCell(cell);
   }

   /**
    * Gets all the cells that are adjacent to the given one.
    * @param cell The cell in question.
    * @return All adjacent or connecting cells.
    */
   @SuppressWarnings("unused")
   private MazeCell[] getAdjacentCells(final MazeCell cell)
   {
      final Direction[] dirs = this.getAdjacentDirections(cell).toArray(new Direction[0]);
      final MazeCell[] result = new MazeCell[dirs.length];
      for (int i = 0; i < dirs.length; i++)
      {
         result[i] = cell.neighbor(dirs[i]);
      }
      return result;
   }

   /**
    * Gets a direction for each adjacent cell that exists. The only time one
    * doesn't exists is when the given cell is on the maze edge.
    * @param cell The cell in question.
    * @return A set of directions with each direction confirming the existence
    *         of a neighbor cell.
    */
   private EnumSet<Direction> getAdjacentDirections(final MazeCell cell)
   {
      // We start with all and then remove because it is less likely.
      final EnumSet<Direction> directions = EnumSet.allOf(Direction.class);
      if (cell.getX() == 1)
      {
         directions.remove(Direction.West);

      }
      if (cell.getY() == 1)
      {
         directions.remove(Direction.North);

      }
      if (cell.getX() == this.model.getSize().width)
      {
         directions.remove(Direction.East);

      }
      if (cell.getY() == this.model.getSize().height)
      {
         directions.remove(Direction.South);

      }
      return directions;
   }

   /**
    * Get the graphics object that draws onto the background image. All drawing
    * onto the background image must use this.
    * @return The graphics object.
    */
   private Graphics2D getBackgroundGraphics()
   {
      if (this.backgroundImage == null)
      {
         // If creating a new background image make sure we paint it.
         this.repaintAll = true;
         this.backgroundGraphics = null;
         this.backgroundImage = new BufferedImage(getWidth(),
                                                  getHeight(),
                                                  BufferedImage.TYPE_INT_ARGB);
      }
      if (this.backgroundGraphics == null)
      {
         this.backgroundGraphics = this.backgroundImage.createGraphics();
         this.setRenderingQualityHigh(this.backgroundGraphics);
      }
      return this.backgroundGraphics;
   }

   /**
    * Get the pixel space of a cell.
    * @param cell The cell in question.
    * @return The location and area in pixels of where the cell is located.
    */
   private Rectangle getCellArea(final MazeCell cell)
   {
      return new Rectangle(this.csm.getWallWidth() + cell.getXZeroBased() * this.csm.getCellWidth(),
                           this.csm.getWallHeight() +
                                 cell.getYZeroBased() *
                                 this.csm.getCellHeight(),
                           this.csm.getCellWidth(),
                           this.csm.getCellHeight());
   }

   /**
    * Similar to <code>getCellArea</code> but just the inside of the cell
    * without the walls.
    * @param cell The cell in question.
    * @return Cell area minus the walls and corner peg.
    */
   private Rectangle getCellAreaInner(final MazeCell cell)
   {
      return new Rectangle(this.csm.getWallWidth() + cell.getXZeroBased() * this.csm.getCellWidth(),
                           this.csm.getWallHeight() +
                                 cell.getYZeroBased() *
                                 this.csm.getCellHeight(),
                           this.csm.getCellWidthInner(),
                           this.csm.getCellHeightInner());
   }

   /**
    * Get the center of the inner cell part without the walls.
    * @param cell The cell in question.
    * @return The point in pixel coordinates.
    */
   Point getCellCenterInner(final MazeCell cell)
   {
      return new Point(this.csm.getWallWidth() +
                             (cell.getXZeroBased() * this.csm.getCellWidth()) +
                             (this.csm.getCellWidthInner() / 2),
                       this.csm.getWallHeight() +
                             (cell.getYZeroBased() * this.csm.getCellHeight()) +
                             (this.csm.getCellHeightInner() / 2));
   }

   /**
    * Get the size of the maze in pixels.
    */
   private Dimension getMazeSize()
   {
      if (this.model != null)
      {
         return new Dimension(this.model.getSize().width * this.csm.getCellWidth(),
                              this.model.getSize().height * this.csm.getCellHeight());
      }
      else
      {
         return new Dimension();
      }
   }

   /**
    * Get the maze model being used for this view.
    */
   public MazeModel getModel()
   {
      return model;
   }

   /**
    * Get the area of the peg of a cell.
    * @param cell The cell in question.
    * @return The location and size of the peg.
    */
   private Rectangle getPegArea(final MazeCell cell)
   {
      return new Rectangle(cell.getX() * this.csm.getCellWidth(),
                           cell.getY() * this.csm.getCellHeight(),
                           this.csm.getWallWidth(),
                           this.csm.getWallHeight());
   }

   /**
    * Get the current location of the robot in absolute view coordinates.
    */
   public Point getRobotLocation()
   {
      return this.robotLocation;
   }

   /**
    * Get the current rotation of the robot in Radians.
    */
   public double getRobotRotation()
   {
      return this.robotRotation;
   }

   /**
    * Get the area of a cell wall.
    * @param cell The cell in question.
    * @param wall Which wall do you want. Must be East or South.
    * @return The absolute coordinates of the area.
    */
   private Rectangle getWallArea(final MazeCell cell, final Direction wall)
   {
      switch (wall)
      {
         case East :
            return new Rectangle(cell.getX() * this.csm.getCellWidth(),
                                 this.csm.getWallHeight() +
                                       cell.getYZeroBased() *
                                       this.csm.getCellHeight(),
                                 this.csm.getWallWidth(),
                                 this.csm.getCellHeightInner());
         case South :
            return new Rectangle(this.csm.getWallWidth() +
                                       cell.getXZeroBased() *
                                       this.csm.getCellWidth(),
                                 cell.getY() * this.csm.getCellHeight(),
                                 this.csm.getCellWidthInner(),
                                 this.csm.getWallHeight());
         default :
            throw new IllegalArgumentException("Non supported direction: " + wall);
      }
   }

   /**
    * Invalidate and repaint all cells.
    */
   public void invalidateAllCells()
   {
      this.repaintAll = true;
      this.repaint();
   }

   /**
    * Invalidate a cell and mark it to be redrawn.
    * @param cell The cell to be redrawn.
    */
   protected void invalidateCell(final MazeCell cell)
   {
      if (cell != null && this.model != null && cell.isInRange(this.model.getSize()))
      {
         synchronized (this.invalidatedCells)
         {
            this.invalidatedCells.add(cell);
         }
         super.repaint();
      }
   }

   public void loadUnderstanding(final int[][] understandingInt)
   {
      this.understandingInt = understandingInt;
   }

   public void loadUnderstandingDir(final Direction[][] arrows)
   {
      this.understandingDir = arrows;
   }

   /**
    * Master starting point for the custom painting. We want to draw as little
    * as we can to keep performance up.
    */
   @Override
   protected void paintComponent(final Graphics arg)
   {
      final Graphics2D bgg = this.getBackgroundGraphics();
      if (this.repaintAll)
      {
         this.repaintAll = false;
         this.drawOutsideWalls(bgg);
         // Clear invalidated because we are going to redraw everything.
         synchronized (this.invalidatedCells)
         {
            this.invalidatedCells.clear();
         }
         this.drawAllCells(bgg);
         // Because the draw all can take so long we still want to redraw invalidated cells after.
      }
      this.drawInvalidatedCells(bgg);
      final Graphics2D g = (Graphics2D) arg;
      g.drawImage(this.backgroundImage, null, 0, 0);
      this.drawTopLayer(g);
   }

   /**
    * @param drawPathBest the drawPathBest to set
    */
   public void setDrawPathBest(final boolean drawPathBest)
   {
      this.drawPathBest = drawPathBest;
   }

   /**
    * @param drawPathCurrent the drawPathCurrent to set
    */
   public void setDrawPathCurrent(final boolean drawPathCurrent)
   {
      if (this.drawPathCurrent != drawPathCurrent)
      {
         this.drawPathCurrent = drawPathCurrent;
         this.invalidateAllCells();
      }
   }

   /**
    * @param drawPathFirst the drawPathFirst to set
    */
   public void setDrawPathFirst(final boolean drawPathFirst)
   {
      this.drawPathFirst = drawPathFirst;
   }

   /**
    * Set whether or not this view should draw/display understanding information
    * from the AI algorithm.
    * @param draw Setter value.
    */
   public void setDrawUnderstanding(final boolean draw)
   {
      if (this.drawUnderstanding != draw)
      {
         this.drawUnderstanding = draw;
         if (this.understandingInt != null || this.understandingDir != null)
         {
            this.invalidateAllCells();
         }
      }
   }

   /**
    * Set the maze model to use for this view.
    */
   @Override
   public void setModel(final MazeModel model)
   {
      if (this.model != model)
      {
         if (this.model != null)
         {
            this.model.removeListener(this);
         }
         this.model = model;
         if (this.model != null)
         {
            this.model.addListener(this);
         }
         this.componentResized(null);
      }
   }

   /**
    * Set the robot path model that this view should use to draw information
    * about the robots path. This should be set while an animation is running
    * and then set to null after and when no animation is running.
    * @param model The model to set.
    */
   public void setRobotPathModel(final RobotPathModel model)
   {
      if (this.robotPathModel != model)
      {
         if (this.robotPathModel != null)
         {
            this.robotPathModel.removeListener(this);
         }
         this.robotPathModel = model;
         if (this.robotPathModel != null)
         {
            this.robotPathModel.addListener(this);
         }
         this.invalidateAllCells();
      }
   }

   /**
    * Sets a new position for the robot and then sets the view to repaint
    * itself.
    * @param newLocation The new location for the robot in absolute view
    *           coordinates.
    * @param newRotation The new rotation of the robot in Radians.
    */
   public void setRobotPosition(final Point newLocation, final double newRotation)
   {
      this.robotLocation = newLocation;
      this.robotRotation = newRotation;
      this.repaint();
   }

   /**
    * @param drawFog the drawFog to set
    */
   public void setDrawFog(boolean drawFog)
   {
      if (this.drawFog != drawFog)
      {
         this.drawFog = drawFog;
         this.invalidateAllCells();
      }
   }

   /**
    * Set a new maze painting delegate for this maze view to use when drawing.
    * @param newPainterDelegate The new delegate to do drawing for this view.
    */
   public void setPainterDelegate(MazePainter newPainterDelegate)
   {
      if (newPainterDelegate != null && this.painter != newPainterDelegate)
      {
         this.painter = newPainterDelegate;
         // When changing the painter we want to redraw everything.
         this.componentResized(null);
      }
   }
}