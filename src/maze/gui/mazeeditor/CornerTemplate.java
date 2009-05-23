package maze.gui.mazeeditor;

import maze.Main;

/**
 * This class provides a corner shaped MazeTemplate that can be grown or shrunk
 * by increasing the length of its sides.
 * @author Johnathan Smith
 */
public class CornerTemplate extends ConjoinedMazeTemplate
{
   private static final int MIN_SIZE = 1;
   private Corner currentCorner = Corner.BottomLeft;
   private int mSize = MIN_SIZE;

   /**
    * This constructor loads the Corner.png image and sets the template
    * description to "Corner".  The template is then updated.
    */
   public CornerTemplate()
   {
      this.mIcon = Main.getImageResource("gui/mazeeditor/images/Corner.png");
      this.mDesc = "Corner";
      updateTemplate();
   }

   /**
    * Rotates the corner template 90 degress clockwise.
    */
   @Override
   public void nextOrientation()
   {
      Corner[] corners = Corner.values();
      currentCorner = corners[(currentCorner.ordinal()+1)%corners.length];
      updateTemplate();
   }

   /**
    * Increases the length of each side of the corner by 1
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
      if (mSize > MIN_SIZE)
      {
         mSize--;
         updateTemplate();
      }
   }

   /**
    * Resets the template to be a corner with size equal to 1
    */
   @Override
   public void reset()
   {
      mSize = MIN_SIZE;
      currentCorner = Corner.BottomLeft;
      updateTemplate();
   }

   /**
    * Rebuilds the template based on its size and orientation.
    */
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

   /**
    * This enumation provides the four possible orientations that the corner
    * template can be in.
    */
   private static enum Corner
   {
      BottomLeft,
      TopLeft,
      TopRight,
      BottomRight

   }
}
