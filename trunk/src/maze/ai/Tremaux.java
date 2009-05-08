package maze.ai;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;

import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

public class Tremaux extends RobotBase {

	private Direction[][] ballOfString;
	private ArrayList<RobotStep> moveQueue = new ArrayList<RobotStep>();
	private boolean turbo = false;
	public void initialize()
	{
		if ( this.robotLocation == null )
	    {
	       throw new RuntimeException( "The robot location model was not set" );
	    }
		Dimension size = robotLocation.getMazeSize();
		if(ballOfString == null)
		{
			ballOfString = new Direction[(int)size.getWidth()][(int)size.getHeight()];
		}
		for (int i = 0; i < size.getWidth(); i++)
		{
			for (int j = 0; j < size.getHeight(); j++)
			{
				ballOfString[i][j] = Direction.Directionless;
			}
		}
	}
	
    public boolean isInTurboMode()
	{
	   return turbo;
	}

	public RobotStep nextStep() {
		RobotStep next;
		if(getDirection(robotLocation.getCurrentLocation()) == Direction.Directionless)
		{
			setDirection();
		}
		if(moveQueue.isEmpty() == true)
		{
		   if( (robotLocation.isWallRight() == false) 
				   && (getRightNeighborDirection() == Direction.Directionless) )
		   {
			   next = RobotStep.RotateRight;
			   moveQueue.add(RobotStep.MoveForward);
			   turbo = false;
		   }
		   else if( (robotLocation.isWallFront() == false) 
				   && (getFrontNeighborDirection() == Direction.Directionless) )
		   {
			   next = RobotStep.MoveForward;
			   turbo = false;
		   }
		   else if((robotLocation.isWallLeft() == false) 
				   && (getLeftNeighborDirection() == Direction.Directionless))
		   {
			   next = RobotStep.RotateLeft;
			   moveQueue.add(RobotStep.MoveForward);
			   turbo = false;
		   }
		   else //Retrace the steps
		   {
			   turbo = true;
			   if(robotLocation.getDirection() 
					   == getDirection(robotLocation.getCurrentLocation()))
			   {
				   next = RobotStep.MoveForward;
			   }
			   else if(robotLocation.getDirection().getLeft() 
					   == getDirection(robotLocation.getCurrentLocation()))
			   {
				   next = RobotStep.RotateLeft;
				   moveQueue.add(RobotStep.MoveForward);
			   }
			   else if(robotLocation.getDirection().getRight() 
					   == getDirection(robotLocation.getCurrentLocation()))
			   {
				   next = RobotStep.RotateRight;
				   moveQueue.add(RobotStep.MoveForward);
			   }
			   else
			   {
				   next = RobotStep.RotateRight;
				   moveQueue.add(RobotStep.RotateRight);
				   moveQueue.add(RobotStep.MoveForward);
			   }
		   }
		}
		else
		{
		   next = moveQueue.get(0);
		   moveQueue.remove(0);
		}
		return next;
	}
	
	private Direction getLeftNeighborDirection() {
		return getNeighborDirection(robotLocation.getDirection().getLeft());
	}

	private Direction getFrontNeighborDirection() {
		if(robotLocation.getCurrentLocation().getY() == 1)
		{
			return Direction.Directionless;
		}
		return getNeighborDirection(robotLocation.getDirection());
	}

	private Direction getRightNeighborDirection() {
		return getNeighborDirection(robotLocation.getDirection().getRight());
	}
	
	private Direction getNeighborDirection(Direction direction)
	{
		MazeCell here = robotLocation.getCurrentLocation();
		MazeCell there;
		Dimension size = robotLocation.getMazeSize();
		if( (direction == Direction.North ) 
				&& (here.getY() != 1) )
		{
			there = new MazeCell(here.getX(),here.getY()-1);
		}
		else if(( direction == Direction.South )
				&& (here.getY() != size.getHeight()) )
		{
			there = new MazeCell(here.getX(),here.getY()+1);
		}
		else if(( direction == Direction.East )
			&& (here.getX() != size.getWidth()) )
		{
			there = new MazeCell(here.getX()+1,here.getY());
		}
		else if(( direction == Direction.West )
			&& (here.getX() != 1) )
		{
			there = new MazeCell(here.getX()-1,here.getY());
		}
		else
		{
			return Direction.Directionless;
		}
		return getDirection(there);
	}

	private void setDirection() {
		Direction wayBack = robotLocation.getDirection().getOpposite();
		MazeCell here = robotLocation.getCurrentLocation();
		ballOfString[here.getX()-1][here.getY()-1] = wayBack;
	}

	private Direction getDirection(MazeCell currentLocation) {
		return ballOfString[currentLocation.getX()-1][currentLocation.getY()-1];
	}

	public static void main(String args[]) 
	{
		    Runnable runner = new Runnable() {
		      public void run() {

		        Tremaux frenchie = new Tremaux();
		        RobotModelMaster master = new RobotModelMaster(new Maze(),new MazeCell(1,16), Direction.North);
		        RobotModel mouse = new RobotModel(master);
		        frenchie.setRobotLocation(mouse);
		        frenchie.initialize();
		        


		        for( int i =0; i<100; i++) 
		        {

		        	RobotStep j = frenchie.nextStep();
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
