package maze.gui.mazeeditor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ImageIcon;

import maze.gui.MazePainter;
import maze.model.CellSizeModel;

/**
 * @author John Smith
 */
public abstract class MazeTemplate
{
   protected ImageIcon mIcon = null;
   protected String mDesc = "";

   public abstract TemplatePeg[] getCenterPegs();

   public abstract Point[] getCenterPoints(CellSizeModel size);

   public abstract void updatePosition(Point p, CellSizeModel size);

   public abstract void nextOrientation();

   public abstract void grow();

   public abstract void shrink();

   /**
    * Draw the maze template on top of the maze view.
    * @param g What to draw on.
    * @param csm The model used to give us the sizes of cells, walls, and pegs.
    * @param painter The maze painter used to do the actual drawing of the pegs
    *           and wall segments.
    */
   public void draw(Graphics2D g, CellSizeModel csm, MazePainter painter)
   {
      TemplatePeg[] pegs = this.getCenterPegs();
      Point[] points = this.getCenterPoints(csm);

      TreeSet<TemplatePeg> visited = new TreeSet<TemplatePeg>();
      for (int i = 0; i < points.length; i += 2)
      {
         Point origin = new Point(points[i].x - csm.getWallWidth(), points[i].y -
                                                                    csm.getWallHeightHalf());

         drawPeg(pegs[i], visited, g, csm, origin, painter);
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

   /**
    * Recursive method used to draw pegs and walls.
    * @param peg Current peg to draw.
    * @param visited Pegs we have drawn.
    * @param g What to draw on.
    * @param size Gives us the size of pegs and walls.
    * @param origin The current location of what we are drawing.
    * @param painter Maze painting delegate.
    */
   protected void drawPeg(TemplatePeg peg, Set<TemplatePeg> visited, Graphics2D g,
         CellSizeModel size, Point origin, MazePainter painter)
   {
      if (peg == null || visited.contains(peg))
         return;

      visited.add(peg);

      painter.drawPeg(g, new Rectangle(origin.x,
                                       origin.y,
                                       size.getWallWidth(),
                                       size.getWallHeight()));

      if (peg.top != null)
      {
         origin.y -= size.getCellHeightInner();
         painter.drawWallSet(g, new Rectangle(origin.x,
                                              origin.y,
                                              size.getWallWidth(),
                                              size.getCellHeightInner()));
         origin.y -= size.getWallHeight();
         drawPeg(peg.top.mRightTop, visited, g, size, origin, painter);
         origin.y += size.getCellHeightInner() + size.getWallHeight();
      }

      if (peg.bottom != null)
      {
         origin.y += size.getCellHeightInner() + size.getWallHeight();
         drawPeg(peg.bottom.mLeftBottom, visited, g, size, origin, painter);
         origin.y -= size.getCellHeightInner() + size.getWallHeight();
      }

      if (peg.left != null)
      {
         origin.x -= size.getCellWidthInner();
         painter.drawWallSet(g, new Rectangle(origin.x,
                                              origin.y,
                                              size.getCellWidthInner(),
                                              size.getWallHeight()));
         origin.x -= size.getWallWidth();
         drawPeg(peg.left.mLeftBottom, visited, g, size, origin, painter);
         origin.x += size.getCellWidthInner() + size.getWallWidth();
      }

      if (peg.right != null)
      {
         origin.x += size.getCellWidthInner() + size.getWallWidth();
         drawPeg(peg.right.mRightTop, visited, g, size, origin, painter);
         origin.x -= size.getCellWidthInner() + size.getWallWidth();
      }
   }
}