package maze.ai;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import maze.model.Direction;
import maze.model.MazeCell;

/**
 * Maze solving algorithm that provides a right wall follower with a memory so
 * it prefers unexplored cells.
 */
public class Tremaux extends RobotBase
{
   private Direction[][] ballOfString;
   private final List<RobotStep> moveQueue = new LinkedList<RobotStep>();
   private boolean turbo = false;

   /**
    * This returns the name of the mathematician who came up with this process
    */
   @Override
   public String toString()
   {
      return "Tremaux";
   }

   /**
    * This function should be called by the controller any time a new run is to
    * commence
    */
   @Override
   public void initialize()
   {
      super.initialize();

      Dimension size = robotLocation.getMazeSize();
      ballOfString = new Direction[(int) size.getWidth()][(int) size.getHeight()];

      for (int i = 0; i < size.getWidth(); i++)
      {
         Arrays.fill(this.ballOfString[i], null);
      }
      ballOfString[0][size.height - 1] = Direction.North;
      this.moveQueue.clear();
   }

   /**
    * This returns the state of the turbo flag. Turbo should be true when
    * traversing previously explored territories.
    */
   @Override
   public boolean isInTurboMode()
   {
      return turbo;
   }

   /**
    * This returns the next step for the robot to take. It should be called by
    * the controller.
    */
   @Override
   public RobotStep nextStep()
   {
      RobotStep next;
      if (getDirection(robotLocation.getCurrentLocation()) == null)
      {
         setDirection();
      }
      if (moveQueue.isEmpty())
      {
         if ( (robotLocation.isWallRight() == false) && (getRightNeighborDirection() == null))
         {
            next = RobotStep.RotateRight;
            moveQueue.add(RobotStep.MoveForward);
            turbo = false;
         }
         else if ( (robotLocation.isWallFront() == false) && (getFrontNeighborDirection() == null))
         {
            next = RobotStep.MoveForward;
            turbo = false;
         }
         else if ( (robotLocation.isWallLeft() == false) && (getLeftNeighborDirection() == null))
         {
            next = RobotStep.RotateLeft;
            moveQueue.add(RobotStep.MoveForward);
            turbo = false;
         }
         else
         //Retrace the steps
         {
            turbo = true;
            if (robotLocation.getDirection() == getDirection(robotLocation.getCurrentLocation()))
            {
               next = RobotStep.MoveForward;
            }
            else if (robotLocation.getDirection().getLeft() == getDirection(robotLocation.getCurrentLocation()))
            {
               next = RobotStep.RotateLeft;
               moveQueue.add(RobotStep.MoveForward);
            }
            else if (robotLocation.getDirection().getRight() == getDirection(robotLocation.getCurrentLocation()))
            {
               next = RobotStep.RotateRight;
               moveQueue.add(RobotStep.MoveForward);
            }
            else
            {
               next = RobotStep.RotateRight;
               moveQueue.add(RobotStep.RotateRight);
               moveQueue.add(RobotStep.MoveForward);
            }
         }
      }
      else
      {
         next = moveQueue.get(0);
         moveQueue.remove(0);
      }
      return next;
   }

   /**
    * This returns the direction for the understanding for the neighbor to the
    * left.
    */
   private Direction getLeftNeighborDirection()
   {
      return getNeighborDirection(robotLocation.getDirection().getLeft());
   }

   /**
    * This returns the direction for the understanding for the neighbor to the
    * front.
    */
   private Direction getFrontNeighborDirection()
   {
      return getNeighborDirection(robotLocation.getDirection());
   }

   /**
    * This returns the direction for the understanding for the neighbor to the
    * right.
    */
   private Direction getRightNeighborDirection()
   {
      return getNeighborDirection(robotLocation.getDirection().getRight());
   }

   /**
    * This returns the direction for the understanding for the neighbor to the
    * direction given from the current cell.
    */
   private Direction getNeighborDirection(Direction direction)
   {
      try
      {
         final MazeCell there = robotLocation.getCurrentLocation().neighbor(direction);
         if (!there.isInRange(robotLocation.getMazeSize()))
            return null;
         else
            return this.getDirection(there);
      }
      catch (IllegalArgumentException e)
      {
         return null;
      }
   }

   /**
    * This sets the direction for the understanding for the current cell.
    */
   private void setDirection()
   {
      Direction wayBack = robotLocation.getDirection().getOpposite();
      MazeCell here = robotLocation.getCurrentLocation();
      ballOfString[here.getX() - 1][here.getY() - 1] = wayBack;
   }

   /**
    * This returns the direction for the understanding for the given cell
    */
   private Direction getDirection(MazeCell currentLocation)
   {
      return ballOfString[currentLocation.getX() - 1][currentLocation.getY() - 1];
   }

   /**
    * This returns the understanding of the maze. Tremaux's understanding is the
    * directions needed to return to the start.
    */
   @Override
   public Direction[][] getUnderstandingDir()
   {
      return ballOfString;
   }
}