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
public class CrossTemplate extends MazeTemplate
{
   private static final int MIN_SIZE = 1;
   private int mSize = MIN_SIZE;
   public CrossTemplate()
   {
      URL iconResource = BoxTemplate.class.getResource("images/Cross.png");
      this.mIcon = new ImageIcon(iconResource);
      this.mDesc = "Cross";
      updateTemplate();
   }
   @Override
   public void nextOrientation(){}

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

      g.translate(initX, initY);

      g.setColor(PEG_COLOR);
      g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());

      for(int i = 0; i < mSize; i++)
      {
         g.translate(0, -size.getCellHeight());
         g.setColor(WALL_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
         g.translate(0, -size.getWallHeight());
         g.setColor(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }
      g.translate(0, mSize*(size.getWallHeight()+size.getCellHeight()));

      for(int i = 0; i < mSize; i++)
      {
         g.translate(0, size.getWallHeight());
         g.setColor(WALL_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getCellHeight());
         g.translate(0, size.getCellHeight());
         g.setColor(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }
      g.translate(0, -mSize*(size.getWallHeight()+size.getCellHeight()));

      for(int i = 0; i < mSize; i++)
      {
         g.translate(-size.getCellWidth(), 0);
         g.setColor(WALL_COLOR);
         g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
         g.translate(-size.getWallWidth(), 0);
         g.setColor(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }
      g.translate(mSize*(size.getWallWidth()+size.getCellWidth()), 0);

      for(int i = 0; i < mSize; i++)
      {
         g.translate(size.getWallWidth(), 0);
         g.setColor(WALL_COLOR);
         g.fillRect(0, 0, size.getCellWidth(), size.getWallHeight());
         g.translate(size.getCellWidth(), 0);
         g.setColor(PEG_COLOR);
         g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());
      }
      g.translate(-mSize*(size.getWallWidth()+size.getCellWidth()), 0);

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
      TemplateWall newWall;
      TemplatePeg newPeg;
      TemplatePeg last = mCenter;

      for (int i = 0; i < mSize; i++)
      {
         newWall = new TemplateWall();
         last.mTop = newWall;
         newWall.mLeftBottom = last;
         newPeg = new TemplatePeg();
         newPeg.mBottom = newWall;
         newWall.mRightTop = newPeg;
         last = newPeg;
      }

      last = mCenter;

      for (int i = 0; i < mSize; i++)
      {
         newWall = new TemplateWall();
         last.mBottom = newWall;
         newWall.mRightTop = last;
         newPeg = new TemplatePeg();
         newPeg.mTop = newWall;
         newWall.mLeftBottom = newPeg;
         last = newPeg;
      }

      last = mCenter;

      for (int i = 0; i < mSize; i++)
      {
         newWall = new TemplateWall();
         last.mRight = newWall;
         newWall.mLeftBottom = last;
         newPeg = new TemplatePeg();
         newPeg.mLeft = newWall;
         newWall.mRightTop = newPeg;
         last = newPeg;
      }

      last = mCenter;

      for (int i = 0; i < mSize; i++)
      {
         newWall = new TemplateWall();
         last.mLeft = newWall;
         newWall.mRightTop = last;
         newPeg = new TemplatePeg();
         newPeg.mRight = newWall;
         newWall.mLeftBottom = newPeg;
         last = newPeg;
      }
   }
}
