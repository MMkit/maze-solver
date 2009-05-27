package maze.gui;

import maze.ai.RobotBase;
import maze.ai.RobotController;
import maze.model.MazeModel;
import maze.model.RobotPathModel;

/**
 * Provides a means of generating statistics on a robots path through a maze.
 * @author Vincent Frey
 */
public class StatTracker
{
   private int firstRunSquaresTraversed;
   private int firstRunTurnsTaken;
   private int bestRunSquaresTraversed;
   private int bestRunTurnsTaken;
   private int bestRunTotalSquaresTraversed;
   private int bestRunTotalTurnsTaken;
   private int previousRunTotalSquaresTraversed;
   private int previousRunTotalTurnsTaken;
   private int currentRunSquaresTraversed;
   private int currentRunTurnsTaken;

   /**
    * This robot controller is used to simulate the robots path through the
    * maze.
    */
   private RobotController controller;

   /**
    * This constructor requires an algorithm and a mouse. It will then determine
    * a handful of relevant statistics for the user to access
    */
   public StatTracker(RobotBase algorithm, MazeModel maze)
   {
      this.reload(algorithm, maze);
   }

   /**
    * This function requires an algorithm and a mouse. It will then determine a
    * handful of relevant statistics for the user to access
    */
   public void reload(RobotBase algorithm, MazeModel maze)
   {
      this.controller = new RobotController(maze, algorithm);
      this.initialize();
      this.recompute();
   }

   /**
    * This function prepares the mouse and algorithm to get ready to start a
    * run.
    */
   private void initialize()
   {
      this.controller.initialize();
      previousRunTotalSquaresTraversed = 0;
      previousRunTotalTurnsTaken = 0;
   }

   /**
    * This function simulates a run through the maze for the mouse and
    * algorithm.
    */
   private void recompute()
   {
      trackARun();

      if (this.controller.isRobotDone())
      {
         if (currentRunSquaresTraversed < this.controller.getRobotMoveCount())
         { // Just in case the mouse makes it to the center but not back.
            firstRunSquaresTraversed = currentRunSquaresTraversed;
            firstRunTurnsTaken = currentRunTurnsTaken;
            bestRunSquaresTraversed = currentRunSquaresTraversed;
            bestRunTurnsTaken = currentRunTurnsTaken;
            bestRunTotalSquaresTraversed = currentRunSquaresTraversed;
            bestRunTotalTurnsTaken = currentRunTurnsTaken;
         }
         else
         { // The first run failed to find the center.
            this.firstRunSquaresTraversed = 0;
         }
         return;
      }

      firstRunSquaresTraversed = currentRunSquaresTraversed;
      firstRunTurnsTaken = currentRunTurnsTaken;

      do
      {
         bestRunSquaresTraversed = currentRunSquaresTraversed;
         bestRunTurnsTaken = currentRunTurnsTaken;
         bestRunTotalSquaresTraversed = previousRunTotalSquaresTraversed +
                                        currentRunSquaresTraversed;
         bestRunTotalTurnsTaken = previousRunTotalTurnsTaken + currentRunTurnsTaken;
         previousRunTotalSquaresTraversed = this.controller.getRobotMoveCount();
         previousRunTotalTurnsTaken = this.controller.getRobotTurnCount();
         trackARun();
      }
      while (bestRunSquaresTraversed > currentRunSquaresTraversed && !this.controller.isRobotDone());
   }

   /**
    * Track a run.
    */
   private void trackARun()
   {
      currentRunSquaresTraversed = 0;
      currentRunTurnsTaken = 0;

      while (!this.controller.isRobotDone() && !this.controller.getRobotModelMaster().isAtCenter())
      {
         if (this.controller.nextStep().isTurn())
         {
            currentRunTurnsTaken++;
         }
         else
         {
            currentRunSquaresTraversed++;
         }
      }

      while (!this.controller.isRobotDone() && !this.controller.getRobotModelMaster().isAtStart())
      {
         this.controller.nextStep();
      }
   }

   /**
    * Did the robot find the center box at least once or did it fail to win at
    * all.
    * @return true if the robot made it to the center at least once.
    */
   public boolean wasCenterFound()
   {
      return this.firstRunSquaresTraversed != 0;
   }

   public int getTotalTraversed()
   {
      return this.controller.getRobotModelMaster().getRobotPathModel().getCellsVisited();
   }

   public int getFirstRunCells()
   {
      return firstRunSquaresTraversed;
   }

   public int getFirstRunTurns()
   {
      return firstRunTurnsTaken;
   }

   public int getBestRunCells()
   {
      return bestRunSquaresTraversed;
   }

   public int getBestRunTurns()
   {
      return bestRunTurnsTaken;
   }

   public int getThroughBestRunCells()
   {
      return bestRunTotalSquaresTraversed;
   }

   public int getThroughBestRunTurns()
   {
      return bestRunTotalTurnsTaken;
   }

   public RobotPathModel getRobotPathModel()
   {
      return this.controller.getRobotModelMaster().getRobotPathModel();
   }
}