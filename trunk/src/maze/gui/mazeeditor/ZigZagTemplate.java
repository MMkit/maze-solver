/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.net.URL;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import maze.gui.CellSize;

/**
 *
 * @author desolc
 */
public class ZigZagTemplate extends MazeTemplate
{
   private TemplatePeg mCenter = null;
   private Point mCenterPoint = new Point(0,0);
   private static final int MIN_SIZE = 2;
   private int mSize = MIN_SIZE;
   private boolean mForward = true;

   public ZigZagTemplate()
   {
      URL iconResource = BoxTemplate.class.getResource("images/ZigZag.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "Zig Zag";
      updateTemplate();
   }

   @Override
   public void nextOrientation()
   {
      mForward = !mForward;
      updateTemplate();
   }

   @Override
   public void grow()
   {
      mSize++;
      updateTemplate();
   }

   @Override
   public void shrink()
   {
      if (mSize > MIN_SIZE)
      {
         mSize--;
         updateTemplate();
      }
   }

   @Override
   public void draw(Graphics2D g, CellSize size)
   {
      g.translate(mCenterPoint.x-size.getWallWidthHalf(),
            mCenterPoint.y-size.getWallHeightHalf());

      TreeSet<TemplatePeg> visited = new TreeSet<TemplatePeg>();
      drawPeg(mCenter,visited, g, size);
      g.translate(-(mCenterPoint.x-size.getWallWidthHalf()),
                  -(mCenterPoint.y-size.getWallHeightHalf()));
   }

   @Override
   public void reset()
   {
      if (mSize > MIN_SIZE)
      {
         mSize = MIN_SIZE;
         updateTemplate();
      }
   }

   public void updateTemplate()
   {
      mCenter = new TemplatePeg();
      int up = mSize/2;
      int down = mSize/2 + mSize%2;

      TemplateWall newWall;
      TemplatePeg newPeg;
      TemplatePeg last = mCenter;

      for (int i =0; i < up; i++)
      {
         if (i%2 == 0)
         {
            newWall = new TemplateWall();
            last.top = newWall;
            newWall.mLeftBottom = last;
            newPeg = new TemplatePeg();
            newWall.mRightTop = newPeg;
            newPeg.bottom = newWall;
            
         }
         else
         {
            newPeg = new TemplatePeg();
            newWall = new TemplateWall();
            if (mForward)
            {
               last.right = newWall;
               newWall.mLeftBottom = last;
               newPeg.left = newWall;
               newWall.mRightTop = newPeg;
            }
            else
            {
               last.left = newWall;
               newWall.mRightTop = last;
               newPeg.right = newWall;
               newWall.mLeftBottom = newPeg;
            }
         }
         last = newPeg;
      }

      last = mCenter;

      for (int i =0; i < down; i++)
      {
         if (i%2 == 0)
         {
            newPeg = new TemplatePeg();
            newWall = new TemplateWall();
            if (mForward)
            {
               last.left = newWall;
               newWall.mRightTop = last;
               newPeg.right = newWall;
               newWall.mLeftBottom = newPeg;
            }
            else
            {
               last.right = newWall;
               newWall.mLeftBottom = last;
               newPeg.left = newWall;
               newWall.mRightTop = newPeg;
            }
         }
         else
         {
            newWall = new TemplateWall();
            last.bottom = newWall;
            newWall.mRightTop = last;
            newPeg = new TemplatePeg();
            newWall.mLeftBottom = newPeg;
            newPeg.top = newWall;
         }
         last = newPeg;
      }
   }
   @Override
   public TemplatePeg[] getCenterPegs()
   {
      return new TemplatePeg[]{mCenter};
   }

   @Override
   public Point[] getCenterPoints()
   {
      return new Point[]{mCenterPoint};
   }

   @Override
   public void updatePosition(Point p, CellSize size)
   {
      mCenterPoint = (Point)p.clone();
   }
}
