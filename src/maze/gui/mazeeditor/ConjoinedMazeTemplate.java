package maze.gui.mazeeditor;

import java.awt.Point;
import maze.gui.CellSize;

/**
 * This class provides a parent class to any maze template that is one piece.
 * This means that a peg can be reached by any other peg just by traversing
 * the walls and pegs in the template.
 * @author Johnathan Smith
 */
public abstract class ConjoinedMazeTemplate extends MazeTemplate
{
   protected TemplatePeg mCenter = null;
   protected Point mCenterPoint = new Point(0,0);

   /**
    * Returns an array consisting of one element, the center peg.
    * @return an array consisting of one element, the center peg
    */
   @Override
   public TemplatePeg[] getCenterPegs()
   {
      return new TemplatePeg[]{mCenter};
   }

   /**
    * Returns an array consisting of one element, the center peg position.  This
    * position represents where the center peg should be positioned on the
    * screen.
    * @param size the dimensions of the maze being displayed
    * @return an array consisting of one element, the center peg position
    */
   @Override
   public Point[] getCenterPoints(CellSize size)
   {
      return new Point[]{mCenterPoint};
   }

   /**
    * Updates the center point of the center peg.
    * @param p the new center position for the center peg
    * @param size the dimensions of the maze being displayed
    */
   @Override
   public void updatePosition(Point p, CellSize size)
   {
      mCenterPoint = (Point)p.clone();
   }

}
