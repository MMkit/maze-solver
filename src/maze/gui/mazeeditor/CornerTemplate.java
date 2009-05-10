/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author desolc
 */
public class CornerTemplate extends ConjointedMazeTemplate
{
   private static final int MIN_SIZE = 1;
   private Corner currentCorner = Corner.BottomLeft;
   private int mSize = MIN_SIZE;

   public CornerTemplate()
   {
      URL iconResource = MazeTemplate.class.getResource("images/Corner.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "Corner";
      updateTemplate();
   }

   @Override
   public void nextOrientation()
   {
      Corner[] corners = Corner.values();
      currentCorner = corners[(currentCorner.ordinal()+1)%corners.length];
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
      TemplateWall newWall;
      TemplatePeg newPeg;
      TemplatePeg last = mCenter;
      if (currentCorner == Corner.BottomLeft ||
          currentCorner == Corner.BottomRight)
      {
         for (int i = 0; i < mSize; i++)
         {
            newWall = new TemplateWall();
            last.top = newWall;
            newWall.mLeftBottom = last;
            newPeg = new TemplatePeg();
            newPeg.bottom = newWall;
            newWall.mRightTop = newPeg;
            last = newPeg;
         }
      }
      else
      {
         for (int i = 0; i < mSize; i++)
         {
            newWall = new TemplateWall();
            last.bottom = newWall;
            newWall.mRightTop = last;
            newPeg = new TemplatePeg();
            newPeg.top = newWall;
            newWall.mLeftBottom = newPeg;
            last = newPeg;
         }
      }

      last = mCenter;

      if (currentCorner == Corner.TopLeft ||
          currentCorner == Corner.BottomLeft)
      {
         for (int i = 0; i < mSize; i++)
         {
            newWall = new TemplateWall();
            last.right = newWall;
            newWall.mLeftBottom = last;
            newPeg = new TemplatePeg();
            newPeg.left = newWall;
            newWall.mRightTop = newPeg;
            last = newPeg;
         }
      }
      else
      {
         for (int i = 0; i < mSize; i++)
         {
            newWall = new TemplateWall();
            last.left = newWall;
            newWall.mRightTop = last;
            newPeg = new TemplatePeg();
            newPeg.right = newWall;
            newWall.mLeftBottom = newPeg;
            last = newPeg;
         }
      }
   }

   private static enum Corner
   {
      BottomLeft,
      TopLeft,
      TopRight,
      BottomRight

   }
}
