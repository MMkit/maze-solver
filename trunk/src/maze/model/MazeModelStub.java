package maze.model;

import java.awt.Point;

/**
 * This is a temporary maze model stub used for testing the gui stuff before we
 * have a real maze model.
 * @author Luke Last
 */
public class MazeModelStub implements MazeModelReadonly
{

   @Override
   public int getSizeX()
   {
      return 16;
   }

   @Override
   public int getSizeY()
   {
      return 16;
   }

   @Override
   public boolean isWallEast( Point p )
   {
      return true;
   }

   @Override
   public boolean isWallNorth( Point p )
   {
      return true;
   }

   @Override
   public boolean isWallSouth( Point p )
   {
      return true;
   }

   @Override
   public boolean isWallWest( Point p )
   {
      return true;
   }

}
