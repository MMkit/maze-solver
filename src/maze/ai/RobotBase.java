package maze.ai;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;

import maze.model.RobotModel;
import maze.model.Direction;

/**
 * This is the interface for the robot AI that is used by the GUI.
 */
public abstract class RobotBase
{
   protected RobotModel robotLocation;
   protected boolean speedRun = false;

   private static MutableComboBoxModel robotListModel;

   /**
    * Gets a singleton instance of a robot list model. This contains a global
    * list of all the AI algorithms available to the system.
    */
   public static MutableComboBoxModel getRobotListModel()
   {
      // Initialize if necessary.
      if (robotListModel == null)
      {
         robotListModel = new DefaultComboBoxModel(new RobotBase[]
         {
            new LeftWallFollower(), new RightWallFollower(), new Tremaux(),
            new Floodfill()//, new ModifiedFloodfill()
         });
      }
      return robotListModel;
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
      if (this.robotLocation == null)
      {
         throw new RuntimeException("The robot location model was not set");
      }
   }

   /**
    * This is called by the controller to get the next move that the robot AI
    * will take.
    */
   public abstract RobotStep nextStep();

   /**
    * When the robot is in TURBO mode the controller/GUI has the option of
    * speeding up the animation.
    */
   public boolean isInTurboMode()
   {
      return false;
   }

   public void setSpeedRun(boolean choice)
   {
      speedRun = choice;
   }
   
   public Direction[][] getUnderstandingDir(){
	   //This returns the cell view taken by the algorithm if it is Direction-based
	   return null;
   }
   
   public int[][] getUnderstandingInt(){
	   //This returns the cell view taken by the algorithm if it is int-based
	   return null;
   }
}
