/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Point;
import maze.gui.CellSize;

/**
 *
 * @author desolc
 */
public abstract class ConjoinedMazeTemplate extends MazeTemplate
{
   protected TemplatePeg mCenter = null;
   protected Point mCenterPoint = new Point(0,0);
   
   @Override
   public TemplatePeg[] getCenterPegs()
   {
      return new TemplatePeg[]{mCenter};
   }

   @Override
   public Point[] getCenterPoints(CellSize size)
   {
      return new Point[]{mCenterPoint};
   }

   @Override
   public void updatePosition(Point p, CellSize size)
   {
      mCenterPoint = (Point)p.clone();
   }

}
