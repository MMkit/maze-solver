package maze.ai;

import maze.model.RobotModel;

/**
 * This is the interface for the robot AI that is used by the GUI.
 */
public abstract class RobotBase
{
   protected RobotModel robotLocation;
   protected boolean speedRun = false;
   public static final RobotBase[] MASTER_AI_LIST = new RobotBase[]
   {
      new LeftWallFollower(), new RightWallFollower(), new Tremaux(), new Floodfill(),
      new PythonScriptRobot(),
   };

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
}
