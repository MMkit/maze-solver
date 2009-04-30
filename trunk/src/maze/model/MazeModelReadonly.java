package maze.model;

import java.awt.Dimension;

/**
 * A read only interface for the maze model. This is useful to use when you
 * don't want a section of code to modify the maze model.
 */
public interface MazeModelReadonly
{
   /**
    * The maze model size defined by the number of cells/boxes/squares in each
    * dimension.
    * @return The size of the maze model.
    */
   public Dimension getSize();

   /**
    * Checks to see if a wall exists at the given location.
    * @param cell The maze cell we are interested in.
    * @param wall The wall segment of the given cell we are interested in.
    * @return true if a wall segment exists.
    */
   public boolean isWall( MazeCell cell, WallDirection wall );

   public boolean isPegLegal( MazeCell cell, PegLocation peg );
}
