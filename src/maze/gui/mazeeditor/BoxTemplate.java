package maze.gui.mazeeditor;

import maze.Main;

/**
 * This class provides a square shaped MazeTemplate that can be grown or shrunk
 * by increasing the length of its sides.
 * @author Johnathan Smith
 */
public class BoxTemplate extends ConjoinedMazeTemplate
{
   private boolean mAbove = true;
   private static int MIN_SIZE = 1;
   private int mSize = MIN_SIZE;

   /**
    * This constructor loads the Box.png image and sets the template description
    * to "Box".  The template is then updated.
    */
   public BoxTemplate()
   {
      this.mIcon = Main.getImageResource("gui/mazeeditor/images/Box.png");
      this.mDesc = "Box";
      updateTemplate();
   }

   /**
    * Toggles between the box being above the mouse or below the mouse
    */
   @Override
   public void nextOrientation()
   {
      mAbove = !mAbove;
      TemplatePeg last = mCenter;
      if (mAbove)
      {
         while(last.bottom != null)
            last = last.bottom.mLeftBottom;
         while(last.right != null)
            last = last.right.mRightTop;
         mCenter = last;
      }
      else
      {
         while(last.top != null)
            last = last.top.mRightTop;
         while(last.left != null)
            last = last.left.mLeftBottom;
         mCenter = last;
      }
   }

   /**
    * Increases the length of each side of the box by 1
    */
   @Override
   public void grow()
   {
      mSize++;
      updateTemplate();
   }

   /**
    * Decreases the length of each side by 1
    */
   @Override
   public void shrink()
   {
      int newSize = Math.max(MIN_SIZE, mSize-1);
      if (newSize == mSize)
         return;
      mSize = newSize;
      updateTemplate();
   }

   /**
    * Resets the template to be a box with size equal to 1
    */
   @Override
   public void reset()
   {
      mAbove = true;
      mSize = MIN_SIZE;
      updateTemplate();
   }

   /**
    * Rebuilds the template based on the size of the box and whether it is
    * above or below the mouse pointer.
    */
   private void updateTemplate()
   {
      mCenter = new TemplatePeg();
      TemplatePeg last = mCenter;
      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.top = tw;
         tw.mLeftBottom = last;
         last = new TemplatePeg();
         tw.mRightTop = last;
         last.bottom = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.left = tw;
         tw.mRightTop = last;
         last = new TemplatePeg();
         tw.mLeftBottom = last;
         last.right = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.bottom = tw;
         tw.mRightTop = last;
         last = new TemplatePeg();
         tw.mLeftBottom = last;
         last.top = tw;
      }

      for (int i= 0; i < mSize; i++)
      {
         TemplateWall tw = new TemplateWall();
         last.right = tw;
         tw.mLeftBottom = last;
         last = new TemplatePeg();
         tw.mRightTop = last;
         last.left = tw;
      }
      last.left.mRightTop = mCenter;
      mCenter.left = last.left;

      if (!mAbove)
      {
         last = mCenter;
         while(last.top != null)
            last = last.top.mRightTop;
         while(last.left != null)
            last = last.left.mLeftBottom;
         mCenter = last;
      }
   }
}
