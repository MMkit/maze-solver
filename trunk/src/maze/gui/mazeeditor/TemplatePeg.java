/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

/**
 *
 * @author desolc
 */
public class TemplatePeg
{
   public TemplateWall mTop = null;
   public TemplateWall mBottom = null;
   public TemplateWall mLeft = null;
   public TemplateWall mRight = null;

   @Override
   protected Object clone() throws CloneNotSupportedException
   {
      TemplatePeg cloned = new TemplatePeg();
      cloned.mTop = mTop;
      cloned.mBottom = mBottom;
      cloned.mLeft = mLeft;
      cloned.mRight = mRight;
      return cloned;
   }

}
