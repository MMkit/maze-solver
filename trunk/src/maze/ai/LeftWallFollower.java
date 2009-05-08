package maze.ai;

import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

import java.awt.EventQueue;
import java.util.*;

/**
 * This is the interface for the robot AI that is used by the GUI.
 */
public class LeftWallFollower extends RobotBase
{

   private ArrayList<RobotStep> moveQueue = new ArrayList<RobotStep>();

   /**
    * Sets the instance of the robot model to use.
    */
   public void setRobotLocation( RobotModel model )
   {
      this.robotLocation = model;
   }

   /**
    * Called to before any steps are requested of the robot. This can be
    * overridden to do any initialization of the AI.
    */
   public void initialize()
   {
      moveQueue.clear();
	  if ( this.robotLocation == null )
      {
         throw new RuntimeException( "The robot location model was not set" );
      }
   }

   /**
    * This is called by the controller to get the next move that the robot AI
    * will take.
    */
   public RobotStep nextStep()
   {
	   RobotStep next;
	   if(moveQueue.isEmpty() == true)
	   {
		   if(robotLocation.isWallLeft() == false)
		   {
			   next = RobotStep.RotateLeft;
			   moveQueue.add(RobotStep.MoveForward);
		   }
		   else if(robotLocation.isWallFront() == false)
		   {
			   next = RobotStep.MoveForward;
		   }
		   else 
		   {
			   next = RobotStep.RotateRight;
		   }
	   }
	   else
	   {
		   next = moveQueue.get(0);
		   moveQueue.remove(0);
	   }
	   return next;
   }

   /**
    * When the robot is in TURBO mode the controller/GUI has the option of
    * speeding up the animation.
    */
   public boolean isInTurboMode()
   {
      return false;
   }
   public static void main(String args[]) 
   {
	    Runnable runner = new Runnable() {
	      public void run() {

	        LeftWallFollower lefty = new LeftWallFollower();
	        RobotModelMaster master = new RobotModelMaster(new Maze(),new MazeCell(1,16), Direction.North);
	        RobotModel mouse = new RobotModel(master);
	        lefty.setRobotLocation(mouse);
	        lefty.initialize();
	        


	        for( int i =0; i<100; i++) 
	        {

	        	RobotStep j = lefty.nextStep();
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
