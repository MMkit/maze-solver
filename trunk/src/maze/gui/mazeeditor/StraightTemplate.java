/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import javax.swing.ImageIcon;

/**
 *
 * @author desolc
 */
public class StraightTemplate extends ConjoinedMazeTemplate
{
   private static final int MIN_SIZE = 2;
   private boolean mVert = true;
   private int mSize = MIN_SIZE;

   public StraightTemplate()
   {
      this.mIcon = maze.Main.getImageResource("gui/mazeeditor/images/Straight.png");
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
   public void reset()
   {
      mVert = true;
      mSize = MIN_SIZE;
      updateTemplate();
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
}
