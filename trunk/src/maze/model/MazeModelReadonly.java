package maze.model;

import java.awt.Point;

/**
 * A read only interface for the maze model. This is useful to use when you
 * don't want a section of code to modify the maze model.
 */
public interface MazeModelReadonly
{
   public int getSizeX();

   public int getSizeY();

   public boolean isWallNorth( Point p );

   public boolean isWallSouth( Point p );

   public boolean isWallEast( Point p );

   public boolean isWallWest( Point p );
}
