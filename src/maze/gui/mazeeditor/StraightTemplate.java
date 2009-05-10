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
public class StraightTemplate extends MazeTemplate
{
   private TemplatePeg mCenter = null;
   private Point mCenterPoint = new Point(0,0);
   private static final int MIN_SIZE = 2;
   private boolean mVert = true;
   private int mSize = MIN_SIZE;

   public StraightTemplate()
   {
      URL iconResource = BoxTemplate.class.getResource("images/Straight.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "Straight";
      updateTemplate();
   }

   @Override
   public void nextOrientation()
   {
      mVert = !mVert;
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

   @Override
   public ImageIcon getTemplateIcon()
   {
      return this.mIcon;
   }

   @Override
   public String getTemplateDescription()
   {
      return this.mDesc;
   }

   private void updateTemplate()
   {
      TemplatePeg last;
      TemplateWall newWall;
      TemplatePeg newPeg;
      int upRight = mSize/2+mSize%2;
      int downLeft = mSize/2;
      mCenter = new TemplatePeg();
      last = mCenter;
      while (upRight > 0)
      {
         newWall = new TemplateWall();
         newPeg = new TemplatePeg();
         if (mVert)
         {
            last.top = newWall;
            newPeg.bottom = newWall;
         }
         else
         {
            last.right = newWall;
            newPeg.left = newWall;
         }
         newWall.mLeftBottom = last;
         newWall.mRightTop = newPeg;

         last = newPeg;
         upRight--;
      }
      last = mCenter;
      while (downLeft > 0)
      {
         newWall = new TemplateWall();
         newPeg = new TemplatePeg();
         if (mVert)
         {
            last.bottom = newWall;
            newPeg.top = newWall;
         }
         else
         {
            last.left = newWall;
            newPeg.right = newWall;
         }
         newWall.mRightTop = last;
         newWall.mLeftBottom = newPeg;

         last = newPeg;
         downLeft--;
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
