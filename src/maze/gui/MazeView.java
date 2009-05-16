package maze.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import maze.Main;
import maze.model.CellSizeModel;
import maze.model.Direction;
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
public class MazeView extends JComponent implements ComponentListener
{
   /**
    * The maze model that stores the configuration of the maze.
    */
   protected MazeModel model;
   /**
    * This holds the sizes of the cells and walls.
    */
   protected final CellSizeModel csm = new CellSizeModel();
   /**
    * Holds the active cell that the mouse is hovering over.
    */
   protected MazeCell active;
   /**
    * True if this MazeView object can edit it's model, false otherwise
    */
   private boolean editable = false;
   /**
    * Holds the pre-rendered background
    */
   private BufferedImage background = null;
   /**
    * Reference to the MouseAdapter object that handles mouse listener events.
    * Used to remove MouseListeners.
    */
   MouseAdapter mouseAdapter = null;
   /**
    * The location of the robot image in graphics coordinates.
    */
   private volatile Point robotLocation = null;
   /**
    * The current rotation of the robot in radians.
    */
   private volatile double robotRotation = 0.0;
   /**
    * The image for the micro mouse aviator that moves around the maze.
    */
   private final ImageIcon robotImage = Main.getImageResource("gui/images/mouse.png");

   protected final MazePaints paints = new MazePaints();
   //protected final MazePaints paints = new MazePaints.MazePaintsClassic();

   private boolean drawFog = false;
   private Set<MazeCell> unexplored;

   private boolean drawFirstRun = false;
   private List<MazeCell> firstRun;

   private boolean drawBestRun = false;
   private List<MazeCell> bestRun;

   private boolean drawCurrentRun = false;
   private List<MazeCell> currentRun;

   private boolean drawUnderstanding = false;
   private int[][] understandingInt = null;
   private Direction[][] understandingDir = null;

   /**
    * Constructor.
    */
   public MazeView()
   {
      this.addComponentListener(this);
   }

   /**
    * Get the maze model being used for this view.
    */
   public MazeModel getModel()
   {
      return model;
   }

