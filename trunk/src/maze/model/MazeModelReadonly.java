package maze.model;

import java.awt.Dimension;

/**
 * A read only interface for the maze model. This is useful to use when you
 * don't want a section of code to modify the maze model.
 */
public interface MazeModelReadonly
{
   public Dimension getSize();

   /**
    * Checks to see if a wall exists at the given location.
    * @param cell The maze cell we are interested in.
    * @param wall The wall segment of the given cell we are interested in.
    * @return true if a wall segment exists.
    */
   public boolean isWall( MazeCell cell, WallDirection wall );
}
