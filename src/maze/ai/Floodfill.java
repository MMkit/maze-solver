package maze.ai;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Vector;

import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

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


    private boolean getNeighborExplored(MazeCell here, Direction direction) {
    	MazeCell neighbor;
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
		MazeCell here = robotLocation.getCurrentLocation();
		int bestDistance = getDistance(here);
		Direction bestDirection = Direction.Directionless;
		
//		System.out.println("Best Distance: " + String.valueOf(bestDistance));
		if(bestDistance > getNeighborDistance(here,robotLocation.getDirection()))
		{
			bestDirection = robotLocation.getDirection();
			bestDistance = getNeighborDistance(here,bestDirection);
		}

		if(bestDistance > getNeighborDistance(here,Direction.North))
		{
			bestDirection = Direction.North;
			bestDistance = getNeighborDistance(here,bestDirection);
		}
		if(bestDistance > getNeighborDistance(here,Direction.East))
		{
			bestDirection = Direction.East;
			bestDistance = getNeighborDistance(here,bestDirection);
		}
		if(bestDistance > getNeighborDistance(here,Direction.West))
		{
			bestDirection = Direction.West;
			bestDistance = getNeighborDistance(here,bestDirection);
		}
		if(bestDistance > getNeighborDistance(here,Direction.South))
		{
			bestDirection = Direction.South;
			bestDistance = getNeighborDistance(here,bestDirection);
		}
		
		if( bestDirection == Direction.Directionless )
		{
			floodfill();
			return getBestDirection();
		}
		else
		{
			return bestDirection;
		}
	}


	private int getNeighborDistance(MazeCell here, Direction direction) {
    	MazeCell neighbor;
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
			return USELESS;
		}

		return getDistance(neighbor);
	}


	private int getDistance(MazeCell here) {
		return distance[here.getX()-1][here.getY()-1];
	}
	
	private void setDistance(MazeCell here, int value) {
		distance[here.getX()-1][here.getY()-1] = value;
	}


	private void floodfill() {
		Dimension size = maze.getSize();
		Vector<MazeCell> queue = new Vector<MazeCell>();
		MazeCell cell;
		int currentDistance;
		boolean speedy;

		for( int i = 1; i <= size.width; i++)
		{
			for( int j = 1; j <= size.height; j++)
			{
				setDistance(new MazeCell(i,j) , USELESS);
			}
		}
		
		if(goal == TO_START)
		{
			cell = new MazeCell(1,size.height);
			setDistance(cell, 0);
			queue.add(cell);
			speedy = false;
		}
		else
		{
			int targetX = size.width/2;
			int targetY = size.height/2;
			cell = new MazeCell(targetX,targetY);
			setDistance(cell, 0);
			queue.add(cell);
			cell = new MazeCell(targetX+1,targetY);
			setDistance(cell, 0);
			queue.add(cell);
			cell = new MazeCell(targetX,targetY+1);
			setDistance(cell, 0);
			queue.add(cell);
			cell = new MazeCell(targetX+1,targetY+1);
			setDistance(cell, 0);
			queue.add(cell);
			if((speedRun == true) && (speedRunCapable == true) )
			{
				speedy = true;
			}
			else
			{
				speedy =false;
			}
		}
		
		while(queue.isEmpty() == false)
		{
			cell = queue.get(0);
			queue.remove(0);
			currentDistance = getDistance(cell);
			
			//Check to see if accessible
			if(maze.getWall(cell,Direction.North).isSet() == false)
			{	//Check to see if it should be added to queue
				if(((currentDistance+1) < getNeighborDistance(cell,Direction.North))
						&& ((speedy == false) || (getNeighborExplored(cell,Direction.North) == true)))
				{
					queue.add(cell.plusY(-1));
					setDistance(cell.plusY(-1),currentDistance+1);
				}
			}

			//Check to see if accessible
			if(maze.getWall(cell,Direction.South).isSet() == false)
			{	//Check to see if it should be added to queue
				if(((currentDistance+1) < getNeighborDistance(cell,Direction.South))
						&& ((speedy == false) || (getNeighborExplored(cell,Direction.South))))
				{
					queue.add(cell.plusY(1));
					setDistance(cell.plusY(1),currentDistance+1);
				}
			}

			//Check to see if accessible
			if(maze.getWall(cell,Direction.West).isSet() == false)
			{	//Check to see if it should be added to queue
				if(((currentDistance+1) < getNeighborDistance(cell,Direction.West))
						&& ((speedy == false) || (getNeighborExplored(cell,Direction.West))))
				{
					queue.add(cell.plusX(-1));
					setDistance(cell.plusX(-1),currentDistance+1);
				}
			}

			//Check to see if accessible
			if(maze.getWall(cell,Direction.East).isSet() == false)
			{	//Check to see if it should be added to queue
				if(((currentDistance+1) < getNeighborDistance(cell,Direction.East))
						&& ((speedy == false) || (getNeighborExplored(cell,Direction.East))))
				{
					queue.add(cell.plusX(1));
					setDistance(cell.plusX(1),currentDistance+1);
				}
			}

		}
	}


	private void blockOutCenter() {
		Dimension size = maze.getSize();
		MazeCell cell1 = new MazeCell(size.width/2,size.height/2);
		MazeCell cell2 = new MazeCell(size.width/2+1,size.height/2);
		MazeCell cell3 = new MazeCell(size.width/2,size.height/2+1);
		MazeCell cell4 = new MazeCell(size.width/2+1,size.height/2+1);
		MazeCell current = robotLocation.getCurrentLocation();
		
		if(cell1.equals(current))
		{
			maze.setWall(cell1.getX(),cell1.getY(),
					Direction.North.getIndex());
			maze.setWall(cell1.getX(),cell1.getY(),
					Direction.West.getIndex());
		}
		if(cell2.equals(current))
		{
			maze.setWall(cell2.getX(),cell2.getY(),
					Direction.North.getIndex());
			maze.setWall(cell2.getX(),cell2.getY(),
					Direction.East.getIndex());
		}
		if(cell3.equals(current))
		{
			maze.setWall(cell3.getX(),cell3.getY(),
					Direction.South.getIndex());
			maze.setWall(cell3.getX(),cell3.getY(),
					Direction.West.getIndex());
		}
		if(cell4.equals(current))
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
	
	
	public static void main(String args[]) 
	{
		    Runnable runner = new Runnable() {
		      public void run() {

		        Floodfill flood = new Floodfill();
		        RobotModelMaster master = new RobotModelMaster(new Maze(),new MazeCell(1,16), Direction.North);
		        RobotModel mouse = new RobotModel(master);
		        flood.setRobotLocation(mouse);
		        flood.initialize();
		        


		        for( int i =0; i<100; i++) 
		        {

		        	RobotStep j = flood.nextStep();
				  mouse.takeNextStep(j);
				  String direction="blank";

				  if(j == RobotStep.RotateLeft)
				  {
					  direction = "Turn Left";
				  }
				  else if(j == RobotStep.RotateRight)
				  {
					  direction = "Turn Right";
				  }
				  else if(j== RobotStep.MoveForward)
				  {
					  direction = "Forward";
				  }
				  else if(j ==  RobotStep.MoveBackward)
				  {
					  direction = "Reverse";
				  }
				  
				  MazeCell locale = mouse.getCurrentLocation();
				  System.out.println("Step " + String.valueOf(i) + ": x= "
					  		+ String.valueOf(locale.getX()) + ", y= "
					  		+ String.valueOf(locale.getY()) + ", dir= "
					  		+ direction);
				}
		      }
		    };
		    EventQueue.invokeLater(runner);
		  }

}
