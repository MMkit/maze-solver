package maze.model;

import java.io.Serializable;
import java.util.Observable;

/**
 * This model stores the sizes of the cells and wall segments that are drawn to
 * the screen.
 */
public final class CellSizeModel extends Observable implements Cloneable, Serializable
{
   private int cellWidth;
   private int cellHeight;
   private int wallWidth;
   private int wallHeight;
   private final boolean forceEven;

   /**
    * Constructor.
    * @param forceEven Force the values stored in this model to be even.
    */
   public CellSizeModel(boolean forceEven)
   {
      this.forceEven = forceEven;
   }

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

   public void setCellWidth(int cellWidth)
   {
      if (this.forceEven && (cellWidth & 1) == 1)
      {
         cellWidth--;
      }
      if (this.cellWidth != cellWidth)
      {
         this.cellWidth = cellWidth;
         super.setChanged();
      }
   }

   public void setCellHeight(int cellHeight)
   {
      if (this.forceEven && (cellHeight & 1) == 1)
      {
         cellHeight--;
      }
      if (this.cellHeight != cellHeight)
      {
         this.cellHeight = cellHeight;
         super.setChanged();
      }
   }

   public void setWallWidth(int wallWidth)
   {
      if (this.forceEven && (wallWidth & 1) == 1)
      {
         wallWidth++;
      }
      if (this.wallWidth != wallWidth)
      {
         this.wallWidth = wallWidth;
         super.setChanged();
      }
   }

   public void setWallHeight(int wallHeight)
   {
      if (this.forceEven && (wallHeight & 1) == 1)
      {
         wallHeight++;
      }
      if (this.wallHeight != wallHeight)
      {
         this.wallHeight = wallHeight;
         super.setChanged();
      }
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

   public int getCellWidthInner()
   {
      return this.cellWidth - this.wallWidth;
   }

   public int getCellHeightInner()
   {
      return this.cellHeight - this.wallHeight;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + cellHeight;
      result = prime * result + cellWidth;
      result = prime * result + wallHeight;
      result = prime * result + wallWidth;
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (! (obj instanceof CellSizeModel))
         return false;
      CellSizeModel other = (CellSizeModel) obj;
      if (cellHeight != other.cellHeight)
         return false;
      if (cellWidth != other.cellWidth)
         return false;
      if (wallHeight != other.wallHeight)
         return false;
      if (wallWidth != other.wallWidth)
         return false;
      return true;
   }

   @Override
   public CellSizeModel clone()
   {
      try
      {
         return (CellSizeModel) super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         throw new RuntimeException(e);
      }
   }

}