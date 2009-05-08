/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import maze.gui.CellSize;
import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel.MazeWall;
/**
 *
 * @author desolc
 */
public class EditableMazeView extends maze.gui.MazeView
{
   private MazeTemplate mCurrentTemplate = null;
   public EditableMazeView()
   {
      super();
   }

   public CellSize getCellSize()
   {
      return new CellSize(csm.getCellWidth()-csm.getWallWidth(),
                          csm.getCellHeight()-csm.getWallHeight(),
                          csm.getWallWidth(), csm.getWallHeight());
   }

   @Override
   protected void paintComponent(Graphics arg)
   {
      super.paintComponent(arg);
      Graphics2D g2 = (Graphics2D)arg;
      if (mCurrentTemplate != null)
         mCurrentTemplate.draw((Graphics2D)arg, getCellSize());
   }

   public void setTemplate(MazeTemplate mt)
   {
      mCurrentTemplate = mt;
   }

   public void applyTemplate(boolean setWall)
   {
      TemplatePeg tp = mCurrentTemplate.getCenterPeg();
      Point cp = mCurrentTemplate.getCenterPoint();
      MazeCell hostCell;
      try
      {
         hostCell = this.getHostMazeCell(cp);
      }
      catch (Exception ex){return;}

      int[] coords = {hostCell.getXZeroBased(), hostCell.getYZeroBased()};

      int leftX = coords[0]*csm.getCellWidth();
      int leftY = coords[1]*csm.getCellHeight();

      if (cp.x > leftX + csm.getCellWidthHalf()/2 &&
          cp.x <= leftX + csm.getCellWidthHalf())
         return;
      if (cp.y > leftY + csm.getCellHeightHalf()/2 &&
          cp.y <= leftY + csm.getCellHeightHalf())
         return;

      if (coords[0] != 0)
      {
         int halfX = leftX + csm.getCellWidthHalf();
         if (cp.x < halfX)
            coords[0]--;
      }

      if (coords[1] != 0)
      {
         int halfY = leftY + csm.getCellHeightHalf();
         if (cp.y < halfY)
            coords[1]--;
      }

      TreeSet<TemplatePeg> visited = new TreeSet<TemplatePeg>(new PegCompare());
      Vector<MazeWall> walls = new Vector<MazeWall>();
      applyPeg(tp, visited, walls, coords);
      for (MazeWall ms : walls)
         ms.set(setWall);
   }

   private void applyPeg(TemplatePeg peg, TreeSet<TemplatePeg> visited,
                         Vector<MazeWall> walls, int[] coords)
   {
      if (visited.contains(peg))
         return;

      visited.add(peg);

      if (peg.mTop != null)
      {
         if (coords[0] >= 0 && coords[0] < model.getSize().width &&
             coords[1] >= 0 && coords[1] < model.getSize().height)
            walls.add(model.getWall(new MazeCell(coords[0]+1, coords[1]+1),
                                    Direction.East));
         
         int[] newCoords = {coords[0], coords[1]-1};
         applyPeg(peg.mTop.mRightTop, visited, walls, newCoords);
      }
      if (peg.mLeft != null)
      {
         
         if (coords[0] >= 0 && coords[0] < model.getSize().width &&
             coords[1] >= 0 && coords[1] < model.getSize().height)
            walls.add(model.getWall(new MazeCell(coords[0]+1, coords[1]+1),
                                    Direction.South));

         int[] newCoords = {coords[0]-1, coords[1]};
         //MazeCell newHost = new MazeCell(coords[0], coords[1]+1);
         applyPeg(peg.mLeft.mLeftBottom, visited, walls, newCoords);
      }
      if (peg.mRight != null)
      {
         int[] newCoords = {coords[0]+1, coords[1]};
         //MazeCell newHost = new MazeCell(coords[0]+2, coords[1]+1);
         applyPeg(peg.mRight.mRightTop, visited, walls, newCoords);
      }
      if (peg.mBottom != null)
      {
         int[] newCoords = {coords[0], coords[1]+1};
         //MazeCell newHost = new MazeCell(coords[0]+1, coords[1]+2);
         applyPeg(peg.mBottom.mLeftBottom, visited, walls, newCoords);
      }

   }

   class PegCompare implements Comparator<TemplatePeg>
   {
      @Override
      public int compare(TemplatePeg o1, TemplatePeg o2)
      {
         if (o1.mLeft == o2.mLeft && o1.mRight == o2.mRight &&
             o1.mBottom == o2.mBottom && o1.mTop == o2.mTop)
            return 0;
         else if (o1.mLeft != null)
         {
            if (o2.mLeft != null)
               return o1.mLeft.hashCode()-o2.mLeft.hashCode();
            return 1;
         }
         else if (o2.mLeft != null)
            return -1;
         else if (o1.mRight != null)
         {
            if (o2.mRight != null)
               return o1.mRight.hashCode()-o2.mRight.hashCode();
            return 1;
         }
         else if (o2.mRight != null)
            return -1;
         else if (o1.mBottom != null)
         {
            if (o2.mBottom != null)
               return o1.mBottom.hashCode()-o2.mBottom.hashCode();
            return 1;
         }
         else if (o2.mBottom != null)
            return -1;
         else if (o1.mTop != null)
         {
            if (o2.mTop != null)
               return o1.mTop.hashCode()-o2.mTop.hashCode();
            return 1;
         }
         else if (o2.mTop != null)
            return -1;
         else
            return 0;
      }
   }
}
