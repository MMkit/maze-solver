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
public class StraightTemplate extends MazeTemplate
{
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
      int initX = mCenterPoint.x - size.getWallWidthHalf();
      int initY = mCenterPoint.y - size.getWallHeightHalf();

      g.translate(initX, initY);

      g.setColor(PEG_COLOR);
      g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());

      int upLeft = mSize/2 + mSize%2;
      int downRight = mSize/2;

      int count = 0;
      if (mVert)
      {
         while (count < upLeft)
         {
            g.translate(0, -size.getCellHeight());
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
            g.translate(0, -size.getWallHeight());
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
            count++;
         }

         count = 0;
         g.translate(0, upLeft*(size.getWallHeight()+size.getCellHeight()));
         while (count < downRight)
         {
            g.translate(0, size.getWallHeight());
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
            g.translate(0, size.getCellHeight());
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
            count++;
         }
         g.translate(0, -downRight*(size.getWallHeight()+size.getCellHeight()));
      }
      else
      {
         while (count < downRight)
         {
            g.translate(-size.getCellWidth(), 0);
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
            g.translate(-size.getWallWidth(), 0);
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
            count++;
         }

         count = 0;
         g.translate(downRight*(size.getWallWidth()+size.getCellWidth()), 0);
         while (count < upLeft)
         {
            g.translate(size.getWallWidth(), 0);
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
            g.translate(size.getCellWidth(), 0);
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
            count++;
         }
         g.translate(-upLeft*(size.getWallWidth()+size.getCellWidth()), 0);
      }

      g.translate(-initX, -initY);

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
            last.mTop = newWall;
            newPeg.mBottom = newWall;
         }
         else
         {
            last.mRight = newWall;
            newPeg.mLeft = newWall;
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
            last.mBottom = newWall;
            newPeg.mTop = newWall;
         }
         else
         {
            last.mLeft = newWall;
            newPeg.mRight = newWall;
         }
         newWall.mRightTop = last;
         newWall.mLeftBottom = newPeg;

         last = newPeg;
         downLeft--;
      }
   }

}
