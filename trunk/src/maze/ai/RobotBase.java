package maze.ai;

import maze.model.RobotModelClient;

/**
 * This is the interface for the robot AI that is used by the GUI.
 */
public abstract class RobotBase
{

   protected RobotModelClient robotLocation;

   /**
    * Sets the instance of the robot model to use.
    */
   public void setRobotLocation( RobotModelClient model )
   {
      this.robotLocation = model;
   }

   /**
    * Called to before any steps are requested of the robot. This can be
    * overridden to do any initialization of the AI.
    */
   public void initialize()
   {
      if ( this.robotLocation == null )
      {
         throw new RuntimeException( "The robot location model was not set" );
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
}
