package maze.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luke Last
 */
public class RobotModelClient
{

   private final RobotModel parent;

   public RobotModelClient( RobotModel parent )
   {
      this.parent = parent;
   }

   public boolean isWallFront()
   {
      return this.parent.isWallFront();
   }

   public boolean isWallBack()
   {
      return this.parent.isWallBack();
   }

   public boolean isWallLeft()
   {
      return this.parent.isWallLeft();
   }

   public boolean isWallRight()
   {
      return this.parent.isWallRight();
   }

   public MazeCell getCurrentLocation()
   {
      return this.parent.getCurrentLocation();
   }

   public Direction getDirection()
   {
      return this.parent.getDirection();
   }

   public List<MazeCell> getHistory()
   {
      return new ArrayList<MazeCell>( this.parent.getHistory() );
   }

   public List<MazeCell> getPathTaken()
   {
      return new ArrayList<MazeCell>( this.parent.getPathTaken() );
   }
}
