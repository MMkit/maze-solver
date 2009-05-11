package maze.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the interface for the robot AI that is used by the GUI.
 */
public class LeftWallFollower extends RobotBase
{
   private List<RobotStep> moveQueue = new ArrayList<RobotStep>();

   @Override
   public String toString()
   {
      return "Left Wall Follower";
   }

   /**
    * Called to before any steps are requested of the robot. This can be
    * overridden to do any initialization of the AI.
    */
   @Override
   public void initialize()
   {
      super.initialize();
      moveQueue.clear();
   }

   /**
    * This is called by the controller to get the next move that the robot AI
    * will take.
    */
   @Override
   public RobotStep nextStep()
   {
      RobotStep next;
      if (moveQueue.isEmpty() == true)
      {
         if (robotLocation.isWallLeft() == false)
         {
            next = RobotStep.RotateLeft;
            moveQueue.add(RobotStep.MoveForward);
         }
         else if (robotLocation.isWallFront() == false)
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
}