   /**
    * Set the maze model to use for this view.
    */
   public void setModel(MazeModel model)
   {
      this.model = model;
      componentResized(null);
      repaint();
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
    * Sets a new position for the robot and then sets the view to repaint
    * itself.
    * @param newLocation The new location for the robot in absolute view
    *           coordinates.
    * @param newRotation The new rotation of the robot in Radians.
    */
   public void setRobotPosition(Point newLocation, double newRotation)
   {
      this.robotLocation = newLocation;
      this.robotRotation = newRotation;
      this.repaint();
   }

   /**
    * This is where the graphics of this component gets painted.
    */
   @Override
   protected void paintComponent(final Graphics arg)
   {
      if (model != null)
      {
         final Graphics2D g = (Graphics2D) arg;
         this.redrawAll(g);
      }
      else
         super.paintComponent(arg);
   }

   /**
    * Get the size of the maze in pixels.
    */
   protected Dimension getMazeSize()
   {
      return new Dimension(this.model.getSize().width * this.csm.getCellWidth(),
                           this.model.getSize().height * this.csm.getCellHeight());
   }

   /**
    * Draws the entire maze onto the given graphics device.
    */
   private void redrawAll(final Graphics2D g)
   {
      final int mazeWidth = this.model.getSize().width * this.csm.getCellWidth();
      final int mazeHeight = this.model.getSize().height * this.csm.getCellHeight();
      final Dimension modelSize = this.model.getSize();

      //Hopefully it won't hurt to always have this on.
      g.setComposite(AlphaComposite.SrcOver);

      this.paints.setGradients(this.getMazeSize());

      if (this.background == null)
      {
         this.background = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
         Graphics2D bgg = (Graphics2D) this.background.getGraphics();
         // Fill in the background color.
         bgg.setColor(Color.white);
         bgg.fillRect(0, 0, super.getWidth(), super.getHeight());
         //Paint the gradient maze background.
         bgg.setPaint(this.paints.getCellBackground());
         bgg.fillRect(0, 0, mazeWidth, mazeHeight);

         // Draw inactive walls
         bgg.setPaint(this.paints.getWallEmpty());
         for (int i = 0; i < modelSize.width + 1; i++)
         {
            //Draw vertical walls.
            bgg.fillRect(i * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
                         0,
                         this.csm.getWallWidth(),
                         mazeHeight);
         }
         for (int i = 0; i < modelSize.height + 1; i++)
         {
            //Draw horizontal walls.
            bgg.fillRect(0,
                         i * this.csm.getCellHeight() - this.csm.getWallHeightHalf(),
                         mazeWidth,
                         this.csm.getWallHeight());
         }

         // Draw outer walls
         bgg.setPaint(this.paints.getWallSet());
         bgg.fillRect(-csm.getWallWidthHalf(), -csm.getWallHeightHalf(), mazeWidth, csm.getWallHeight());
         bgg.fillRect(-csm.getWallWidthHalf(), -csm.getWallHeightHalf(), csm.getWallWidth(), mazeHeight);
         bgg.fillRect(0, mazeHeight - csm.getWallHeightHalf(), mazeWidth, csm.getWallHeight());
         bgg.fillRect(mazeWidth - csm.getWallWidthHalf(), 0, csm.getWallWidth(), mazeHeight);

         // Draw pegs
         bgg.setPaint(this.paints.getPeg());
         for (int i = 1; i <= modelSize.width + 1; i++)
            for (int j = 1; j <= modelSize.height + 1; j++)
               bgg.fill(this.getPegRegion(new MazeCell(i, j), PegLocation.TopLeft));
      } //End update background image.

      g.drawImage(background, 0, 0, null);

      //Loop through each cell in the maze.
      for (int x = 1; x <= modelSize.width; x++)
      {
         for (int y = 1; y <= modelSize.height; y++)
         {
            final MazeCell cell = new MazeCell(x, y);
            final EnumSet<Direction> wallsToPaint = EnumSet.of(Direction.South, Direction.East);
            //We are painting the first cell in the row.
            if (cell.getX() == 1)
               wallsToPaint.add(Direction.West);
            //We are painting the top horizontal row.
            if (cell.getY() == 1)
               wallsToPaint.add(Direction.North);

            g.setPaint(this.paints.getWallSet());
            for (Direction wall : wallsToPaint)
               if (this.model.getWall(cell, wall).isSet())
                  g.fill(this.getWallLocation(cell, wall));

         } //End y loop.
      } //End x loop.

      //Draw the yellow hover box. This could get axed.
      if (this.active != null)
      {
         g.setPaint(this.paints.getHover());
         g.fillRect(active.getXZeroBased() * this.csm.getCellWidth() + this.csm.getWallWidthHalf(),
                    active.getYZeroBased() * this.csm.getCellHeight() + this.csm.getWallHeightHalf(),
                    this.csm.getCellWidth() - this.csm.getWallWidth(),
                    this.csm.getCellHeight() - this.csm.getWallHeight());
      }

      if (drawFog == true)
      {
         drawFog(g);
      }

      if (drawFirstRun == true)
      {
         drawFirstRun(g);
      }

      if (drawBestRun == true)
      {
         drawBestRun(g);
      }

      if (drawCurrentRun == true)
      {
         drawCurrentRun(g);
      }

      if (drawUnderstanding == true)
      {
         drawUnderstanding(g);
      }

      // Draw the robot onto the maze.
      if (this.editable == false && this.robotLocation != null)
      {
         //We have to translate our coordinate system to make the image rotation easier.
         g.translate(this.robotLocation.x, this.robotLocation.y);

         //Back up the current graphics state.
         final AffineTransform oldTransform = g.getTransform();
         //We add half a PI to the rotation because the top of the image is forward.
         final AffineTransform transform = AffineTransform.getRotateInstance(this.robotRotation +
                                                                             Math.PI /
                                                                             2);
         //Set the image rotation transformation.
         g.transform(transform);
         g.drawImage(this.robotImage.getImage(),
                     -this.robotImage.getIconWidth() / 2,
                     -this.robotImage.getIconHeight() / 2,
                     null);
         //Restore the original graphics state.
         g.setTransform(oldTransform);
         g.translate(-this.robotLocation.x, -this.robotLocation.y);
      }

   } //End method.

   /**
    * Turns a maze cell into global coordinates for the center of the cell.
    */
   protected Point getCellCenter(MazeCell cell)
   {
      return new Point( (cell.getXZeroBased() * this.csm.getCellWidth()) + this.csm.getCellWidthHalf(),
                       (cell.getYZeroBased() * this.csm.getCellHeight()) + this.csm.getCellHeightHalf());
   }

   /**
    * Get a rectangle covering the wall segment with component relative
    * coordinates.
    */
   private Rectangle getWallLocation(MazeCell cell, Direction wall)
   {
      Point center = this.getCellCenter(cell);
      if (wall == Direction.North)
      {
         return new Rectangle(center.x - (this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf()),
                              center.y - (this.csm.getCellHeightHalf() + this.csm.getWallHeightHalf()),
                              this.csm.getCellWidth() - this.csm.getWallWidth(),
                              this.csm.getWallHeight());
      }
      else if (wall == Direction.South)
      {
         return new Rectangle(center.x - (this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf()),
                              center.y + (this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf()),
                              this.csm.getCellWidth() - this.csm.getWallWidth(),
                              this.csm.getWallHeight());
      }
      else if (wall == Direction.East)
      {
         return new Rectangle(center.x + (this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf()),
                              center.y - (this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf()),
                              this.csm.getWallWidth(),
                              this.csm.getCellHeight() - this.csm.getWallHeight());
      }
      else if (wall == Direction.West)
      {
         return new Rectangle(center.x - (this.csm.getCellWidthHalf() + this.csm.getWallWidthHalf()),
                              center.y - (this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf()),
                              this.csm.getWallWidth(),
                              this.csm.getCellHeight() - this.csm.getWallHeight());
      }
      return new Rectangle(); //should never get here.
   }

   /**
    * Get the absolute region of a peg with respect to the given maze cell.
    */
   protected Rectangle getPegRegion(MazeCell cell, PegLocation peg)
   {
      if (peg == PegLocation.TopLeft)
      {
         return new Rectangle( (cell.getX() * this.csm.getCellWidth()) -
                                    this.csm.getWallWidthHalf() -
                                    this.csm.getCellWidth(),
                              (cell.getY() * this.csm.getCellHeight()) -
                                    this.csm.getCellHeight() -
                                    this.csm.getWallHeightHalf(),
                              this.csm.getWallWidth(),
                              this.csm.getWallHeight());
      }
      else if (peg == PegLocation.TopRight)
      {
         return new Rectangle(cell.getX() * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
                              (cell.getY() * this.csm.getCellHeight()) -
                                    this.csm.getCellHeight() -
                                    this.csm.getWallHeightHalf(),
                              this.csm.getWallWidth(),
                              this.csm.getWallHeight());
      }
      else if (peg == PegLocation.BottomRight)
      {
         return new Rectangle(cell.getX() * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
                              cell.getY() * this.csm.getCellHeight() - this.csm.getWallHeightHalf(),
                              this.csm.getWallWidth(),
                              this.csm.getWallHeight());
      }
      else if (peg == PegLocation.BottomLeft)
      {
         return new Rectangle(cell.getXZeroBased() *
                                    this.csm.getCellWidth() -
                                    this.csm.getWallWidthHalf(),
                              cell.getY() * this.csm.getCellHeight() - this.csm.getWallHeightHalf(),
                              this.csm.getWallWidth(),
                              this.csm.getWallHeight());
      }
      return new Rectangle(); //Should never get here.
   }

   /**
    * Converts a mouse pointer position into the maze cell that it is in.
    * @throws Exception If the pointer location is not within the maze.
    */
   protected MazeCell getHostMazeCell(Point pointerLocation) throws Exception
   {
      MazeCell cell = new MazeCell( (pointerLocation.x / this.csm.getCellWidth()) + 1,
                                   (pointerLocation.y / this.csm.getCellHeight()) + 1);
      if (cell.isInRange(this.model.getSize()))
      {
         return cell;
      }
      else
      {
         throw new Exception("The pointer location is outside of the current maze.");
      }
   }

   /**
    * @param cell
    * @param mouseLocation
    * @return
    * @throws java.lang.Exception If the mouse pointer isn't actually on a wall.
    */
   private Direction getWallDirection(MazeCell cell, Point mouseLocation) throws Exception
   {
      for (Direction direction : Direction.values())
      {
         if (getWallLocation(cell, direction).contains(mouseLocation))
         {
            return direction;
         }
      }
      throw new Exception("Not inside a wall.");
   }

   /**
    * Converts a mouse pointer location into an actual maze wall object.
    * @param mouseLocation
    * @return
    * @throws java.lang.Exception If the mouse pointer isn't actually on a wall.
    */
   protected MazeWall getWall(Point mouseLocation) throws Exception
   {
      final MazeCell cell = getHostMazeCell(mouseLocation);
      return model.getWall(cell, getWallDirection(cell, mouseLocation));
   }

   /**
    * Notifies the MazeView that it has been resized.
    * @param e
    */
   @Override
   public void componentResized(ComponentEvent e)
   {
      if (model == null)
         return;
      background = null;
      this.csm.setCellWidth(this.getWidth() / this.model.getSize().width);
      this.csm.setCellHeight(this.getHeight() / this.model.getSize().height);
      this.csm.setWallWidth(this.csm.getCellWidth() / 4);
      this.csm.setWallHeight(this.csm.getCellHeight() / 4);
   }

   @Override
   public void componentMoved(ComponentEvent e)
   {}

   @Override
   public void componentShown(ComponentEvent e)
   {}

   @Override
   public void componentHidden(ComponentEvent e)
   {}

   /**
    * Sets whether this MazeView can modify its underlying MazeModel.
    * @param b true if the MazeModel should be editable, false otherwise
    */
   public void setEditable(boolean b)
   {
      if (b == editable)
         return;

      editable = b;
      if (editable)
      {
         //Create an event handler to handle mouse events.
         mouseAdapter = new MouseAdapter()
         {
            @Override
            public void mouseMoved(MouseEvent e)
            {
               if (model != null)
               {
                  try
                  {
                     active = getHostMazeCell(e.getPoint());
                     repaint();
                  }
                  catch (Exception ex)
                  {}
               }
            } // public void mouseMoved(MouseEvent e)

            @Override
            public void mouseDragged(MouseEvent e)
            {
               if (model != null)
               {
                  try
                  {
                     active = getHostMazeCell(e.getPoint());
                     MazeWall wall = getWall(e.getPoint());
                     if (SwingUtilities.isLeftMouseButton(e))
                        wall.set(true);
                     else if (SwingUtilities.isRightMouseButton(e))
                        wall.set(false);
                     repaint();
                  }
                  catch (Exception ex)
                  {}
               }
            } // public void mouseDragged(MouseEvent e)

            @Override
            public void mousePressed(MouseEvent e)
            {
               if (model != null)
               {
                  try
                  {
                     final MazeWall wall = getWall(e.getPoint());
                     //Flip the status of the wall.
                     wall.set(!wall.isSet());
                     repaint();
                  }
                  catch (Exception ex)
                  {}
               }
            } // public void mousePressed(MouseEvent e)
         };
         this.addMouseListener(mouseAdapter);
         this.addMouseMotionListener(mouseAdapter);
      } // if (editable)
      else
      {
         active = null;
         this.removeMouseListener(mouseAdapter);
         this.removeMouseMotionListener(mouseAdapter);
         repaint();
      } // else
   }

   private void drawFog(Graphics2D g)
   {
      //Draw the box for fog
      g.setComposite(AlphaComposite.SrcOver);
      g.setPaint(this.paints.getFog());
      if (unexplored == null)
      {
         return;
      }
      else
      {
         Object[] foggy = unexplored.toArray();
         for (int i = 0; i < foggy.length; i++)
         {
            MazeCell here = (MazeCell) foggy[i];
            g.fillRect( (here.getX() - 1) * this.csm.getCellWidth(),
                       (here.getY() - 1) * this.csm.getCellHeight(),
                       this.csm.getCellWidth(),
                       this.csm.getCellHeight());
         }
      }
   }

   public void loadUnexplored(Set<MazeCell> set)
   {
      unexplored = set;
   }

   public void setDrawFog(boolean setter)
   {
      drawFog = setter;
   }

   private void drawFirstRun(Graphics2D g)
   {
      if (this.firstRun != null && this.firstRun.isEmpty() == false)
      {
         g.setPaint(this.paints.getRunFirst());

         MazeCell here = firstRun.get(0);
         MazeCell there;
         Point center;
         int width, height;
         for (int i = 1; i < firstRun.size(); i++)
         {
            there = firstRun.get(i);
            if (here.getX() < there.getX())
            {
               //here is west of there
               center = this.getCellCenter(here);
               center.x += this.csm.getCellWidth() / 4;
               width = this.csm.getCellWidth();
               height = this.csm.getCellHeight() / 4;
            }
            else if (here.getX() > there.getX())
            {
               //here is east of there
               center = this.getCellCenter(there);
               center.x -= this.csm.getCellWidth() / 4;
               width = this.csm.getCellWidth();
               height = this.csm.getCellHeight() / 4;
            }
            else if (here.getY() > there.getY())
            {
               //here is south of there
               center = this.getCellCenter(there);
               width = this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight();
            }
            else
            {
               //here is north of there
               center = this.getCellCenter(here);
               width = this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight();
            }
            g.fillRect(center.x, center.y, width, height);
            here = there;
         }
      }
   }

   public void loadFirstRun(List<MazeCell> list)
   {
      firstRun = list;
   }

   public void setDrawFirstRun(boolean setter)
   {
      drawFirstRun = setter;
   }

   private void drawBestRun(Graphics2D g)
   {
      if (this.bestRun != null && this.bestRun.isEmpty() == false)
      {

         g.setPaint(this.paints.getRunBest());

         MazeCell here = bestRun.get(0);
         MazeCell there;
         int x, y;
         int width, height;
         for (int i = 1; i < bestRun.size(); i++)
         {
            there = bestRun.get(i);
            if (here.getX() < there.getX())
            {
               //here is west of there
               x = (here.getX() - 1) * this.csm.getCellWidth() + this.csm.getCellWidth() / 4;
               y = (here.getY() - 1) * this.csm.getCellHeight() + this.csm.getCellHeight() / 4;
               width = 5 * this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight() / 4;
            }
            else if (here.getX() > there.getX())
            {
               //here is east of there
               x = (there.getX() - 1) * this.csm.getCellWidth() + this.csm.getCellWidth() / 4;
               y = (there.getY() - 1) * this.csm.getCellHeight() + this.csm.getCellHeight() / 4;
               width = 5 * this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight() / 4;
            }
            else if (here.getY() > there.getY())
            {
               //here is south of there
               x = (there.getX() - 1) * this.csm.getCellWidth() + this.csm.getCellWidth() / 4;
               y = (there.getY() - 1) * this.csm.getCellHeight() + this.csm.getCellHeight() / 4;
               width = this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight();
            }
            else
            {
               //here is north of there
               x = (here.getX() - 1) * this.csm.getCellWidth() + this.csm.getCellWidth() / 4;
               y = (here.getY() - 1) * this.csm.getCellHeight() + this.csm.getCellHeight() / 4;
               width = this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight();
            }
            g.fillRect(x, y, width, height);
            here = there;
         }
      }
   }

   public void loadBestRun(List<MazeCell> list)
   {
      bestRun = list;
   }

   public void setDrawBestRun(boolean setter)
   {
      drawBestRun = setter;
   }

   private void drawCurrentRun(Graphics2D g)
   {
      if (this.currentRun != null && this.currentRun.isEmpty() == false)
      {
         g.setComposite(AlphaComposite.SrcOver);
         g.setPaint(this.paints.getRunCurrent());

         MazeCell here = currentRun.get(0);
         MazeCell there;
         int x, y;
         int width, height;
         for (int i = 1; i < currentRun.size(); i++)
         {
            there = currentRun.get(i);
            if (here.getX() < there.getX())
            {
               //here is west of there
               x = here.getXZeroBased() * this.csm.getCellWidth() + 5 * this.csm.getCellWidth() / 8;
               y = here.getYZeroBased() * this.csm.getCellHeight() + 3 * this.csm.getCellHeight() / 8;
               //width = 5 * this.csm.getCellWidth() / 4;
               width = this.csm.getCellWidth();
               height = this.csm.getCellHeight() / 4;
            }
            else if (here.getX() > there.getX())
            {
               //here is east of there
               x = (there.getX() - 1) * this.csm.getCellWidth() + 3 * this.csm.getCellWidth() / 8;
               y = (there.getY() - 1) * this.csm.getCellHeight() + 3 * this.csm.getCellHeight() / 8;
               //width = 5 * this.csm.getCellWidth() / 4;
               width = this.csm.getCellWidth();
               height = this.csm.getCellHeight() / 4;
            }
            else if (here.getY() > there.getY())
            {
               //here is south of there
               x = (there.getX() - 1) * this.csm.getCellWidth() + 3 * this.csm.getCellWidth() / 8;
               y = (there.getY() - 1) * this.csm.getCellHeight() + 3 * this.csm.getCellHeight() / 8;
               width = this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight();
            }
            else
            {
               //here is north of there
               x = here.getXZeroBased() * this.csm.getCellWidth() + 3 * this.csm.getCellWidth() / 8;
               y = here.getYZeroBased() * this.csm.getCellHeight() + 5 * this.csm.getCellHeight() / 8;
               width = this.csm.getCellWidth() / 4;
               height = this.csm.getCellHeight();
            }
            g.fillRect(x, y, width, height);
            here = there;
         }
      }
   }

   public void loadCurrentRun(List<MazeCell> list)
   {
      currentRun = list;
   }

   public void setDrawCurrentRun(boolean setter)
   {
      drawCurrentRun = setter;
   }

   private void drawUnderstanding(Graphics2D g)
   {
      MazeCell here;
      int x, y;
      if (understandingInt != null)
      {
         int local;
         for (int i = 1; i <= model.getSize().width; i++)
         {
            for (int j = 1; j <= model.getSize().height; j++)
            {
               here = new MazeCell(i, j);
               if (unexplored.contains(here))
               {
                  g.setColor(Color.WHITE);
               }
               else
               {
                  g.setColor(Color.BLACK);
               }
               x = (i - 1) * this.csm.getCellWidth() + 3 * this.csm.getCellWidth() / 8;
               y = (j - 1) * this.csm.getCellHeight() + this.csm.getCellHeight() / 2;
               local = understandingInt[i - 1][j - 1];
               g.drawString(String.valueOf(local), x, y);
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
               if (unexplored.contains(here))
               {
                  g.setColor(Color.WHITE);
               }
               else
               {
                  g.setColor(Color.BLACK);
               }
               x = (i - 1) * this.csm.getCellWidth() + this.csm.getCellWidth() / 2;
               y = (j - 1) * this.csm.getCellHeight() + this.csm.getCellHeight() / 2;
               local = understandingDir[i - 1][j - 1];
               drawArrow(g, local, x, y);
            }
         }
      }
   }

   private void drawArrow(Graphics2D g, Direction local, int x, int y)
   {
      //Draws an arrow in the direction of "local" centered on the point (x,y)
      if (local.equals(Direction.North))
      {
         int[] ys =
         {
            y + this.csm.getCellHeight() * 3 / 8, y, y, y - this.csm.getCellHeight() * 3 / 8, y, y
         };
         int[] xs =
         {
            x, x - this.csm.getCellWidth() / 8, x - this.csm.getCellWidth() / 4, x,
            x + this.csm.getCellWidth() / 4, x + this.csm.getCellWidth() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
      if (local.equals(Direction.South))
      {
         int[] ys =
         {
            y - this.csm.getCellHeight() * 3 / 8, y, y, y + this.csm.getCellHeight() * 3 / 8, y, y
         };
         int[] xs =
         {
            x, x - this.csm.getCellWidth() / 8, x - this.csm.getCellWidth() / 4, x,
            x + this.csm.getCellWidth() / 4, x + this.csm.getCellWidth() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
      if (local.equals(Direction.West))
      {
         int[] xs =
         {
            x + this.csm.getCellWidth() * 3 / 8, x, x, x - this.csm.getCellWidth() * 3 / 8, x, x
         };
         int[] ys =
         {
            y, y - this.csm.getCellHeight() / 8, y - this.csm.getCellHeight() / 4, y,
            y + this.csm.getCellHeight() / 4, y + this.csm.getCellHeight() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
      if (local.equals(Direction.East))
      {
         int[] xs =
         {
            x - this.csm.getCellWidth() * 3 / 8, x, x, x + this.csm.getCellWidth() * 3 / 8, x, x
         };
         int[] ys =
         {
            y, y - this.csm.getCellHeight() / 8, y - this.csm.getCellHeight() / 4, y,
            y + this.csm.getCellHeight() / 4, y + this.csm.getCellHeight() / 8
         };
         g.drawPolygon(xs, ys, 6);
      }
   }

   public void setDrawUnderstanding(boolean setter)
   {
      drawUnderstanding = setter;
   }

   public void loadUnderstanding(int[][] understandingInt)
   {
      this.understandingInt = understandingInt;
   }

   public void loadUnderstandingDir(Direction[][] understandingDir)
   {
      this.understandingDir = understandingDir;
   }
}
