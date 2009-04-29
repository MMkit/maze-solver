package maze.model;

import java.awt.Dimension;

public interface MazeModelWriteable extends MazeModelReadonly
{
   public void setSize( Dimension size );

   public void clearWall( MazeCell cell, WallDirection wall );

   public void enableWall( MazeCell cell, WallDirection wall );

}
