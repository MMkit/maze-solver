package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;

import javax.swing.ImageIcon;

import maze.Main;

/**
 * Holds all the paints and colors and images used to draw the maze view.
 * @author Luke Last
 */
public class MazePaints
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

   public MazePaints()
   {
      this.setDefaults();
   }

   /**
    * Set the default values for colors. You may also want to call
    * <code>setGradients(...)</code> to completely initialize all paints.
    */
   public void setDefaults()
   {
      this.cellBackground = Color.BLACK;
      this.wallSet = Color.BLACK;
      this.wallEmpty = Color.BLACK;
      this.background = Color.WHITE;
      this.peg = Color.BLACK;
      this.pegValid = Color.BLACK;
      this.pegInvalid = new GradientPaint(0, 0, Color.RED, 2, 2, new Color(240, 0, 0), true);
      this.fog = new Color(0, 0, 0, 75);
      this.runFirst = new Color(0, 0, 255, 75);
      this.runBest = new Color(255, 0, 0, 75);
      this.runCurrent = new Color(0, 255, 0, 50);
      this.hover = new Color(255, 255, 0, 100);
      this.robotImage = Main.getImageResource("gui/images/mouse.png");
   }

   /**
    * Sets the default gradient paints that rely on the maze size.
    * @param mazeSize The size of the maze in pixels.
    */
   public void setGradients(Dimension mazeSize)
   {
      final int halfX = mazeSize.width / 2;
      final int halfY = mazeSize.height / 2;
      final Point zero = new Point(0, 0);
      final Point middle = new Point(halfX, halfY);
      this.background = Color.WHITE;

      this.cellBackground = new GradientPaint(zero, Color.WHITE, middle, new Color(229, 236, 255), true);
      this.wallEmpty = new GradientPaint(zero, //Gradient start corner.
                                         new Color(229, 236, 255), //Really light blue.
                                         middle,
                                         new Color(204, 218, 255), //Light blue.
                                         true);
      this.wallSet = new GradientPaint(zero, new Color(0, 94, 189), middle, new Color(0, 56, 112), true);
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
   
   
   public static class MazePaintsClassic extends MazePaints
   {
      @Override
      public void setDefaults()
      {
         this.cellBackground = Color.BLACK;
         this.wallSet = Color.RED;
         this.wallEmpty = Color.BLACK;
         this.background = Color.WHITE;
         this.peg = Color.GREEN;
         this.pegValid = Color.GREEN;
         this.pegInvalid = new GradientPaint(0, 0, Color.RED, 2, 2, new Color(240, 0, 0), true);
         this.fog = new Color(0, 0, 0, 75);
         this.runFirst = new Color(0, 0, 255, 75);
         this.runBest = new Color(255, 0, 0, 75);
         this.runCurrent = new Color(0, 255, 0, 50);
         this.hover = new Color(255, 255, 0, 100);
         this.robotImage = Main.getImageResource("gui/images/mouse.png");
      }
      
      @Override
      public void setGradients(Dimension arg0)
      {
      }
   }

}
