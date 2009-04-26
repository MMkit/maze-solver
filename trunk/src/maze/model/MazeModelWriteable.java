package maze.model;

import java.awt.Point;

public interface MazeModelWriteable extends MazeModelReadonly
{
   public void setSizeX( int x );

   public void setSizeY( int y );

   public void setWallNorth( Point p );

   public void setWallSouth( Point p );

   public void setWallEast( Point p );

   public void setWallWest( Point p );

}
