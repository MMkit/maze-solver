package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.EnumSet;

import javax.swing.ImageIcon;

import maze.Main;
import maze.model.CellSizeModelInterface;
import maze.model.Direction;

/**
 * This is the base class for the UI rendering delegate classes for painting the
 * maze view. This base class provides a set of paints and some simple drawing
 * methods. Extending classes can set new paints or it can override drawing
 * methods for more control.
 * @author Luke Last
 */
public abstract class MazePainter
{
   protected Paint cellBackground;
   protected Paint wallSet;
   protected Paint wallEmpty;
   protected Paint background;
   protected Paint peg;
   protected Paint pegValid;
   protected Paint pegInvalid;
   protected Paint fog;
   protected Paint runFirst;
   protected Paint runBest;
   protected Paint runCurrent;
   protected Paint hover;
   protected ImageIcon robotImage;
   protected final CellSizeModelInterface csm;
   protected int mazeWidth;
   protected int mazeHeight;

   public void drawCellBackground(Graphics2D g, Rectangle area)
   {
      g.setPaint(this.cellBackground);
      g.fill(area);
   }

   public void drawCellHover(Graphics2D g, Rectangle area)
   {
      g.setPaint(this.hover);
      g.fill(area);
   }

   public void drawWallSet(Graphics2D g, Rectangle area)
   {
      g.setPaint(this.wallSet);
      g.fill(area);
   }

   public void drawWallEmpty(Graphics2D g, Rectangle area)
   {
      g.setPaint(this.wallEmpty);
      g.fill(area);
   }

   public void drawPeg(Graphics2D g, Rectangle area)
   {
      g.setPaint(this.peg);
      g.fill(area);
   }

   public void drawFog(Graphics2D g, Rectangle area)
   {
      g.setPaint(this.fog);
      g.fill(area);
   }

   public void drawRunCurrent(Graphics2D g, Rectangle cellArea, EnumSet<Direction> dirsTraveled)
   {
      g.setPaint(this.runCurrent);
      final int size = Math.min(cellArea.width, cellArea.height) / 5;
      final int x = cellArea.x + cellArea.width / 2 - size / 2;
      final int y = cellArea.y + cellArea.height / 2 - size / 2;
      g.fillOval(x, y, size, size);
      for (Direction direction : dirsTraveled)
      {
         switch (direction)
         {
            case North :
               g.fillOval(x, cellArea.y, size, size);
               break;
            case East :
               g.fillOval(cellArea.x + cellArea.width - size, y, size, size);
               break;
            case South :
               g.fillOval(x, cellArea.y + cellArea.height - size, size, size);
               break;
            case West :
               g.fillOval(cellArea.x, y, size, size);
               break;
         }
      }
   }

   public void drawRobot(Graphics2D g, Point location, double rotation)
   {
      //We have to translate our coordinate system to make the image rotation easier.
      g.translate(location.x, location.y);

      //Back up the current graphics state.
      final AffineTransform oldTransform = g.getTransform();
      //We add half a PI to the rotation because the top of the image is forward.
      final AffineTransform transform = AffineTransform.getRotateInstance(rotation + Math.PI / 2);
      //Set the image rotation transformation.
      g.transform(transform);
      //Get the size of half the final image. Based on smallest cell dimension.
      final int size = Math.min(this.csm.getCellWidth() - this.csm.getWallWidth(),
                                this.csm.getCellHeight() - this.csm.getWallHeight()) / 2;
      //Draw the image to scale. The point 0,0 is the center of the image.
      g.drawImage(this.robotImage.getImage(),
                  -size,
                  -size,
                  size,
                  size,
                  0,
                  0,
                  this.robotImage.getIconWidth(),
                  this.robotImage.getIconHeight(),
                  null);
      //Restore the original graphics state.
      g.setTransform(oldTransform);
      g.translate(-location.x, -location.y);
   }

   /**
    * Constructor.
    * @param cellSizeModel The cell size model from the view this class is
    *           rendering for.
    */
   public MazePainter(CellSizeModelInterface cellSizeModel)
   {
      if (cellSizeModel == null)
         throw new IllegalArgumentException("The cell size model cannot be null");

      this.csm = cellSizeModel;
      this.setDefaults();
   }

