/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
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
   private static final int WALL_SIZE_DIVIDER = 3;
   private MazeTemplate mCurrentTemplate = null;

   public CellSize getCellSize()
   {
      return new CellSize(csm.getCellWidth()-csm.getWallWidth(),
                          csm.getCellHeight()-csm.getWallHeight(),
                          csm.getWallWidth(), csm.getWallHeight(),
                          csm.getCellWidth()*model.getSize().width,
                          csm.getCellHeight()*model.getSize().height);
   }
   
   @Override
   public void componentResized(ComponentEvent e)
   {
      super.componentResized(e);
      //We override the standard wall size here so it is easier to click on.
      final int wallSize = Math.min(csm.getCellWidth(), csm.getCellHeight()) / WALL_SIZE_DIVIDER;
      this.csm.setWallWidth(wallSize);
      this.csm.setWallHeight(wallSize);
   }

   @Override
   protected void paintComponent(Graphics arg)
   {
      super.paintComponent(arg);
      Graphics2D g2 = (Graphics2D)arg;
      if (model != null)
      {
         g2.setPaint(super.paints.getPegInvalid());
         int cx, cy;
         for (Point p : model.illegalPegs())
         {
            cx = p.x*csm.getCellWidth()-csm.getWallWidthHalf();
            cy = p.y*csm.getCellHeight()-csm.getWallHeightHalf();
            g2.fillRect(cx, cy, csm.getWallWidth(), csm.getWallHeight());
         }
         if (model.isCenterLegal())
         {
            g2.setColor(Color.GREEN);
            Dimension size = model.getSize();
            cx = size.width/2*csm.getCellWidth()-csm.getWallWidthHalf();
            cy = size.height/2*csm.getCellHeight()-csm.getWallHeightHalf();
            g2.fillRect(cx, cy, csm.getWallWidth(), csm.getWallHeight());
         }

         if (mCurrentTemplate != null)
            mCurrentTemplate.draw((Graphics2D)arg, getCellSize());
      }
   }

   public void setTemplate(MazeTemplate mt)
   {
      mCurrentTemplate = mt;
   }

   public void applyTemplate(boolean setWall)
   {
      if (model == null || mCurrentTemplate == null)
         return;
      TemplatePeg[] tp = mCurrentTemplate.getCenterPegs();
      Point[] cp = mCurrentTemplate.getCenterPoints(getCellSize());
      Vector<MazeWall> walls = new Vector<MazeWall>();
      TreeSet<TemplatePeg> applied = new TreeSet<TemplatePeg>();
      for (int i = 0; i < Math.min(tp.length, cp.length); i++)
      {
         if (applied.contains(tp[i]))
            continue;

         MazeCell hostCell;
         try
         {
            hostCell = this.getHostMazeCell(cp[i]);
         }
         catch (Exception ex)
         {
            continue;
         }

         int[] coords = {hostCell.getXZeroBased(), hostCell.getYZeroBased()};

         int leftX = coords[0]*csm.getCellWidth();
         int leftY = coords[1]*csm.getCellHeight();

         if (cp[i].x > leftX + csm.getCellWidthHalf()/2 &&
             cp[i].x <= leftX + csm.getCellWidthHalf())
            continue;
         if (cp[i].y > leftY + csm.getCellHeightHalf()/2 &&
             cp[i].y <= leftY + csm.getCellHeightHalf())
            continue;

         int halfX = leftX + csm.getCellWidthHalf();
         if (cp[i].x < halfX)
            coords[0]--;

         int halfY = leftY + csm.getCellHeightHalf();
         if (cp[i].y < halfY)
            coords[1]--;

         TreeSet<TemplatePeg> visited = new TreeSet<TemplatePeg>();

         applyPeg(tp[i], visited, walls, coords);
         applied.addAll(visited);
      }
      for (MazeWall ms : walls)
            ms.set(setWall);
   }

   private void applyPeg(TemplatePeg peg, TreeSet<TemplatePeg> visited,
                         Vector<MazeWall> walls, int[] coords)
   {
      if (visited.contains(peg))
         return;

      visited.add(peg);

      if (peg.top != null)
      {
         if (coords[0] >= 0 && coords[0] < model.getSize().width &&
             coords[1] >= 0 && coords[1] < model.getSize().height)
            walls.add(model.getWall(new MazeCell(coords[0]+1, coords[1]+1),
                                    Direction.East));
         
         int[] newCoords = {coords[0], coords[1]-1};
         applyPeg(peg.top.mRightTop, visited, walls, newCoords);
      }
      if (peg.left != null)
      {
         
         if (coords[0] >= 0 && coords[0] < model.getSize().width &&
             coords[1] >= 0 && coords[1] < model.getSize().height)
            walls.add(model.getWall(new MazeCell(coords[0]+1, coords[1]+1),
                                    Direction.South));

         int[] newCoords = {coords[0]-1, coords[1]};
         //MazeCell newHost = new MazeCell(coords[0], coords[1]+1);
         applyPeg(peg.left.mLeftBottom, visited, walls, newCoords);
      }
      if (peg.right != null)
      {
         int[] newCoords = {coords[0]+1, coords[1]};
         //MazeCell newHost = new MazeCell(coords[0]+2, coords[1]+1);
         applyPeg(peg.right.mRightTop, visited, walls, newCoords);
      }
      if (peg.bottom != null)
      {
         int[] newCoords = {coords[0], coords[1]+1};
         //MazeCell newHost = new MazeCell(coords[0]+1, coords[1]+2);
         applyPeg(peg.bottom.mLeftBottom, visited, walls, newCoords);
      }

   }
}
