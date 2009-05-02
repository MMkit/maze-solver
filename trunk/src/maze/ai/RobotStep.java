package maze.ai;

/**
 * This represents one step taken by a robot.
 * @author Luke Last
 */
public enum RobotStep
{
   /**
    * Move forward one cell.
    */
   MoveForward,
   /**
    * Move back one cell.
    */
   MoveBackward,
   /**
    * Rotates the robot left.
    */
   RotateLeft,
   /**
    * Rotates the robot to the right.
    */
   RotateRight,
}
