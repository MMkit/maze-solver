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
   private int[] sizes = {44, 50, 10, 10};
   private int cellWidth = 44;
   private int cellHeight = 50;
   private int wallWidth = 10;
   private int wallHeight = 10;

   public CellSize(int cw, int ch, int ww, int wh)
   {
      sizes[0] = cw;
      sizes[1] = ch;
      sizes[2] = ww;
      sizes[3] = wh;
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

   @Override
   public int compareTo(CellSize o)
   {
      for (int i = 0; i < 4; i++)
         if (sizes[i]-o.sizes[i] != 0)
            return sizes[i]-o.sizes[i];
      return 0;
   }
}
