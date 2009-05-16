package maze.model;

import java.awt.Dimension;
import java.util.ArrayList;
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
    * The list of cells that the
    */
   private final List<MazeCell> pathTaken = new ArrayList<MazeCell>();

   private List<MazeCell> firstRun = new ArrayList<MazeCell>();

   private List<MazeCell> bestRun = new ArrayList<MazeCell>();
   /**
    * All the maze cells that have already been visited.
    */
   private final Set<MazeCell> history = new TreeSet<MazeCell>();

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
   }

   public boolean isWallFront()
   {
      return this.mazeModel.getWall(this.currentLocation, this.direction).isSet();
   }

   public boolean isWallBack()
   {
      return this.mazeModel.getWall(this.currentLocation, this.direction.getOpposite()).isSet();
   }

   public boolean isWallLeft()
   {
      return this.mazeModel.getWall(this.currentLocation, this.direction.getLeft()).isSet();
   }

   public boolean isWallRight()
   {
      return this.mazeModel.getWall(this.currentLocation, this.direction.getRight()).isSet();
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
    * Attempts to move the robot with the given step. If the
    * @param nextStep
    */
   public void takeNextStep(RobotStep nextStep)
   {
      if (nextStep == RobotStep.RotateLeft)
      {
         direction = direction.getLeft();
      }
      else if (nextStep == RobotStep.RotateRight)
      {
         direction = direction.getRight();
      }
      else if (nextStep == RobotStep.MoveForward)
      {
         if (this.isWallFront() == true)
         {
            throw new RuntimeException("The mouse just crashed into a wall");
         }
         else
         {
            if (direction == Direction.North)
            {
               currentLocation = new MazeCell(currentLocation.getX(), currentLocation.getY() - 1);
            }
            else if (direction == Direction.South)
            {
               currentLocation = new MazeCell(currentLocation.getX(), currentLocation.getY() + 1);
            }
            else if (direction == Direction.East)
            {
               currentLocation = new MazeCell(currentLocation.getX() + 1, currentLocation.getY());
            }
            else if (direction == Direction.West)
            {
               currentLocation = new MazeCell(currentLocation.getX() - 1, currentLocation.getY());
            }
            pathTaken.add(currentLocation);
            history.add(currentLocation);
         }
      }
      else if (nextStep == RobotStep.MoveBackward)
      {
         if (this.isWallBack() == true)
         {
            throw new RuntimeException("The mouse just crashed into a wall");
         }
         else
         {
            if (direction == Direction.North)
            {
               currentLocation = new MazeCell(currentLocation.getX(), currentLocation.getY() + 1);
            }
            else if (direction == Direction.South)
            {
               currentLocation = new MazeCell(currentLocation.getX(), currentLocation.getY() - 1);
            }
            else if (direction == Direction.East)
            {
               currentLocation = new MazeCell(currentLocation.getX() - 1, currentLocation.getY());
            }
            else if (direction == Direction.West)
            {
               currentLocation = new MazeCell(currentLocation.getX() + 1, currentLocation.getY());
            }
            pathTaken.add(currentLocation);
            history.add(currentLocation);
         }
      }

      MazeCell goal1 = new MazeCell(mazeModel.getSize().width / 2, mazeModel.getSize().height / 2);
      MazeCell goal2 = goal1.plusX(1);
      MazeCell goal3 = goal1.plusY(1);
      MazeCell goal4 = goal2.plusY(1);
      if (currentLocation.equals(goal1) ||
          currentLocation.equals(goal2) ||
          currentLocation.equals(goal3) ||
          currentLocation.equals(goal4))
      {
         if (firstRun.isEmpty())
         {
            for (int i = 0; i < pathTaken.size(); i++)
            {
               firstRun.add(pathTaken.get(i));
               bestRun.add(pathTaken.get(i));
            }
         }
         else
         {
            MazeCell startCell = new MazeCell(1, mazeModel.getSize().height);
            if ( (bestRun.size()) > (pathTaken.size() - (pathTaken.lastIndexOf(startCell) + 1)))
            {
               bestRun.clear();
               for (int i = pathTaken.lastIndexOf(startCell); i < pathTaken.size(); i++)
               {
                  bestRun.add(pathTaken.get(i));
               }
            }
         }
      }
   }

   public boolean isExplored(MazeCell location)
   {
      return history.contains(location);
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
      return firstRun;
   }

   public List<MazeCell> getBestRun()
   {
      return bestRun;
   }

}
