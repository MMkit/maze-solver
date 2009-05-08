/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Graphics2D;
import java.net.URL;
import javax.swing.ImageIcon;
import maze.gui.CellSize;

/**
 *
 * @author desolc
 */
public class BoxTemplate extends MazeTemplate
{
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
         while(last.mBottom != null)
            last = last.mBottom.mLeftBottom;
         while(last.mRight != null)
            last = last.mRight.mRightTop;
         mCenter = last;
      }
      else
      {
         while(last.mTop != null)
            last = last.mTop.mRightTop;
         while(last.mLeft != null)
            last = last.mLeft.mLeftBottom;
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
      int[] translate = {mCenterPoint.x-size.getWallWidthHalf(),
                         mCenterPoint.y-size.getWallHeightHalf()};
      if (!mAbove)
      {
         translate[0] += (size.getCellWidth() + size.getWallWidth())*mSize;
         translate[1] += (size.getCellHeight() + size.getWallHeight())*mSize;
      }

      g.translate(translate[0], translate[1]);
      
      for (int i = 0; i < mSize; i++)
      {
         g.translate(0, -size.getCellHeight());
         g.setPaint(WALL_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
         g.translate(0, -size.getWallHeight());
         g.setPaint(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }

      for (int i = 0; i < mSize; i++)
      {
         g.translate(-size.getCellWidth(), 0);
         g.setPaint(WALL_COLOR);
         g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
         g.translate(-size.getWallWidth(), 0);
         g.setPaint(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }

      for (int i = 0; i < mSize; i++)
      {
         g.translate(0, size.getWallHeight());
         g.setPaint(WALL_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
         g.translate(0, size.getCellHeight());
         g.setPaint(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }

      for (int i = 0; i < mSize; i++)
      {
         g.translate(size.getWallWidth(), 0);
         g.setPaint(WALL_COLOR);
         g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
         g.translate(size.getCellWidth(), 0);
         g.setPaint(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }
      g.translate(-translate[0], -translate[1]);
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
         last.mTop = tw;
         tw.mLeftBottom = last;
         last = new TemplatePeg();
         tw.mRightTop = last;
         last.mBottom = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.mLeft = tw;
         tw.mRightTop = last;
         last = new TemplatePeg();
         tw.mLeftBottom = last;
         last.mRight = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.mBottom = tw;
         tw.mRightTop = last;
         last = new TemplatePeg();
         tw.mLeftBottom = last;
         last.mTop = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.mRight = tw;
         tw.mLeftBottom = last;
         last = new TemplatePeg();
         tw.mRightTop = last;
         last.mLeft = tw;
      }
      last.mLeft.mRightTop = mCenter;
      mCenter.mLeft = last.mLeft;

      if (!mAbove)
      {
         last = mCenter;
         while(last.mTop != null)
            last = last.mTop.mRightTop;
         while(last.mLeft != null)
            last = last.mLeft.mLeftBottom;
         mCenter = last;
      }
   }
}