   /**
    * Set the maze model dimensions so that this renderer knows how big the maze
    * is and can calculate locations.
    * @param size The size of the maze in pixels.
    */
   public void setMazeSize(Dimension size)
   {
      if (size != null)
      {
         this.mazeWidth = size.width;
         this.mazeHeight = size.height;
      }
   }

   /**
    * Set the default values for colors. The base class defaults are all black.
    */
   public void setDefaults()
   {
      this.cellBackground = Color.BLACK;
      this.wallSet = Color.BLACK;
      this.wallEmpty = Color.BLACK;
      this.background = Color.WHITE;
      this.peg = Color.BLACK;
      this.pegValid = Color.BLACK;
      this.pegInvalid = Color.BLACK;
      this.fog = Color.BLACK;
      this.runFirst = Color.BLACK;
      this.runBest = Color.BLACK;
      this.runCurrent = Color.BLACK;
      this.hover = Color.BLACK;
      this.robotImage = Main.getImageResource("gui/images/mouse.png");
   }

   /**
    * @return the cellBackground
    */
   public Paint getCellBackground()
   {
      return cellBackground;
   }

   /**
    * @param cellBackground the cellBackground to set
    */
   public void setCellBackground(Paint cellBackground)
   {
      this.cellBackground = cellBackground;
   }

   /**
    * @return the wallSet
    */
   public Paint getWallSet()
   {
      return wallSet;
   }

   /**
    * @param wallSet the wallSet to set
    */
   public void setWallSet(Paint wallSet)
   {
      this.wallSet = wallSet;
   }

   /**
    * @return the wallEmpty
    */
   public Paint getWallEmpty()
   {
      return wallEmpty;
   }

   /**
    * @param wallEmpty the wallEmpty to set
    */
   public void setWallEmpty(Paint wallEmpty)
   {
      this.wallEmpty = wallEmpty;
   }

   /**
    * @return the background
    */
   public Paint getBackground()
   {
      return background;
   }

   /**
    * @param background the background to set
    */
   public void setBackground(Paint background)
   {
      this.background = background;
   }

   /**
    * @return the peg
    */
   public Paint getPeg()
   {
      return peg;
   }

   /**
    * @param peg the peg to set
    */
   public void setPeg(Paint peg)
   {
      this.peg = peg;
   }

   /**
    * @return the pegValid
    */
   public Paint getPegValid()
   {
      return pegValid;
   }

   /**
    * @param pegValid the pegValid to set
    */
   public void setPegValid(Paint pegValid)
   {
      this.pegValid = pegValid;
   }

   /**
    * @return the pegInvalid
    */
   public Paint getPegInvalid()
   {
      return pegInvalid;
   }

   /**
    * @param pegInvalid the pegInvalid to set
    */
   public void setPegInvalid(Paint pegInvalid)
   {
      this.pegInvalid = pegInvalid;
   }

   /**
    * @return the fog
    */
   public Paint getFog()
   {
      return fog;
   }

   /**
    * @param fog the fog to set
    */
   public void setFog(Paint fog)
   {
      this.fog = fog;
   }

   /**
    * @return the runFirst
    */
   public Paint getRunFirst()
   {
      return runFirst;
   }

   /**
    * @param runFirst the runFirst to set
    */
   public void setRunFirst(Paint runFirst)
   {
      this.runFirst = runFirst;
   }

   /**
    * @return the runBest
    */
   public Paint getRunBest()
   {
      return runBest;
   }

   /**
    * @param runBest the runBest to set
    */
   public void setRunBest(Paint runBest)
   {
      this.runBest = runBest;
   }

   /**
    * @return the runCurrent
    */
   public Paint getRunCurrent()
   {
      return runCurrent;
   }

   /**
    * @param runCurrent the runCurrent to set
    */
   public void setRunCurrent(Paint runCurrent)
   {
      this.runCurrent = runCurrent;
   }

   /**
    * @return the robotImage
    */
   public ImageIcon getRobotImage()
   {
      return robotImage;
   }

   /**
    * @param robotImage the robotImage to set
    */
   public void setRobotImage(ImageIcon robotImage)
   {
      this.robotImage = robotImage;
   }

   /**
    * @return the hover
    */
   public Paint getHover()
   {
      return hover;
   }

   /**
    * @param hover the hover to set
    */
   public void setHover(Paint hover)
   {
      this.hover = hover;
   }
}
