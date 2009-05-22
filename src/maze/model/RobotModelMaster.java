package maze.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import maze.ai.RobotStep;

/**
 * Holds data and provides information about the robots journey through the
 * maze. This class holds an instance of a MazeModel and provides indirect
 * access to it for the AI algorithms.
 * @author Luke Last
 */
public class RobotModelMaster
{
   /**
    * The maze model that backs this robot model.
    */
   private final MazeModel mazeModel;
   /**
    * The current location of the robot.
    */
   private MazeCell currentLocation;
   /**
    * The direction the robot is currently facing.
    */
   private Direction direction = Direction.North;
   /**
    * 
    */
   private final RobotPathModel robotPathModel = new RobotPathModel();
   /**
    * The list of cells that the
    */
   private final List<MazeCell> pathTaken = new ArrayList<MazeCell>();


   /**
    * All the maze cells that have already been visited.
    */
   private final Set<MazeCell> history = new HashSet<MazeCell>(128);

   /**
    * Sole constructor.
    * @param mazeModel The maze model that will back this robot model. Can only
    *           be set once here.
    * @param currentLocation The starting location of the robot.
    * @param direction The starting direction of the robot.
    */
   public RobotModelMaster(MazeModel mazeModel, MazeCell currentLocation, Direction direction)
   {
      if (mazeModel == null)
         throw new IllegalArgumentException("MazeModel cannot be null");

      this.mazeModel = mazeModel;
      this.currentLocation = currentLocation;
      this.direction = direction;

      MazeCell startCell = new MazeCell(1, mazeModel.getSize().height);

      pathTaken.add(startCell);
      history.add(startCell);
      this.robotPathModel.addLocation(startCell);
   }

   public boolean isWall(Direction direction)
   {
      return this.mazeModel.getWall(this.currentLocation, direction).isSet();
   }

   public Dimension getMazeSize()
   {
      return this.mazeModel.getSize();
   }

   public MazeCell getCurrentLocation()
   {
      return currentLocation;
   }

   public void setCurrentLocation(MazeCell location)
   {
      this.currentLocation = location;
   }

   public Direction getDirection()
   {
      return direction;
   }

   public void setDirection(Direction direction)
   {
      this.direction = direction;
   }

   public Set<MazeCell> getHistory()
   {
      return history;
   }

   public MazeModel getMazeModel()
   {
      return mazeModel;
   }

   public List<MazeCell> getPathTaken()
   {
      return pathTaken;
   }

   /**
    * Attempts to move the robot with the given step.
    * @param nextStep
    */
   public void takeNextStep(final RobotStep nextStep)
   {
      Direction moveDirection = null;
      switch (nextStep)
      {
         case RotateLeft :
            this.direction = direction.getLeft();
            break;
         case RotateRight :
            this.direction = direction.getRight();
            break;
         case MoveForward :
            moveDirection = this.direction;
            break;
         case MoveBackward :
            moveDirection = this.direction.getOpposite();
            break;
         default :
            throw new IllegalArgumentException("Invalid step: " + nextStep);
      }

      if (moveDirection != null)
      {
         if (this.isWall(moveDirection))
         {
            throw new RuntimeException("The mouse just crashed into a wall");
         }
         else
         {
            this.currentLocation = this.currentLocation.neighbor(moveDirection);
            this.robotPathModel.addLocation(this.currentLocation);
            pathTaken.add(currentLocation);
            history.add(currentLocation);
         }
      }

      // Are we in a winning cell?
      if (this.isAtCenter())
      {
         if (this.robotPathModel.firstPath.isEmpty())
         {
            for (int i = 0; i < pathTaken.size(); i++)
            {
               this.robotPathModel.firstPath.add(pathTaken.get(i));
               this.robotPathModel.bestPath.add(pathTaken.get(i));
            }
         }
         else
         { // First run was not empty.
            MazeCell startCell = new MazeCell(1, mazeModel.getSize().height);
            if ( (this.robotPathModel.bestPath.size()) > (pathTaken.size() - (pathTaken.lastIndexOf(startCell) + 1)))
            {
               this.robotPathModel.bestPath.clear();
               for (int i = pathTaken.lastIndexOf(startCell); i < pathTaken.size(); i++)
               {
                  this.robotPathModel.bestPath.add(pathTaken.get(i));
               }
            }
         }
      }
   }

   public Set<MazeCell> getNonHistory()
   {
      Set<MazeCell> history = this.getHistory();
      if (history == null)
      {
         return null;
      }
      Set<MazeCell> nonHistory = new TreeSet<MazeCell>();
      for (int i = 1; i <= mazeModel.getSize().width; i++)
      {
         for (int j = 1; j <= mazeModel.getSize().height; j++)
         {
            MazeCell here = new MazeCell(i, j);
            if (history.contains(here) == false)
            {
               nonHistory.add(here);
            }
         }
      }
      return nonHistory;
   }

   public List<MazeCell> getCurrentRun()
   {
      if (pathTaken.isEmpty())
      {
         return new ArrayList<MazeCell>();
      }
      MazeCell startCell = new MazeCell(1, mazeModel.getSize().height);
      if (startCell.equals(pathTaken.get(pathTaken.size() - 1)))
      {
         return new ArrayList<MazeCell>();
      }
      ArrayList<MazeCell> currentRun = new ArrayList<MazeCell>();
      for (int i = pathTaken.lastIndexOf(new MazeCell(1, mazeModel.getSize().height)); i < pathTaken.size(); i++)
      {
         currentRun.add(pathTaken.get(i));
      }
      return currentRun;
   }

   public List<MazeCell> getFirstRun()
   {
      return this.robotPathModel.firstPath;
   }

   public List<MazeCell> getBestRun()
   {
      return this.robotPathModel.bestPath;
   }

   public RobotPathModel getRobotPathModel()
   {
      return this.robotPathModel;
   }

   /**
    * Tells you if the robot is currently at the starting position.
    * @return
    */
   public boolean isAtStart()
   {
      MazeCell start = new MazeCell(1, this.mazeModel.getSize().height);
      if (this.currentLocation.equals(start))
      {
         return true;
      }
      return false;
   }

   /**
    * Tells you if the robot is in the center 2x2 box. The winning area.
    * @return
    */
   public boolean isAtCenter()
   {
      MazeCell goal1 = new MazeCell(this.mazeModel.getSize().width / 2,
                                    this.mazeModel.getSize().height / 2);
      MazeCell goal2 = goal1.plusX(1);
      MazeCell goal3 = goal1.plusY(1);
      MazeCell goal4 = goal3.plusX(1);
      if ( (this.currentLocation.equals(goal1)) ||
          (this.currentLocation.equals(goal2)) ||
          (this.currentLocation.equals(goal3)) ||
          (this.currentLocation.equals(goal4)))
      {
         return true;
      }
      return false;
   }

}
