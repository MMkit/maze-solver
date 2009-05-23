/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;


/**
 *
 * @author Johnathan Smith
 */
public class WaveTemplate extends ConjoinedMazeTemplate
{
   private static final int MIN_SIZE = 0;
   private int mSize = MIN_SIZE;
   private int rotation = 0;

   public WaveTemplate()
   {
      this.mIcon = maze.Main.getImageResource("gui/mazeeditor/images/Wave.png");
      this.mDesc = "Square Wave";
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

   private void updateTemplate()
   {
      int firstCount = mSize/2 + 1;
      int secondCount = mSize/2+ mSize%2;
      TemplatePeg last = new TemplatePeg();
      while(firstCount > 0)
      {
         last = createWave(last);
         firstCount--;
      }

      switch(rotation)
      {
         case 0:
            mCenter = last.right.mRightTop;
            break;
         case 1:
            mCenter = last.bottom.mLeftBottom;
            break;
         case 2:
            mCenter = last.left.mLeftBottom;
            break;
         case 3:
            mCenter = last.top.mRightTop;
            break;
      }

      while (secondCount > 0)
      {
         last = createWave(last);
         secondCount--;
      }
   }

   private TemplatePeg createWave(TemplatePeg center)
   {
      TemplatePeg[] pegs = new TemplatePeg[4];
      TemplateWall[] walls = new TemplateWall[4];
      for(int i = 0; i < 4; i++)
      {
         pegs[i] = new TemplatePeg();
         walls[i] = new TemplateWall();
      }
      
      if (rotation < 2)
      {
         walls[0].mLeftBottom = center;
         walls[0].mRightTop = pegs[0];
         walls[2].mLeftBottom = pegs[2];
         walls[2].mRightTop = pegs[1];
      }
      else
      {
         walls[0].mRightTop = center;
         walls[0].mLeftBottom = pegs[0];
         walls[2].mRightTop = pegs[2];
         walls[2].mLeftBottom = pegs[1];
      }

      if (rotation == 0 || rotation == 3)
      {
         walls[1].mRightTop = pegs[0];
         walls[1].mLeftBottom = pegs[1];
         walls[3].mRightTop = pegs[2];
         walls[3].mLeftBottom = pegs[3];
      }
      else
      {
         walls[1].mLeftBottom = pegs[0];
         walls[1].mRightTop = pegs[1];
         walls[3].mLeftBottom = pegs[2];
         walls[3].mRightTop = pegs[3];
      }

      switch(rotation)
      {
         case 0:
            center.top = pegs[0].bottom = walls[0];
            pegs[0].left = pegs[1].right = walls[1];
            pegs[1].bottom = pegs[2].top = walls[2];
            pegs[2].left = pegs[3].right = walls[3];
            break;
         case 1:
            center.right = pegs[0].left = walls[0];
            pegs[0].top = pegs[1].bottom = walls[1];
            pegs[1].left = pegs[2].right = walls[2];
            pegs[2].top = pegs[3].bottom = walls[3];
            break;
         case 2:
            center.bottom = pegs[0].top = walls[0];
            pegs[0].right = pegs[1].left = walls[1];
            pegs[1].top = pegs[2].bottom = walls[2];
            pegs[2].right = pegs[3].left = walls[3];
            break;
         case 3:
            center.left = pegs[0].right = walls[0];
            pegs[0].bottom = pegs[1].top = walls[1];
            pegs[1].right = pegs[2].left = walls[2];
            pegs[2].bottom = pegs[3].top = walls[3];
            break;
      }
      return pegs[3];
   }
}
