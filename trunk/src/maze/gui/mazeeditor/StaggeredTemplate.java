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
public class StaggeredTemplate extends ConjoinedMazeTemplate
{
   private static final int MIN_SIZE = 0;
   protected int mSize = MIN_SIZE;
   protected int rotation = 0;

   public StaggeredTemplate()
   {
      URL iconResource = MazeTemplate.class.getResource("images/Staggered.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "Staggered";
      updateTemplate();
   }

   @Override
   public void nextOrientation()
   {
      rotation = ((rotation+1)%4);
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
      rotation = 0;
      mSize = MIN_SIZE;
      updateTemplate();
   }

   protected void updateTemplate()
   {
      int positive = mSize/2 + mSize%2;
      int negative = mSize/2;
      mCenter = new TemplatePeg();
      TemplatePeg lastPos, lastNeg;
      TemplatePeg newPeg;
      TemplateWall newWall;
      lastPos = lastNeg = mCenter;

      lastPos = createCHalf(mCenter, true);
      lastNeg = createCHalf(mCenter, false);

      while(positive > 0)
      {
         newPeg = new TemplatePeg();
         newWall = new TemplateWall();
         if (rotation < 2)
         {
            newWall.mLeftBottom = lastPos;
            newWall.mRightTop = newPeg;
         }
         else
         {
            newWall.mRightTop = lastPos;
            newWall.mLeftBottom = newPeg;
         }

         switch(rotation)
         {
            case 0:
               lastPos.top = newPeg.bottom = newWall;
               break;
            case 1:
               lastPos.right = newPeg.left = newWall;
               break;
            case 2:
               lastPos.bottom = newPeg.top = newWall;
               break;
            case 3:
               lastPos.left = newPeg.right = newWall;
         }
         lastPos = createCHalf(newPeg, true);
         positive--;
      }

      while (negative > 0)
      {
         newPeg = new TemplatePeg();
         newWall = new TemplateWall();
         if (rotation < 2)
         {
            newWall.mRightTop = lastNeg;
            newWall.mLeftBottom = newPeg;
         }
         else
         {
            newWall.mLeftBottom = lastNeg;
            newWall.mRightTop = newPeg;
         }

         switch(rotation)
         {
            case 0:
               lastNeg.bottom = newPeg.top = newWall;
               break;
            case 1:
               lastNeg.left = newPeg.right = newWall;
               break;
            case 2:
               lastNeg.top = newPeg.bottom = newWall;
               break;
            case 3:
               lastNeg.right = newPeg.left = newWall;
               break;
         }
         lastNeg = createCHalf(newPeg, false);
         negative--;
      }
   }

   protected TemplatePeg createCHalf(TemplatePeg center, boolean positive)
   {
      TemplatePeg pegs[] = new TemplatePeg[2];
      TemplateWall walls[] = new TemplateWall[2];

      for(int i = 0; i < 2; i++)
      {
         pegs[i] = new TemplatePeg();
         walls[i] = new TemplateWall();
      }

      if (rotation == 0 || rotation == 3)
      {
         walls[1].mLeftBottom = pegs[0];
         walls[1].mRightTop = pegs[1];
      }
      else
      {
         walls[1].mLeftBottom = pegs[1];
         walls[1].mRightTop = pegs[0];
      }

      if (positive &&(rotation == 0 || rotation == 1) ||
          !positive && (rotation == 2 || rotation == 3))
      {
         walls[0].mLeftBottom = center;
         walls[0].mRightTop = pegs[0];
      }
      else
      {
         walls[0].mLeftBottom = pegs[0];
         walls[0].mRightTop = center;
      }

      switch(rotation)
      {
         case 0:
            if (positive)
            {
               center.top = walls[0];
               pegs[0].bottom = walls[0];
            }
            else
            {
               center.bottom = walls[0];
               pegs[0].top = walls[0];
            }
            pegs[0].right = pegs[1].left = walls[1];
            break;
         case 1:
            if (positive)
            {
               center.right = walls[0];
               pegs[0].left = walls[0];
            }
            else
            {
               center.left = walls[0];
               pegs[0].right = walls[0];
            }
            pegs[0].bottom = pegs[1].top = walls[1];
            break;
         case 2:
            if (positive)
            {
               center.bottom = walls[0];
               pegs[0].top = walls[0];
            }
            else
            {
               center.top = walls[0];
               pegs[0].bottom = walls[0];
            }
            pegs[0].left = pegs[1].right = walls[1];
            break;
         case 3:
            if (positive)
            {
               center.left = walls[0];
               pegs[0].right = walls[0];
            }
            else
            {
               center.right = walls[0];
               pegs[0].left = walls[0];
            }
            pegs[0].top = pegs[1].bottom = walls[1];
            break;
      }
      return pegs[0];
   }

}
