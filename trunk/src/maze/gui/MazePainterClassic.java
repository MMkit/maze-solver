package maze.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import maze.model.CellSizeModelInterface;

public class MazePainterClassic extends MazePainter
{

   public MazePainterClassic(CellSizeModelInterface cellSizeModel)
   {
      super(cellSizeModel);
   }

   @Override
   public void setDefaults()
   {
      super.setDefaults();
      this.wallSet = new Color(220, 0, 0); // Dark red.
      this.background = Color.WHITE;
      this.peg = Color.GREEN;
      this.peg = new Color(112, 255, 148); // Greenish.
      this.pegValid = Color.GREEN;
      this.pegInvalid = new GradientPaint(0, 0, Color.RED, 2, 2, new Color(240, 0, 0), true);
   }

   /**
    * Ignore drawing fog.
    */
   @Override
   public void drawFog(Graphics2D g, Rectangle area)
   {}
}
