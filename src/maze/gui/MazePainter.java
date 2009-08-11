package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.EnumSet;

import javax.swing.ImageIcon;

import maze.Main;
import maze.model.Direction;

/**
 * This is the base class for the UI rendering delegate classes for painting the
 * maze view. This base class provides a set of paints and some simple drawing
 * methods. Extending classes can set new paints or it can override drawing
 * methods for more control.
 * @author Luke Last
 */
public abstract class MazePainter implements Serializable
{
   protected Paint background;
   protected Paint cellBackground;
   protected Paint fog;
   protected Paint hover;
   protected int mazeHeight;
   protected int mazeWidth;
   protected Paint peg;
   protected Paint pegInvalid;
   protected Paint pegValid;
   protected ImageIcon robotImage;
   protected Paint runBest;
   protected Paint runCurrent;
   protected Paint runFirst;
   protected Paint wallEmpty;
   protected Paint wallSet;

   /**
    * Constructor.
    */
   public MazePainter()
   {
      this.setDefaults();
   }

   protected void drawRectangle(Graphics2D g, Paint paint, Rectangle area)
   {
      g.setPaint(paint);
      g.fillRect(area.x, area.y, area.width, area.height);
   }

   public void drawCellBackground(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.cellBackground, area);
   }

   public void drawCellHover(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.hover, area);
   }

   public void drawFog(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.fog, area);
   }

   public void drawPeg(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.peg, area);
   }

   public void drawPegInvalid(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.pegInvalid, area);
   }

   public void drawPegValid(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.pegValid, area);
   }

   public void drawRobot(Graphics2D g, Point location, double rotation, int cellWidth,
         int cellHeight)
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
      final int size = Math.min(cellWidth, cellHeight) / 2;
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

   public void drawRunCurrent(Graphics2D g, Rectangle cellArea, EnumSet<Direction> dirsTraveled)
   {
      g.setPaint(this.runCurrent);
      final int size = Math.min(cellArea.width, cellArea.height) / 5;
      final int x = cellArea.x + cellArea.width / 2 - size / 2;
      final int y = cellArea.y + cellArea.height / 2 - size / 2;
      g.fillOval(x, y, size, size);
      for (final Direction direction : dirsTraveled)
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

   public void drawWallEmpty(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.wallEmpty, area);
   }

   public void drawWallSet(Graphics2D g, Rectangle area)
   {
      this.drawRectangle(g, this.wallSet, area);
   }

   /**
    * @return the background
    */
   public Paint getBackground()
   {
      return background;
   }

   /**
    * @return the cellBackground
    */
   public Paint getCellBackground()
   {
      return cellBackground;
   }

   /**
    * @return the fog
    */
   public Paint getFog()
   {
      return fog;
   }

   /**
    * @return the hover
    */
   public Paint getHover()
   {
      return hover;
   }

   /**
    * @return the peg
    */
   public Paint getPeg()
   {
      return peg;
   }

   /**
    * @return the pegInvalid
    */
   public Paint getPegInvalid()
   {
      return pegInvalid;
   }

   /**
    * @return the pegValid
    */
   public Paint getPegValid()
   {
      return pegValid;
   }

   /**
    * @return the robotImage
    */
   public ImageIcon getRobotImage()
   {
      return robotImage;
   }

   /**
    * @return the runBest
    */
   public Paint getRunBest()
   {
      return runBest;
   }

   /**
    * @return the runCurrent
    */
   public Paint getRunCurrent()
   {
      return runCurrent;
   }

   /**
    * @return the runFirst
    */
   public Paint getRunFirst()
   {
      return runFirst;
   }

   /**
    * @return the wallEmpty
    */
   public Paint getWallEmpty()
   {
      return wallEmpty;
   }

   /**
    * @return the wallSet
    */
   public Paint getWallSet()
   {
      return wallSet;
   }

   /**
    * @param background the background to set
    */
   public void setBackground(Paint background)
   {
      this.background = background;
   }

   /**
    * @param cellBackground the cellBackground to set
    */
   public void setCellBackground(Paint cellBackground)
   {
      this.cellBackground = cellBackground;
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
    * @param fog the fog to set
    */
   public void setFog(Paint fog)
   {
      this.fog = fog;
   }

   /**
    * @param hover the hover to set
    */
   public void setHover(Paint hover)
   {
      this.hover = hover;
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
    * @param peg the peg to set
    */
   public void setPeg(Paint peg)
   {
      this.peg = peg;
   }

   /**
    * @param pegInvalid the pegInvalid to set
    */
   public void setPegInvalid(Paint pegInvalid)
   {
      this.pegInvalid = pegInvalid;
   }

   /**
    * @param pegValid the pegValid to set
    */
   public void setPegValid(Paint pegValid)
   {
      this.pegValid = pegValid;
   }

   /**
    * @param robotImage the robotImage to set
    */
   public void setRobotImage(ImageIcon robotImage)
   {
      this.robotImage = robotImage;
   }

   /**
    * @param runBest the runBest to set
    */
   public void setRunBest(Paint runBest)
   {
      this.runBest = runBest;
   }

   /**
    * @param runCurrent the runCurrent to set
    */
   public void setRunCurrent(Paint runCurrent)
   {
      this.runCurrent = runCurrent;
   }

   /**
    * @param runFirst the runFirst to set
    */
   public void setRunFirst(Paint runFirst)
   {
      this.runFirst = runFirst;
   }

   /**
    * @param wallEmpty the wallEmpty to set
    */
   public void setWallEmpty(Paint wallEmpty)
   {
      this.wallEmpty = wallEmpty;
   }

   /**
    * @param wallSet the wallSet to set
    */
   public void setWallSet(Paint wallSet)
   {
      this.wallSet = wallSet;
   }
}