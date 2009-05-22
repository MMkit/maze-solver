package maze.model;

public final class CellSizeModel2 implements CellSizeModelInterface
{
   private int cellWidth;
   private int cellHeight;
   private int wallWidth;
   private int wallHeight;

   @Override
   public String toString()
   {
      return "Cell[w=" +
             this.cellWidth +
             ",h=" +
             this.cellHeight +
             "] Wall[w=" +
             this.wallWidth +
             ",h=" +
             this.wallHeight +
             "]";
   }

   /**
    * @return the cellWidth
    */
   public int getCellWidth()
   {
      return this.cellWidth;
   }

   /**
    * @param cellWidth the cellWidth to set
    */
   public void setCellWidth(int cellWidth)
   {
      this.cellWidth = cellWidth;
   }

   /**
    * @return the cellHeight
    */
   public int getCellHeight()
   {
      return this.cellHeight;
   }

   /**
    * @param cellHeight the cellHeight to set
    */
   public void setCellHeight(int cellHeight)
   {
      this.cellHeight = cellHeight;
   }

   /**
    * @return the wallWidth
    */
   public int getWallWidth()
   {
      return this.wallWidth;
   }

   /**
    * @param wallWidth the wallWidth to set
    */
   public void setWallWidth(int wallWidth)
   {
      this.wallWidth = wallWidth;
   }

   /**
    * @return the wallHeight
    */
   public int getWallHeight()
   {
      return (int) this.wallHeight;
   }

   /**
    * @param wallHeight the wallHeight to set
    */
   public void setWallHeight(int wallHeight)
   {
      this.wallHeight = wallHeight;
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

   public int getCellWidthInner()
   {
      return this.cellWidth - this.wallWidth;
   }

   public int getCellHeightInner()
   {
      return this.cellHeight - this.wallHeight;
   }
}
