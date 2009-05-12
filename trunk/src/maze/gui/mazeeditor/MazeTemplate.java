package maze.gui.mazeeditor;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
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
   protected Paint mWallPaint;
   protected static final Color PEG_COLOR = Color.BLACK;
   protected ImageIcon mIcon = null;
   protected String mDesc = "";

   public abstract TemplatePeg[] getCenterPegs();
   public abstract Point[] getCenterPoints(CellSize size);
   public abstract void updatePosition(Point p, CellSize size);
   public abstract void nextOrientation();
   public abstract void grow();
   public abstract void shrink();
   public void draw(Graphics2D g, CellSize size)
   {
      mWallPaint = new GradientPaint(new Point(0, 0), new Color(0, 94, 189),
                                     new Point(size.getTotalWidth()/2, 
                                     size.getTotalHeight()/2),
                                     new Color(0, 56, 112), true);

      TemplatePeg[] pegs = this.getCenterPegs();
      Point[] points = this.getCenterPoints(size);

      TreeSet<TemplatePeg> visited = new TreeSet<TemplatePeg>();
      for (int i = 0; i < points.length; i+=2)
      {
         Point origin = new Point(points[i].x-size.getWallWidth(),
                                  points[i].y-size.getWallHeightHalf());

         drawPeg(pegs[i],visited, g, size, origin);
      }
   }

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
                          Graphics2D g, CellSize size, Point origin)
   {
      if (peg == null || visited.contains(peg))
         return;

      visited.add(peg);

      g.setPaint(null);
      g.setColor(PEG_COLOR);
      g.fillRect(origin.x, origin.y, size.getWallWidth(), size.getWallHeight());

      if (peg.top != null)
      {
         origin.y -= size.getCellHeight();
         g.setPaint(mWallPaint);
         g.fillRect(origin.x, origin.y, size.getWallWidth(),
                    size.getCellHeight());
         origin.y -= size.getWallHeight();
         drawPeg(peg.top.mRightTop, visited, g, size, origin);
         origin.y += size.getCellHeight()+size.getWallHeight();
      }

      if (peg.bottom != null)
      {
         origin.y += size.getCellHeight()+size.getWallHeight();
         drawPeg(peg.bottom.mLeftBottom, visited, g, size, origin);
         origin.y -= size.getCellHeight()+size.getWallHeight();
      }

      if (peg.left != null)
      {
         origin.x -= size.getCellWidth();
         g.setPaint(mWallPaint);
         g.fillRect(origin.x, origin.y, size.getCellWidth(),
                    size.getWallHeight());
         origin.x -= size.getWallWidth();
         drawPeg(peg.left.mLeftBottom, visited, g, size, origin);
         origin.x += size.getCellWidth()+size.getWallWidth();
      }

      if (peg.right != null)
      {
         origin.x += size.getCellWidth()+size.getWallWidth();
         drawPeg(peg.right.mRightTop, visited, g, size, origin);
         origin.x -= size.getCellWidth()+size.getWallWidth();
      }
   }
}
