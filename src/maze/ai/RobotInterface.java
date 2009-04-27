package maze.ai;

/**
 * This is the interface for the robot AI that is used by the GUI.
 */
public interface RobotInterface
{
   /**
    * This is called by the controller to get the next move that the robot AI
    * will take.
    */
   public RobotStep nextStep();

   /**
    * When the robot is in TURBO mode the controller/GUI has the option of
    * speeding up the animation.
    */
   public boolean isInTurboMode();
}
