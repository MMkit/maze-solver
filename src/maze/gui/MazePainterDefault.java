package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Point;

import maze.Main;
import maze.model.CellSizeModelInterface;

public class MazePainterDefault extends MazePainter
{

   public MazePainterDefault(CellSizeModelInterface cellSizeModel)
   {
      super(cellSizeModel);
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
    * When the maze size changes we want to recalculate our gradients.
    * {@inheritDoc}
    */
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
