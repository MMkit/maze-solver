/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze.gui;

/**
 * This model stores the sizes of the cells and wall segments that are drawn
 * to the screen.
 */
public class CellSize
{

   private int cellWidth = 44;
   private int cellHeight = 50;
   private int wallWidth = 10;
   private int wallHeight = 10;

   public CellSize(int cw, int ch, int ww, int wh)
   {
      cellWidth = cw;
      cellHeight = ch;
      wallWidth = ww;
      wallHeight = wh;
   }

   public int getCellWidth()
   {
      return cellWidth;
   }

   public int getCellHeight()
   {
      return cellHeight;
   }

   public int getWallWidth()
   {
      return wallWidth;
   }

   public int getWallHeight()
   {
      return wallHeight;
   }

   public int getCellWidthHalf()
   {
      return cellWidth / 2;
   }

   public int getCellHeightHalf()
   {
      return cellHeight / 2;
   }

   public int getWallWidthHalf()
   {
      return wallWidth / 2;
   }

   public int getWallHeightHalf()
   {
      return wallHeight / 2;
   }
}
