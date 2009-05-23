package maze.gui.mazeeditor;

import maze.Main;

/**
 * This class provides a cross shaped MazeTemplate that can be grown or shrunk
 * by increasing the length of its arms.
 * @author Johnathan Smith
 */
public class CrossTemplate extends ConjoinedMazeTemplate
{
   private static final int MIN_SIZE = 1;
   private int mSize = MIN_SIZE;

   /**
    * This constructor loads the Cross.png image and sets the template
    * description to "Cross".  The template is then updated.
    */
   public CrossTemplate()
   {
      this.mIcon = Main.getImageResource("gui/mazeeditor/images/Cross.png");
      this.mDesc = "Cross";
      updateTemplate();
   }

   /**
    * Does nothing
    */
   @Override
   public void nextOrientation(){}

   /**
    * Increases the length of each arm of the cross by 1
    */
   @Override
   public void grow()
   {
      mSize++;
      updateTemplate();
   }

   /**
    * Decreases the length of each arm of the cross by 1
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
    * Resets the template to be a cross with each arm being length 1
    */
   @Override
   public void reset()
   {
      if (mSize > MIN_SIZE)
      {
         mSize = MIN_SIZE;
         updateTemplate();
      }
   }

   /**
    * Rebuilds the template based on its size
    */
   public void updateTemplate()
   {
      mCenter = new TemplatePeg();
      TemplateWall newWall;
      TemplatePeg newPeg;
      TemplatePeg last = mCenter;

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

      last = mCenter;

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

      last = mCenter;

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

      last = mCenter;

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
