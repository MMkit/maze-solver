package maze.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Luke Last
 */
public class RobotModel
{
   /**
     *
     */
   private final MazeModel mazeModel;
   /**
    * The current location of the robot.
    */
   private MazeCell currentLocation;
   /**
    * The direction the robot is currently facing.
    */
   private Direction direction = Direction.North;
   /**
    * The list of cells that the
    */
   private final List<MazeCell> pathTaken = new ArrayList<MazeCell>();
   /**
    * All the maze cells that have already been visited.
    */
   private final Set<MazeCell> history = new TreeSet<MazeCell>();

   public RobotModel( MazeModel mazeModel, MazeCell currentLocation )
   {
      this.mazeModel = mazeModel;
      this.currentLocation = currentLocation;
   }

   public boolean isWallFront()
   {
      return this.mazeModel.getWall( this.currentLocation, this.direction ).isSet();
   }

   public boolean isWallBack()
   {
      return this.mazeModel.getWall( this.currentLocation, this.direction.getOpposite() ).isSet();
   }

   public boolean isWallLeft()
   {
      return this.mazeModel.getWall( this.currentLocation, this.direction.getLeft() ).isSet();
   }

   public boolean isWallRight()
   {
      return this.mazeModel.getWall( this.currentLocation, this.direction.getRight() ).isSet();
   }

   public MazeCell getCurrentLocation()
   {
      return currentLocation;
   }

   public Direction getDirection()
   {
      return direction;
   }

   public Collection<MazeCell> getHistory()
   {
      return history;
   }

   public MazeModel getMazeModel()
   {
      return mazeModel;
   }

   public List<MazeCell> getPathTaken()
   {
      return pathTaken;
   }

}
