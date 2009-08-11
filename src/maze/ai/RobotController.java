package maze.ai;

import javax.swing.JOptionPane;

import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

/**
 * Controls the AI and models to move the robot through the maze.
 * @author Luke Last
 */
public final class RobotController
{
   private static final int MAX_STEP_COUNT = 2000;
   private final MazeModel mazeModel;
   private final RobotModelMaster robotModelMaster;
   private final RobotModel robotModelClient;
   private final RobotBase ai;
   /**
    * A flag that is set true if the robot crashes into a wall.
    */
   private boolean robotCrashed = false;
   /**
    * Stores the total number of moves taken from one cell to the next not
    * counting turns.
    */
   private int robotMoveCount = 0;
   /**
    * Stores the total number of turns taken.
    */
   private int robotTurnCount = 0;

   /**
    * Constructor.
    * @param model The maze model to use.
    * @param robotAI The robot AI algorithm to use.
    */
   public RobotController(MazeModel model, RobotBase robotAI)
   {
      this.mazeModel = model;
      this.ai = robotAI;
      final MazeCell start = MazeCell.valueOf(1, this.mazeModel.getSize().height);
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
      this.robotModelMaster.setCurrentLocation(MazeCell.valueOf(1, this.mazeModel.getSize().height));
      this.robotModelMaster.setDirection(Direction.North);
      this.ai.setRobotLocation(this.robotModelClient);
      this.ai.initialize();
      this.robotCrashed = false;
      this.robotMoveCount = 0;
      this.robotTurnCount = 0;
   }

   /**
    * Get the next step the robot has taken.
    */
   public RobotStep nextStep()
   {
      final RobotStep nextStep = this.ai.nextStep();
      //System.out.println(nextStep);
      try
      {
         this.robotModelMaster.takeNextStep(nextStep);
      }
      catch (maze.model.RobotModelMaster.RobotCrashedException e)
      {
         this.robotCrashed = true;
         e.printStackTrace();
         JOptionPane.showMessageDialog(maze.Main.getPrimaryFrameInstance(), e.getMessage());
      }
      if (nextStep.isTurn())
         this.robotTurnCount++;
      else
         this.robotMoveCount++;

      return nextStep;
   }

   /**
    * Is the robot done moving.
    */
   public boolean isRobotDone()
   {
      return this.getStepCount() > MAX_STEP_COUNT || this.robotCrashed;
   }

   /**
    * Get the total number of steps the robot has taken so far. Turns count as a
    * step.
    */
   public int getStepCount()
   {
      return this.robotMoveCount + this.robotTurnCount;
   }

   /**
    * Get the total number of moves from cell to cell taken by the robot so far.
    * Turns not counted.
    * @return Total moves.
    */
   public int getRobotMoveCount()
   {
      return robotMoveCount;
   }

   /**
    * Get the total number of turns taken by the robot.
    * @return Total turns taken.
    */
   public int getRobotTurnCount()
   {
      return robotTurnCount;
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
