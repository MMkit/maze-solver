package maze.gui.mazeeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import java.util.TreeSet;
import javax.swing.ImageIcon;

import maze.gui.CellSize;

/**
 *
 * @author John Smith
 */
public abstract class MazeTemplate
{
   protected static final Color WALL_COLOR = new Color(0, 94, 189);
   protected static final Color PEG_COLOR = Color.BLACK;   
   protected ImageIcon mIcon = null;
   protected String mDesc = "";

   public abstract TemplatePeg[] getCenterPegs();
   public abstract Point[] getCenterPoints();
   public abstract void updatePosition(Point p, CellSize size);
   public abstract void nextOrientation();
   public abstract void grow();
   public abstract void shrink();
   public abstract void draw(Graphics2D g, CellSize size);
   public abstract void reset();
   
   public ImageIcon getTemplateIcon()
   {
      return mIcon;
   }
   public String getTemplateDescription()
   {
      return mDesc;
   }

   protected void drawPeg(TemplatePeg peg, TreeSet<TemplatePeg> visited,
                          Graphics2D g, CellSize size)
   {
      if (peg == null || visited.contains(peg))
         return;

      visited.add(peg);

      g.setColor(PEG_COLOR);
      g.fillRect(0, 0, size.getWallWidth(), size.getWallHeight());

      if (peg.top != null)
      {
         g.translate(0, -size.getCellHeight());
         g.setColor(WALL_COLOR);
         g.fillRect(0,0, size.getWallWidth(), size.getCellHeight());
         g.translate(0, -size.getWallHeight());
         drawPeg(peg.top.mRightTop, visited, g, size);
         g.translate(0, size.getCellHeight()+size.getWallHeight());
      }

      if (peg.bottom != null)
      {
         g.translate(0, size.getCellHeight()+size.getWallHeight());
         drawPeg(peg.bottom.mLeftBottom, visited, g, size);
         g.translate(0, -(size.getCellHeight()+size.getWallHeight()));
      }

      if (peg.left != null)
      {
         g.translate(-size.getCellWidth(), 0);
         g.setColor(WALL_COLOR);
         g.fillRect(0,0, size.getCellWidth(), size.getWallHeight());
         g.translate(-size.getWallWidth(), 0);
         drawPeg(peg.left.mLeftBottom, visited, g, size);
         g.translate(size.getCellWidth()+size.getWallWidth(), 0);
      }

      if (peg.right != null)
      {
         g.translate(size.getCellWidth()+size.getWallWidth(), 0);
         drawPeg(peg.right.mRightTop, visited, g, size);
         g.translate(-(size.getCellWidth()+size.getWallWidth()), 0);
      }
   }
}
