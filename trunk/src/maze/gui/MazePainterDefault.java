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
   private final ImageIcon pegImage = maze.Main.getImageResource("gui/images/peg.png");

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
      g.setColor(new Color(0,0,0,185));
      g.fill(area);
   }

   /**
    * Set the default values for colors. You may also want to call
    * <code>setGradients(...)</code> to completely initialize all paints.
    */
   @Override
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
                                                 Color.WHITE,
                                                 middle,
                                                 new Color(229, 236, 255),
                                                 true);
         this.wallEmpty = new GradientPaint(zero, //Gradient start corner.
                                            new Color(229, 236, 255), //Really light blue.
                                            middle,
                                            new Color(204, 218, 255), //Light blue.
                                            true);
         this.wallSet = new GradientPaint(zero,
                                          new Color(0, 94, 189),
                                          middle,
                                          new Color(0, 56, 112),
                                          true);
      }
   }
}
