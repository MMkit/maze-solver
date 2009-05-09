package maze.ai;

import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModelMaster;
import maze.model.RobotModel;

/**
 * Controls the AI and models to move the robot through the maze.
 * @author Luke Last
 */
public class RobotController
{
   private final MazeModel mazeModel;
   private final RobotModelMaster robotModelMaster;
   private final RobotModel robotModelClient;
   private final RobotBase ai;
   private int robotStepCount = 0;

   public RobotModelMaster getRobotModelMaster()
   {
      return this.robotModelMaster;
   }

   public RobotController(MazeModel model, RobotBase robotAI)
   {
      this.mazeModel = model;
      this.ai = robotAI;

      this.robotModelMaster = new RobotModelMaster(this.mazeModel, new MazeCell(1, 1), Direction.South);

      this.robotModelClient = new RobotModel(this.robotModelMaster);

      this.ai.setRobotLocation(this.robotModelClient);
      this.ai.initialize();
   }

   public RobotStep nextStep()
   {
      final RobotStep nextStep = this.ai.nextStep();
      //System.out.println(nextStep);

      this.robotModelMaster.takeNextStep(nextStep);

      this.robotStepCount++;
      return nextStep;
   }

   public boolean isRobotDone()
   {
      return this.robotStepCount > 1000;
   }
}
