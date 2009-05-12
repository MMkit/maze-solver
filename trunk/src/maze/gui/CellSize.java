/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze.gui;

/**
 * This model stores the sizes of the cells and wall segments that are drawn
 * to the screen.
 */
public class CellSize implements Comparable<CellSize>
{
   private int[] sizes = {44, 50, 10, 10, 0, 0};

   public CellSize(int cw, int ch, int ww, int wh, int tw, int th)
   {
      sizes[0] = cw;
      sizes[1] = ch;
      sizes[2] = ww;
      sizes[3] = wh;
      sizes[4] = tw;
      sizes[5] = th;
   }

   public int getCellWidth()
   {
      return sizes[0];
   }

   public int getCellHeight()
   {
      return sizes[1];
   }

   public int getWallWidth()
   {
      return sizes[2];
   }

   public int getWallHeight()
   {
      return sizes[3];
   }

   public int getCellWidthHalf()
   {
      return sizes[0] / 2;
   }

   public int getCellHeightHalf()
   {
      return sizes[1] / 2;
   }

   public int getWallWidthHalf()
   {
      return sizes[2] / 2;
   }

   public int getWallHeightHalf()
   {
      return sizes[3] / 2;
   }

   public int getTotalWidth()
   {
      return sizes[4];
   }

   public int getTotalHeight()
   {
      return sizes[5];
   }

   @Override
   public int compareTo(CellSize o)
   {
      for (int i = 0; i < sizes.length; i++)
         if (sizes[i]-o.sizes[i] != 0)
            return sizes[i]-o.sizes[i];
      return 0;
   }
}
