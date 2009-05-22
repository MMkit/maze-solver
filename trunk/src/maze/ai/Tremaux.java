package maze.ai;

import java.awt.Dimension;
import java.util.ArrayList;

import maze.model.Direction;
import maze.model.MazeCell;

public class Tremaux extends RobotBase
{
   /**
	* @param args
	*/
   private Direction[][] ballOfString;
   private ArrayList<RobotStep> moveQueue = new ArrayList<RobotStep>();
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
	 * This function should be called by the controller any time a new run is
	 * to commence
	 */
   @Override
   public void initialize()
   {
      super.initialize();

      Dimension size = robotLocation.getMazeSize();
      if (ballOfString == null)
      {
         ballOfString = new Direction[(int) size.getWidth()]
                                      [(int) size.getHeight()];
      }
      for (int i = 0; i < size.getWidth(); i++)
      {
         for (int j = 0; j < size.getHeight(); j++)
         {
            ballOfString[i][j] = null;
         }
      }
      ballOfString[0][size.height - 1] = Direction.North;
   }

   /**
    * This returns the state of the turbo flag.  Turbo should be true when
    * traversing previously explored territories.
    */
   public boolean isInTurboMode()
   {
      return turbo;
   }

   /**
    * This returns the next step for the robot to take.  It should be called
    * by the controller.
    */
   public RobotStep nextStep()
   {
      RobotStep next;
      if (getDirection(robotLocation.getCurrentLocation())
    		  == null)
      {
         setDirection();
      }
      if (moveQueue.isEmpty() == true)
      {
         if ( (robotLocation.isWallRight() == false) &&
             (getRightNeighborDirection() == null))
         {
            next = RobotStep.RotateRight;
            moveQueue.add(RobotStep.MoveForward);
            turbo = false;
         }
         else if ( (robotLocation.isWallFront() == false) &&
                  (getFrontNeighborDirection() == null))
         {
            next = RobotStep.MoveForward;
            turbo = false;
         }
         else if ( (robotLocation.isWallLeft() == false) &&
                  (getLeftNeighborDirection() == null))
         {
            next = RobotStep.RotateLeft;
            moveQueue.add(RobotStep.MoveForward);
            turbo = false;
         }
         else
         //Retrace the steps
         {
            turbo = true;
            if (robotLocation.getDirection()
            		== getDirection(robotLocation.getCurrentLocation()))
            {
               next = RobotStep.MoveForward;
            }
            else if (robotLocation.getDirection().getLeft()
            		== getDirection(robotLocation.getCurrentLocation()))
            {
               next = RobotStep.RotateLeft;
               moveQueue.add(RobotStep.MoveForward);
            }
            else if (robotLocation.getDirection().getRight()
            		== getDirection(robotLocation.getCurrentLocation()))
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
      if (robotLocation.getCurrentLocation().getY() == 1)
      {
         return null;
      }
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
      MazeCell here = robotLocation.getCurrentLocation();
      MazeCell there;
      Dimension size = robotLocation.getMazeSize();
      if ( (direction == Direction.North) && (here.getY() != 1))
      {
         there = new MazeCell(here.getX(), here.getY() - 1);
      }
      else if ( (direction == Direction.South) && (here.getY() != size.getHeight()))
      {
         there = new MazeCell(here.getX(), here.getY() + 1);
      }
      else if ( (direction == Direction.East) && (here.getX() != size.getWidth()))
      {
         there = new MazeCell(here.getX() + 1, here.getY());
      }
      else if ( (direction == Direction.West) && (here.getX() != 1))
      {
         there = new MazeCell(here.getX() - 1, here.getY());
      }
      else
      {
         return null;
      }
      return getDirection(there);
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
    * This returns the understanding of the maze.  Tremaux's understanding is
    * the directions needed to return to the start.
    */
   public Direction[][] getUnderstandingDir(){
	   return ballOfString;
   }
}