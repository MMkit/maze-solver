package maze.ai;

import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

/**
 * Controls the AI and models to move the robot through the maze.
 * @author Luke Last
 */
public class RobotController
{
   private static final int MAX_STEP_COUNT = 1500;
   private final MazeModel mazeModel;
   private final RobotModelMaster robotModelMaster;
   private final RobotModel robotModelClient;
   private final RobotBase ai;
   private int robotStepCount = 0;

   /**
    * Constructor.
    * @param model The maze model to use.
    * @param robotAI The robot AI algorithm to use.
    */
   public RobotController(MazeModel model, RobotBase robotAI)
   {
      this.mazeModel = model;
      this.ai = robotAI;

      final MazeCell start = new MazeCell(1, this.mazeModel.getSize().height);

      this.robotModelMaster = new RobotModelMaster(this.mazeModel, start, Direction.North);

      this.robotModelClient = new RobotModel(this.robotModelMaster);
      this.initialize();
   }

   /**
    * Initializes/resets all values of this controller allowing a new simulation
    * to be run from the beginning.
    */
   public void initialize()
   {
      this.robotModelMaster.setCurrentLocation(new MazeCell(1, this.mazeModel.getSize().height));
      this.robotModelMaster.setDirection(Direction.North);
      this.ai.setRobotLocation(this.robotModelClient);
      this.ai.initialize();
   }

   /**
    * Get the next step the robot has taken.
    */
   public RobotStep nextStep()
   {
      final RobotStep nextStep = this.ai.nextStep();
      //System.out.println(nextStep);

      this.robotModelMaster.takeNextStep(nextStep);

      this.robotStepCount++;
      return nextStep;
   }

   /**
    * Is the robot done moving.
    */
   public boolean isRobotDone()
   {
      return this.robotStepCount > MAX_STEP_COUNT;
   }

   /**
    * Get the total number of steps the robot has taken so far. Turns count as a
    * step.
    */
   public int getStepCount()
   {
      return this.robotStepCount;
   }

   /**
    * Get the robot model being used by this controller.
    */
   public RobotModelMaster getRobotModelMaster()
   {
      return this.robotModelMaster;
   }

   public Direction[][] getUnderstandingDir()
   {
      return ai.getUnderstandingDir();
   }

   public int[][] getUnderstandingInt()
   {
      return ai.getUnderstandingInt();
   }

}
