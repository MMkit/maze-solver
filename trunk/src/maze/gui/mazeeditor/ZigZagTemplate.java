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
public class ZigZagTemplate extends MazeTemplate
{
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
      int initX = mCenterPoint.x - size.getWallWidthHalf();
      int initY = mCenterPoint.y - size.getWallHeightHalf();

      int up = mSize/2;
      int down = mSize/2 + mSize%2;

      g.translate(initX, initY);
      
      g.setColor(PEG_COLOR);
      g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());

      int totalTransX = 0, totalTransY = 0;

      for (int i = 0; i < up; i++)
      {
         if (i%2 == 0)
         {
            g.translate(0, -size.getCellHeight());
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
            g.translate(0, -size.getWallHeight());
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
            totalTransY -= size.getCellHeight()+size.getWallHeight();
         }
         else
         {
            int firstTrans, secondTrans;
            if (mForward)
            {
               firstTrans = size.getWallWidth();
               secondTrans = size.getCellWidth();
            }
            else
            {
               firstTrans = -size.getCellWidth();
               secondTrans = -size.getWallWidth();
            }

            totalTransX += firstTrans+secondTrans;
            
            g.translate(firstTrans, 0);
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
            g.translate(secondTrans, 0);
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
         }
      }

      g.translate(-totalTransX, -totalTransY);

      totalTransX = totalTransY = 0;

      for (int i = 0; i < down; i++)
      {
         if (i%2 == 0)
         {
            int firstTrans, secondTrans;
            if (mForward)
            {
               firstTrans = -size.getCellWidth();
               secondTrans = -size.getWallWidth();
            }
            else
            {
               firstTrans = size.getWallWidth();
               secondTrans = size.getCellWidth();
            }

            totalTransX += firstTrans+secondTrans;
            
            g.translate(firstTrans, 0);
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
            g.translate(secondTrans, 0);
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
         }
         else
         {
            g.translate(0, size.getWallHeight());
            g.setColor(WALL_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
            g.translate(0, size.getCellHeight());
            g.setColor(PEG_COLOR);
            g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
            totalTransY += size.getCellHeight()+size.getWallHeight();
         }
      }

      g.translate(-totalTransX, -totalTransY);

      g.translate(-initX, -initY);
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
            last.mTop = newWall;
            newWall.mLeftBottom = last;
            newPeg = new TemplatePeg();
            newWall.mRightTop = newPeg;
            newPeg.mBottom = newWall;
            
         }
         else
         {
            newPeg = new TemplatePeg();
            newWall = new TemplateWall();
            if (mForward)
            {
               last.mRight = newWall;
               newWall.mLeftBottom = last;
               newPeg.mLeft = newWall;
               newWall.mRightTop = newPeg;
            }
            else
            {
               last.mLeft = newWall;
               newWall.mRightTop = last;
               newPeg.mRight = newWall;
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
               last.mLeft = newWall;
               newWall.mRightTop = last;
               newPeg.mRight = newWall;
               newWall.mLeftBottom = newPeg;
            }
            else
            {
               last.mRight = newWall;
               newWall.mLeftBottom = last;
               newPeg.mLeft = newWall;
               newWall.mRightTop = newPeg;
            }
         }
         else
         {
            newWall = new TemplateWall();
            last.mBottom = newWall;
            newWall.mRightTop = last;
            newPeg = new TemplatePeg();
            newWall.mLeftBottom = newPeg;
            newPeg.mTop = newWall;
         }
         last = newPeg;
      }
   }

}
