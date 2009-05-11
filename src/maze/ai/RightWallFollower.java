package maze.ai;

import java.util.ArrayList;
import java.util.List;

import maze.model.RobotModel;


public class RightWallFollower extends RobotBase
{
   private List<RobotStep> moveQueue = new ArrayList<RobotStep>();

   @Override
   public String toString()
   {
      return "Right Wall Follower";
   }

   /**
    * Sets the instance of the robot model to use.
    */
   public void setRobotLocation(RobotModel model)
   {
      this.robotLocation = model;
   }

   /**
    * Called to before any steps are requested of the robot. This can be
    * overridden to do any initialization of the AI.
    */
   public void initialize()
   {
      super.initialize();
      moveQueue.clear();
   }

   /**
    * This is called by the controller to get the next move that the robot AI
    * will take.
    */
   public RobotStep nextStep()
   {
      RobotStep next;
      if (moveQueue.isEmpty() == true)
      {
         if (robotLocation.isWallRight() == false)
         {
            next = RobotStep.RotateRight;
            moveQueue.add(RobotStep.MoveForward);
         }
         else if (robotLocation.isWallFront() == false)
         {
            next = RobotStep.MoveForward;
         }
         else
         {
            next = RobotStep.RotateLeft;
         }
      }
      else
      {
         next = moveQueue.get(0);
         moveQueue.remove(0);
      }
      return next;
   }
}
