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
public class BoxTemplate extends MazeTemplate
{
   private TemplatePeg mCenter = null;
   private Point mCenterPoint = new Point(0,0);
   private boolean mAbove = true;
   private static int MIN_SIZE = 1;
   private int mSize = MIN_SIZE;

   public BoxTemplate()
   {
      URL iconResource = BoxTemplate.class.getResource("images/Box.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "o";
      updateTemplate();
   }

   @Override
   public void nextOrientation()
   {
      mAbove = !mAbove;
      TemplatePeg last = mCenter;
      if (mAbove)
      {
         while(last.bottom != null)
            last = last.bottom.mLeftBottom;
         while(last.right != null)
            last = last.right.mRightTop;
         mCenter = last;
      }
      else
      {
         while(last.top != null)
            last = last.top.mRightTop;
         while(last.left != null)
            last = last.left.mLeftBottom;
         mCenter = last;
      }
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
      int newSize = Math.max(MIN_SIZE, mSize-1);
      if (newSize == mSize)
         return;
      mSize = newSize;
      updateTemplate();
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
      if (mSize != MIN_SIZE)
      {
         mSize = MIN_SIZE;
         updateTemplate();
      }
   }

   private void updateTemplate()
   {
      mCenter = new TemplatePeg();
      TemplatePeg last = mCenter;
      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.top = tw;
         tw.mLeftBottom = last;
         last = new TemplatePeg();
         tw.mRightTop = last;
         last.bottom = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.left = tw;
         tw.mRightTop = last;
         last = new TemplatePeg();
         tw.mLeftBottom = last;
         last.right = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.bottom = tw;
         tw.mRightTop = last;
         last = new TemplatePeg();
         tw.mLeftBottom = last;
         last.top = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.right = tw;
         tw.mLeftBottom = last;
         last = new TemplatePeg();
         tw.mRightTop = last;
         last.left = tw;
      }
      last.left.mRightTop = mCenter;
      mCenter.left = last.left;

      if (!mAbove)
      {
         last = mCenter;
         while(last.top != null)
            last = last.top.mRightTop;
         while(last.left != null)
            last = last.left.mLeftBottom;
         mCenter = last;
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
