package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * Provides a default theme for the maze.
 * @author Luke Last
 */
public final class MazePainterDefault extends MazePainter
{
   private Color cellBackgroundEnd;
   private Color cellBackgroundStart;
   private final ImageIcon pegImage = maze.Main.getImageResource("gui/images/peg.png");

   private Color wallEmptyEnd;
   private Color wallEmptyStart;

   private Color wallSetEnd;
   private Color wallSetStart;

   /**
    * Draw a special image for the peg.
    */
   @Override
   public void drawPeg(Graphics2D g, Rectangle area)
   {
      g.drawImage(this.pegImage.getImage(),
                  area.x,
                  area.y,
                  area.x + area.width,
                  area.y + area.height,
                  0,
                  0,
                  this.pegImage.getIconWidth(),
                  this.pegImage.getIconHeight(),
                  null);
      g.setPaint(this.peg);
      g.fill(area);
   }

   public Color getCellBackgroundEnd()
   {
      return cellBackgroundEnd;
   }

   public Color getCellBackgroundStart()
   {
      return cellBackgroundStart;
   }

   public Color getWallEmptyEnd()
   {
      return wallEmptyEnd;
   }

   public Color getWallEmptyStart()
   {
      return wallEmptyStart;
   }

   public Color getWallSetEnd()
   {
      return wallSetEnd;
   }

   public Color getWallSetStart()
   {
      return wallSetStart;
   }

   public void setCellBackgroundEnd(Color cellBackgroundEnd)
   {
      this.cellBackgroundEnd = cellBackgroundEnd;
   }

   public void setCellBackgroundStart(Color cellBackgroundStart)
   {
      this.cellBackgroundStart = cellBackgroundStart;
   }

   /**
    * Set the default values for colors. You may also want to call
    * <code>setGradients(...)</code> to completely initialize all paints.
    */
   @Override
   public void setDefaults()
   {
      this.cellBackground = Color.WHITE;
      this.cellBackgroundStart = Color.WHITE;
      this.cellBackgroundEnd = new Color(229, 236, 255); // Really light blue.

      this.wallSet = Color.BLACK;
      this.wallSetStart = new Color(0, 94, 189);
      this.wallSetEnd = new Color(0, 56, 112);

      this.wallEmpty = Color.BLACK;
      this.wallEmptyStart = this.cellBackgroundEnd;
      this.wallEmptyEnd = new Color(204, 218, 255); // Light blue.

      this.background = Color.WHITE;
      this.peg = new Color(0, 0, 0, 185); // Slightly transparent black.
      this.pegValid = Color.GREEN;
      this.pegInvalid = new GradientPaint(0, 0, Color.RED, 2, 2, new Color(240, 0, 0), true);
      this.fog = new Color(0, 0, 0, 75);
      this.runFirst = new Color(0, 0, 255, 75);
      this.runBest = new Color(255, 0, 0, 75);
      this.runCurrent = new Color(0, 255, 0, 50);
      this.hover = new Color(255, 255, 0, 100);
      this.robotImage = maze.Main.getImageResource("gui/images/mouse.png");
   }

   /**
    * When the maze size changes we want to recalculate our gradients.
    * {@inheritDoc}
    */
   @Override
   public void setMazeSize(Dimension size)
   {
      if (this.mazeWidth != size.width || this.mazeHeight != size.height)
      {
         this.mazeWidth = size.width;
         this.mazeHeight = size.height;
         final int halfX = this.mazeWidth / 2;
         final int halfY = this.mazeHeight / 2;
         final Point zero = new Point(0, 0);
         final Point middle = new Point(halfX, halfY);
         this.cellBackground = new GradientPaint(zero,
                                                 this.cellBackgroundStart,
                                                 middle,
                                                 this.cellBackgroundEnd,
                                                 true);
         this.wallEmpty = new GradientPaint(zero, //Gradient start corner.
                                            this.wallEmptyStart,
                                            middle,
                                            this.wallEmptyEnd,
                                            true);
         this.wallSet = new GradientPaint(zero, this.wallSetStart, middle, this.wallSetEnd, true);
      }
   }

   public void setWallEmptyEnd(Color wallEmptyEnd)
   {
      this.wallEmptyEnd = wallEmptyEnd;
   }

   public void setWallEmptyStart(Color wallEmptyStart)
   {
      this.wallEmptyStart = wallEmptyStart;
   }

   public void setWallSetEnd(Color wallSetEnd)
   {
      this.wallSetEnd = wallSetEnd;
   }

   public void setWallSetStart(Color wallSetStart)
   {
      this.wallSetStart = wallSetStart;
   }
}
