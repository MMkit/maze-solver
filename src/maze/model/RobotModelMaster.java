package maze.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


import maze.ai.RobotStep;

/**
 * @author Luke Last
 */
public class RobotModelMaster
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

   public RobotModelMaster( MazeModel mazeModel, MazeCell currentLocation, Direction direction )
   {
      this.mazeModel = mazeModel;
      this.currentLocation = currentLocation;
      this.direction = direction;
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

   public Dimension getMazeSize()
   {
	   return this.mazeModel.getSize();
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

   public void takeNextStep(RobotStep nextStep) {
		  if(nextStep == RobotStep.RotateLeft)
		  {
			  direction = direction.getLeft();
		  }
		  else if(nextStep == RobotStep.RotateRight)
		  {
			  direction = direction.getRight();
		  }
		  else if(nextStep == RobotStep.MoveForward)
		  {
			  if( this.isWallFront() == true)
			  {
				  throw new RuntimeException( "The mouse just crashed into a wall" );
			  }
			  else
			  {
				  if(direction == Direction.North)
				  {
					  currentLocation = new MazeCell(currentLocation.getX(),currentLocation.getY() - 1);
				  }
				  else if(direction == Direction.South)
				  {
					  currentLocation = new MazeCell(currentLocation.getX(),currentLocation.getY() + 1);
				  }
				  else if(direction == Direction.East)
				  {
					  currentLocation = new MazeCell(currentLocation.getX() + 1,currentLocation.getY());
				  }
				  else if(direction == Direction.West)
				  {
					  currentLocation = new MazeCell(currentLocation.getX() - 1,currentLocation.getY());
				  }
				  pathTaken.add(currentLocation);
				  history.add(currentLocation);
			  }
		  }
		  else if(nextStep ==  RobotStep.MoveBackward)
		  {
			  if( this.isWallBack() == true)
			  {
				  throw new RuntimeException( "The mouse just crashed into a wall" );
			  }
			  else
			  {
				  if(direction == Direction.North)
				  {
					  currentLocation = new MazeCell(currentLocation.getX(),currentLocation.getY() + 1);
				  }
				  else if(direction == Direction.South)
				  {
					  currentLocation = new MazeCell(currentLocation.getX(),currentLocation.getY() - 1);
				  }
				  else if(direction == Direction.East)
				  {
					  currentLocation = new MazeCell(currentLocation.getX() - 1,currentLocation.getY());
				  }
				  else if(direction == Direction.West)
				  {
					  currentLocation = new MazeCell(currentLocation.getX() + 1,currentLocation.getY());
				  }
				  pathTaken.add(currentLocation);
				  history.add(currentLocation);
			  }
		  }

	
   }

}
