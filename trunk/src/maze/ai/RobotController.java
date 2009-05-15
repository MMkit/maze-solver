package maze.ai;

import java.util.List;
import java.util.Set;

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
   
	public Set<MazeCell> getAllUnexplored(){
		return robotModelMaster.getNonHistory();
	}

	public List<MazeCell> getFirstRun() {
		return robotModelMaster.getFirstRun();
	}
	
	public List<MazeCell> getBestRun() {
		return robotModelMaster.getBestRun();
	}
	
	public List<MazeCell> getCurrentRun() {
		return robotModelMaster.getCurrentRun();
	}
	
	public Direction[][] getUnderstandingDir(){
		return ai.getUnderstandingDir();
	}
	
	public int[][] getUnderstandingInt(){
		return ai.getUnderstandingInt();
	}
	

}
