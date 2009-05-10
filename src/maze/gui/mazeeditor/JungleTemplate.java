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
public class JungleTemplate extends StaggeredTemplate
{

   public JungleTemplate()
   {
      URL iconResource = MazeTemplate.class.getResource("images/Jungle.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "Jungle";
      updateTemplate();
   }

   @Override
   protected void updateTemplate()
   {
      int positive = mSize/2 + mSize%2 + 1;
      int negative = mSize/2 + 1;
      mCenter = new TemplatePeg();
      TemplatePeg last = mCenter;
      while(positive > 0)
      {
         last = createCHalf(last, true);
         positive--;
      }

      last = mCenter;

      while (negative > 0)
      {
         last = createCHalf(last, false);
         negative--;
      }

      TemplatePeg newPeg = new TemplatePeg();
      TemplateWall newWall = new TemplateWall();

      switch(rotation)
      {
         case 0:
            mCenter.right = newWall;
            newWall.mLeftBottom = mCenter;
            newWall.mRightTop = newPeg;
            newPeg.left = newWall;
            break;
         case 1:
            mCenter.bottom = newWall;
            newWall.mRightTop = mCenter;
            newWall.mLeftBottom = newPeg;
            newPeg.top = newWall;
            break;
         case 2:
            mCenter.left = newWall;
            newWall.mRightTop = mCenter;
            newWall.mLeftBottom = newPeg;
            newPeg.right = newWall;
            break;
         case 3:
            mCenter.top = newWall;
            newWall.mLeftBottom = mCenter;
            newWall.mRightTop = newPeg;
            newPeg.bottom = newWall;
            break;
      }
   }
}
