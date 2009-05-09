package maze.gui.mazeeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

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
   protected TemplatePeg mCenter = null;
   protected Point mCenterPoint = new Point(0,0);
   protected ImageIcon mIcon = null;
   protected String mDesc = "";

   public TemplatePeg getCenterPeg()
   {
      return mCenter;
   }
   public Point getCenterPoint()
   {
      return mCenterPoint;
   }
   public void updatePosition(Point p)
   {
      mCenterPoint = (Point)p.clone();
   }
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
}
