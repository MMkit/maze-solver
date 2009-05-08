package maze.ai;

import java.awt.Dimension;
import java.util.ArrayList;

import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;

public class Floodfill extends RobotBase {

	private int[][] distance;
	private boolean[][] explored;
	private ArrayList<RobotStep> moveQueue = new ArrayList<RobotStep>();
	private boolean turbo = false;
	private static final int USELESS = 1024;
	private Maze maze = new Maze();
	private boolean goal;
	private static final boolean TO_CENTER = true;
	private static final boolean TO_START = false;
	private boolean speedRunCapable;
	
   public void initialize()
   {
      if ( this.robotLocation == null )
      {
         throw new RuntimeException( "The robot location model was not set" );
      }
      moveQueue.clear();
	   // TODO if ever we make mazes vary in size then here must be updated
      //maze.setSize(robotLocation.getMazeSize());
      maze.clearMaze();
		Dimension size = maze.getSize();
		if(distance == null)
		{
			distance = new int[size.width][size.height];
			explored = new boolean[size.width][size.height];
		}
		for (int i = 0; i < size.getWidth(); i++)
		{
			for (int j = 0; j < size.getHeight(); j++)
			{
				distance[i][j] = USELESS;
				explored[i][j] = false;
			}
		}
		goal = TO_CENTER;
		speedRunCapable = false;
   }


	public RobotStep nextStep() {
		RobotStep next;
		Direction nextDirection;
		Direction currentDirection = robotLocation.getDirection();
		if(moveQueue.isEmpty())
		{
			if(getExplored() == false)
			{
				checkWalls();
				setExplored();
			}
			if(atGoal() == true)
			{
				if( (goal == TO_CENTER) && (speedRunCapable == false) )
				{
					speedRunCapable = true;
					blockOutCenter();
				}
				goal = !goal;
				floodfill();
			}
			nextDirection = getBestDirection();
			turbo = getNeighborExplored(nextDirection);
			if(nextDirection == currentDirection)
			{
				next = RobotStep.MoveForward;
			}
			else if(nextDirection == currentDirection.getLeft())
			{
				next = RobotStep.RotateLeft;
				moveQueue.add(RobotStep.MoveForward);
			}
			else if(nextDirection == currentDirection.getRight())
			{
				next = RobotStep.RotateRight;
				moveQueue.add(RobotStep.MoveForward);
			}
			else //if(nextDirection == currentDirection.getOpposite())
			{
				next = RobotStep.MoveBackward;
			}
		}
		else
		{
			next = moveQueue.get(0);
			moveQueue.remove(0);
		}
		return next;
	}

    private boolean getNeighborExplored(Direction direction) {
    	MazeCell neighbor;
		MazeCell here = robotLocation.getCurrentLocation();
		Dimension size = maze.getSize();
		if( (direction == Direction.North ) 
				&& (here.getY() != 1) )
		{
			neighbor = new MazeCell(here.getX(),here.getY()-1);
		}
		else if(( direction == Direction.South )
				&& (here.getY() != size.getHeight()) )
		{
			neighbor = new MazeCell(here.getX(),here.getY()+1);
		}
		else if(( direction == Direction.East )
			&& (here.getX() != size.getWidth()) )
		{
			neighbor = new MazeCell(here.getX()+1,here.getY());
		}
		else if(( direction == Direction.West )
			&& (here.getX() != 1) )
		{
			neighbor = new MazeCell(here.getX()-1,here.getY());
		}
		else
		{
			return false;
		}

		return getExplored(neighbor);
	}


	private boolean getExplored(MazeCell cell) {
		return explored[cell.getX()-1][cell.getY()-1];
	}


	private boolean getExplored() {
		return getExplored(robotLocation.getCurrentLocation());
	}


	private void setExplored() {
		explored[robotLocation.getCurrentLocation().getX()-1]
		         [robotLocation.getCurrentLocation().getY()-1] = true;
	}


	private Direction getBestDirection() {
		// TODO Auto-generated method stub
		return null;
	}


	private void floodfill() {
		// TODO Auto-generated method stub
		
	}


	private void blockOutCenter() {
		Dimension size = maze.getSize();
		MazeCell cell1 = new MazeCell(size.width/2,size.height/2);
		MazeCell cell2 = new MazeCell(size.width/2+1,size.height/2);
		MazeCell cell3 = new MazeCell(size.width/2,size.height/2+1);
		MazeCell cell4 = new MazeCell(size.width/2+1,size.height/2+1);
		MazeCell current = robotLocation.getCurrentLocation();
		
		if(cell1 != current)
		{
			maze.setWall(cell1.getX(),cell1.getY(),
					Direction.North.getIndex());
			maze.setWall(cell1.getX(),cell1.getY(),
					Direction.West.getIndex());
		}
		if(cell2 != current)
		{
			maze.setWall(cell2.getX(),cell2.getY(),
					Direction.North.getIndex());
			maze.setWall(cell2.getX(),cell2.getY(),
					Direction.East.getIndex());
		}
		if(cell3 != current)
		{
			maze.setWall(cell3.getX(),cell3.getY(),
					Direction.South.getIndex());
			maze.setWall(cell3.getX(),cell3.getY(),
					Direction.West.getIndex());
		}
		if(cell4 != current)
		{
			maze.setWall(cell4.getX(),cell4.getY(),
					Direction.South.getIndex());
			maze.setWall(cell4.getX(),cell4.getY(),
					Direction.East.getIndex());
		}
	}


	private boolean atGoal() {
		MazeCell cell = robotLocation.getCurrentLocation();
		Dimension size = maze.getSize();
		if( (goal == TO_START) && (cell.getX() == 1)
				&& (cell.getY() == size.height))
		{
			return true;
		}
		if( (goal == TO_CENTER) 
			&& ((cell.getY()==size.height/2)||(cell.getY()==(size.height/2+1)))
			&& ((cell.getX()==size.width/2)||(cell.getX()==(size.width/2+1))))
		{
			return true;
		}
		return false;
	}


	private void checkWalls() {
		MazeCell cell = robotLocation.getCurrentLocation();
		Direction direction = robotLocation.getDirection();
		if(robotLocation.isWallFront())
		{
			maze.setWall(cell.getX(),cell.getY(),
					direction.getIndex());
		}
		if(robotLocation.isWallLeft())
		{
			maze.setWall(cell.getX(),cell.getY(),
					direction.getLeft().getIndex());
		}
		if(robotLocation.isWallRight())
		{
			maze.setWall(cell.getX(),cell.getY(),
					direction.getRight().getIndex());
		}
		//Shouldn't need to set the wall behind because you had to go thru it
	}


	public boolean isInTurboMode()
    {
    	return turbo;
    }
	
}
