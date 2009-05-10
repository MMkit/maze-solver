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
public class ZigZagTemplate extends ConjoinedMazeTemplate
{
   private static final int MIN_SIZE = 2;
   private int mSize = MIN_SIZE;
   private boolean mForward = true;

   public ZigZagTemplate()
   {
      URL iconResource = MazeTemplate.class.getResource("images/ZigZag.png");
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
   public void reset()
   {
      mForward = true;
      mSize = MIN_SIZE;
      updateTemplate();
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
}
