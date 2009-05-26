package maze.ai;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Vector;

import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;

public class ModifiedFloodfill extends RobotBase
{
   private int[][] distance;
   private boolean[][] explored;
   private int[][] directions;
   private ArrayList<RobotStep> moveQueue = new ArrayList<RobotStep>();
   private boolean turbo = false;
   private static final int USELESS = 1024;
   private MazeModel maze = new MazeModel();
   private boolean goal;
   private static final boolean TO_CENTER = true;
   private static final boolean TO_START = false;
   private boolean speedRunCapable;

   /**
    * Returns a string describing this class
    */
   @Override
   public String toString()
   {
      return "Modified Flood Fill";
   }

   /**
    * Initializes the algorithm for use. This should be called before any run is
    * started.
    */
   @Override
   public void initialize()
   {
      super.initialize();
      moveQueue.clear();
      maze.setSize(robotLocation.getMazeSize());
      maze.clearMaze();
      Dimension size = maze.getSize();
      if (distance == null)
      {
         distance = new int[size.width][size.height];
         explored = new boolean[size.width][size.height];
         directions = new int[size.width][size.height];
      }
      for (int i = 0; i < size.width; i++)
      {
         for (int j = 0; j < size.height; j++)
         {
            distance[i][j] = USELESS;
            explored[i][j] = false;
            directions[i][j] = 0;
         }
      }
      goal = TO_CENTER;
      speedRunCapable = false;
   }

   /**
    * Returns the next step that the robot should take. This is called by the
    * controller.
    */
   @Override
   public RobotStep nextStep()
   {
      RobotStep next;
      Direction nextDirection;
      Direction currentDirection = robotLocation.getDirection();
      if (moveQueue.isEmpty())
      {
         if (getExplored() == false)
         {
            checkWalls();
            setExplored();
         }
         if (atGoal() == true)
         {
            if ( (goal == TO_CENTER) && (speedRunCapable == false))
            {
               speedRunCapable = true;
               blockOutCenter();
            }
            goal = !goal;
            floodfill();
         }
         nextDirection = getBestDirection();
         turbo = getNeighborExplored(nextDirection);
         if (nextDirection == currentDirection)
         {
            next = RobotStep.MoveForward;
         }
         else if (nextDirection == currentDirection.getLeft())
         {
            next = RobotStep.RotateLeft;
            moveQueue.add(RobotStep.MoveForward);
         }
         else if (nextDirection == currentDirection.getRight())
         {
            next = RobotStep.RotateRight;
            moveQueue.add(RobotStep.MoveForward);
         }
         else
         // if(nextDirection == currentDirection.getOpposite())
         {
            next = RobotStep.MoveBackward;
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
    * Returns the explored flag of the current location's neighbor in the given
    * direction.
    */
   private boolean getNeighborExplored(Direction direction)
   {
      MazeCell neighbor;
      MazeCell here = robotLocation.getCurrentLocation();
      Dimension size = maze.getSize();
      if ( (direction == Direction.North) && (here.getY() != 1))
      {
         neighbor = new MazeCell(here.getX(), here.getY() - 1);
      }
      else if ( (direction == Direction.South) && (here.getY() != size.getHeight()))
      {
         neighbor = new MazeCell(here.getX(), here.getY() + 1);
      }
      else if ( (direction == Direction.East) && (here.getX() != size.getWidth()))
      {
         neighbor = new MazeCell(here.getX() + 1, here.getY());
      }
      else if ( (direction == Direction.West) && (here.getX() != 1))
      {
         neighbor = new MazeCell(here.getX() - 1, here.getY());
      }
      else
      {
         return false;
      }

      return getExplored(neighbor);
   }

   /**
    * Returns the explored flag of the given location's neighbor.
    */
   private boolean getNeighborExplored(MazeCell here, Direction direction)
   {
      MazeCell neighbor;
      Dimension size = maze.getSize();
      if ( (direction == Direction.North) && (here.getY() != 1))
      {
         neighbor = new MazeCell(here.getX(), here.getY() - 1);
      }
      else if ( (direction == Direction.South) && (here.getY() != size.getHeight()))
      {
         neighbor = new MazeCell(here.getX(), here.getY() + 1);
      }
      else if ( (direction == Direction.East) && (here.getX() != size.getWidth()))
      {
         neighbor = new MazeCell(here.getX() + 1, here.getY());
      }
      else if ( (direction == Direction.West) && (here.getX() != 1))
      {
         neighbor = new MazeCell(here.getX() - 1, here.getY());
      }
      else
      {
         return false;
      }

      return getExplored(neighbor);
   }

   /**
    * Returns the explored flag of the given location.
    */
   private boolean getExplored(MazeCell cell)
   {
      return explored[cell.getX() - 1][cell.getY() - 1];
   }

   /**
    * Returns the explored flag of the current location.
    */
   private boolean getExplored()
   {
      return getExplored(robotLocation.getCurrentLocation());
   }

   /**
    * Sets the explored flag of the current location to true.
    */
   private void setExplored()
   {
      explored[robotLocation.getCurrentLocation().getX() - 1][robotLocation.getCurrentLocation().getY() - 1] = true;
   }

   /**
    * Returns the best direction to go, according to Floodfill, from the current
    * MazeCell. This algorithm biases in the following order: Straight, North,
    * East, West, South
    */
   private Direction getBestDirection()
   {
      MazeCell here = robotLocation.getCurrentLocation();
      Direction current = robotLocation.getDirection();
      //int bestDistance = getDistance(here);
      Direction bestDirection = null;

      if (hasSameDirection(here, Direction2.getDirection(current)))
      //&& (robotLocation.isWallFront() == false))
      {
         bestDirection = current;
      }
      else if (hasSameDirection(here, Direction2.getDirection(Direction.North)))
      //&& (maze.getWall(here, Direction.North).isSet() == false))
      {
         bestDirection = Direction.North;
      }
      else if (hasSameDirection(here, Direction2.getDirection(Direction.East)))
      //&& (maze.getWall(here, Direction.East).isSet() == false))
      {
         bestDirection = Direction.East;
      }
      else if (hasSameDirection(here, Direction2.getDirection(Direction.South)))
      //&& (maze.getWall(here, Direction.South).isSet() == false))
      {
         bestDirection = Direction.South;
      }
      else if (hasSameDirection(here, Direction2.getDirection(Direction.West)))
      //&& (maze.getWall(here, Direction.West).isSet() == false))
      {
         bestDirection = Direction.West;
      }
      /*
            if ( (bestDistance > 
            		getNeighborDistance(here, robotLocation.getDirection())) &&
                (robotLocation.isWallFront() == false))
            {
               bestDirection = robotLocation.getDirection();
               bestDistance = getNeighborDistance(here, bestDirection);
            }

            if ( (bestDistance > getNeighborDistance(here, Direction.North)) &&
                (maze.getWall(here, Direction.North).isSet() == false))
            {
               bestDirection = Direction.North;
               bestDistance = getNeighborDistance(here, bestDirection);
            }
            if ( (bestDistance > getNeighborDistance(here, Direction.East)) &&
                (maze.getWall(here, Direction.East).isSet() == false))
            {
               bestDirection = Direction.East;
               bestDistance = getNeighborDistance(here, bestDirection);
            }
            if ( (bestDistance > getNeighborDistance(here, Direction.West)) &&
                (maze.getWall(here, Direction.West).isSet() == false))
            {
               bestDirection = Direction.West;
               bestDistance = getNeighborDistance(here, bestDirection);
            }
            if ( (bestDistance > getNeighborDistance(here, Direction.South)) &&
                (maze.getWall(here, Direction.South).isSet() == false))
            {
               bestDirection = Direction.South;
               bestDistance = getNeighborDistance(here, bestDirection);
            }
            */

      if (bestDirection == null)
      {
         modifiedFloodfill();
         return getBestDirection();
      }
      else
      {
         return bestDirection;
      }
   }

   /**
    * Returns the distance of the MazeCell adjacent to the passed MazeCell and
    * is adjacent in the specified direction.
    */
   private int getNeighborDistance(MazeCell here, Direction direction)
   {
      MazeCell neighbor;
      Dimension size = maze.getSize();
      if ( (direction == Direction.North) && (here.getY() != 1))
      {
         neighbor = new MazeCell(here.getX(), here.getY() - 1);
      }
      else if ( (direction == Direction.South) && (here.getY() != size.getHeight()))
      {
         neighbor = new MazeCell(here.getX(), here.getY() + 1);
      }
      else if ( (direction == Direction.East) && (here.getX() != size.getWidth()))
      {
         neighbor = new MazeCell(here.getX() + 1, here.getY());
      }
      else if ( (direction == Direction.West) && (here.getX() != 1))
      {
         neighbor = new MazeCell(here.getX() - 1, here.getY());
      }
      else
      {
         return USELESS;
      }

      return getDistance(neighbor);
   }

   /**
    * Returns the distance of the desired MazeCell
    */
   private int getDistance(MazeCell here)
   {
      return distance[here.getX() - 1][here.getY() - 1];
   }

   /**
    * Sets the distance of the desired MazeCell to the value provided
    */
   private void setDistance(MazeCell here, int value)
   {
      distance[here.getX() - 1][here.getY() - 1] = value;
   }

   private void floodfill()
   {
      Dimension size = maze.getSize();
      Vector<MazeCell> queue = new Vector<MazeCell>();
      MazeCell cell;
      int currentDistance;
      boolean speedy;

      for (int i = 1; i <= size.width; i++)
      {
         for (int j = 1; j <= size.height; j++)
         {
            MazeCell here = new MazeCell(i, j);
            setDistance(here, USELESS);
            setDirection(here, 0);
         }
      }

      if (goal == TO_START)
      {
         cell = new MazeCell(1, size.height);
         setDistance(cell, 0);
         queue.add(cell);
         speedy = false;
      }
      else
      {
         int targetX = size.width / 2;
         int targetY = size.height / 2;
         cell = new MazeCell(targetX, targetY);
         setDistance(cell, 0);
         queue.add(cell);
         cell = new MazeCell(targetX + 1, targetY);
         setDistance(cell, 0);
         queue.add(cell);
         cell = new MazeCell(targetX, targetY + 1);
         setDistance(cell, 0);
         queue.add(cell);
         cell = new MazeCell(targetX + 1, targetY + 1);
         setDistance(cell, 0);
         queue.add(cell);
         if ( (speedRun == true) && (speedRunCapable == true))
         {
            speedy = true;
         }
         else
         {
            speedy = false;
         }
      }

      while (queue.isEmpty() == false)
      {
         cell = queue.get(0);
         queue.remove(0);
         currentDistance = getDistance(cell);

         //Check to see if accessible
         if (maze.getWall(cell, Direction.North).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.North)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.North) == true)))
            {
               queue.add(cell.plusY(-1));
               setDistance(cell.plusY(-1), currentDistance + 1);
               setDirection(cell.plusY(-1), Direction2.South.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.North)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.North) == true)))
            {
               setDirection(cell.plusY(-1), Direction2.South.getIndex() +
                                            getDirection(cell.plusY(-1)));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.South).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.South)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.South))))
            {
               queue.add(cell.plusY(1));
               setDistance(cell.plusY(1), currentDistance + 1);
               setDirection(cell.plusY(1), Direction2.North.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.South)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.South) == true)))
            {
               setDirection(cell.plusY(1), Direction2.North.getIndex() +
                                           getDirection(cell.plusY(1)));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.West).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.West)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.West))))
            {
               queue.add(cell.plusX(-1));
               setDistance(cell.plusX(-1), currentDistance + 1);
               setDirection(cell.plusX(-1), Direction2.East.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.West)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.West) == true)))
            {
               setDirection(cell.plusX(-1), Direction2.East.getIndex() +
                                            getDirection(cell.plusX(-1)));
            }

         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.East).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.East)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.East))))
            {
               queue.add(cell.plusX(1));
               setDistance(cell.plusX(1), currentDistance + 1);
               setDirection(cell.plusX(1), Direction2.West.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.East)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.East) == true)))
            {
               setDirection(cell.plusX(1), Direction2.West.getIndex() + getDirection(cell.plusX(1)));
            }
         }
      }

      MazeCell here = robotLocation.getCurrentLocation();
      if (getDistance(here) == USELESS)
      {
         System.out.println("Purging Knowledge");
         maze.clearMaze();
         speedRunCapable = false;
         for (int i = 0; i < size.width; i++)
         {
            for (int j = 0; j < size.height; j++)
            {
               explored[i][j] = false;
            }
         }
         explored[here.getX() - 1][here.getY() - 1] = true;
         checkWalls();
         floodfill();
      }
   }

   /**
    * This function loads a value into the directions array based upon the cell
    * passed and the value in i
    */
   private void setDirection(MazeCell here, int i)
   {
      directions[here.getX() - 1][here.getX() - 1] = i;
   }

   /**
    * This function loads a value into the directions array based upon the cell
    * passed and the value in i
    */
   private int getDirection(MazeCell here)
   {
      return directions[here.getX() - 1][here.getX() - 1];
   }

   /**
    * This function loads a value into the directions array based upon the cell
    * passed and the value in i
    */
   private boolean isSameDirection(MazeCell here, Direction2 dir)
   {
      if (directions[here.getX() - 1][here.getX() - 1] == dir.getIndex())
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * This function loads a value into the directions array based upon the cell
    * passed and the value in i
    */
   private boolean hasSameDirection(MazeCell here, Direction2 dir)
   {
      return Direction2.containsDirection(directions[here.getX() - 1][here.getX() - 1], dir);
   }

   /**
    * This helper function should only be called when first entering the center
    * goal. Sets the maze knowledge of the algorithm to think that there are no
    * other ways out of the center because according the American rules there is
    * only one entrance/exit associated with the goal.
    */
   private void blockOutCenter()
   {
      Dimension size = maze.getSize();
      MazeCell cell1 = new MazeCell(size.width / 2, size.height / 2);
      MazeCell cell2 = new MazeCell(size.width / 2 + 1, size.height / 2);
      MazeCell cell3 = new MazeCell(size.width / 2, size.height / 2 + 1);
      MazeCell cell4 = new MazeCell(size.width / 2 + 1, size.height / 2 + 1);
      MazeCell current = robotLocation.getCurrentLocation();

      if (cell1.equals(current) == false)
      {
         maze.setWall(cell1.getX(), cell1.getY(), Direction.North.getIndex());
         maze.setWall(cell1.getX(), cell1.getY(), Direction.West.getIndex());
      }
      if (cell2.equals(current) == false)
      {
         maze.setWall(cell2.getX(), cell2.getY(), Direction.North.getIndex());
         maze.setWall(cell2.getX(), cell2.getY(), Direction.East.getIndex());
      }
      if (cell3.equals(current) == false)
      {
         maze.setWall(cell3.getX(), cell3.getY(), Direction.South.getIndex());
         maze.setWall(cell3.getX(), cell3.getY(), Direction.West.getIndex());
      }
      if (cell4.equals(current) == false)
      {
         maze.setWall(cell4.getX(), cell4.getY(), Direction.South.getIndex());
         maze.setWall(cell4.getX(), cell4.getY(), Direction.East.getIndex());
      }
   }

   /**
    * Returns true if the current location of the robot is its current
    * destination.
    */
   private boolean atGoal()
   {
      MazeCell cell = robotLocation.getCurrentLocation();
      Dimension size = maze.getSize();
      if ( (goal == TO_START) && (cell.getX() == 1) && (cell.getY() == size.height))
      {
         return true;
      }
      if ( (goal == TO_CENTER) &&
          ( (cell.getY() == size.height / 2) || (cell.getY() == (size.height / 2 + 1))) &&
          ( (cell.getX() == size.width / 2) || (cell.getX() == (size.width / 2 + 1))))
      {
         return true;
      }
      return false;
   }

   /**
    * Updates the algorithm's memory of the maze based upon the walls in the
    * current cell.
    */
   private void checkWalls()
   {
      MazeCell cell = robotLocation.getCurrentLocation();
      Direction direction = robotLocation.getDirection();
      if (robotLocation.isWallFront())
      {
         maze.setWall(cell.getX(), cell.getY(), direction.getIndex());
         if (hasSameDirection(cell, Direction2.getDirection(direction)))
         {
            setDirection(cell, getDirection(cell) ^ Direction2.getDirection(direction).getIndex());
         }
      }
      if (robotLocation.isWallLeft())
      {
         maze.setWall(cell.getX(), cell.getY(), direction.getLeft().getIndex());
         setDirection(cell,
                      getDirection(cell) &
                            (Direction2.Mask.getIndex() ^ Direction2.getDirection(direction.getLeft()).getIndex()));
      }
      if (robotLocation.isWallRight())
      {
         maze.setWall(cell.getX(), cell.getY(), direction.getRight().getIndex());
         setDirection(cell,
                      getDirection(cell) &
                            (Direction2.Mask.getIndex() ^ Direction2.getDirection(direction.getRight()).getIndex()));
      }
      if (robotLocation.isWallBack())
      {
         maze.setWall(cell.getX(), cell.getY(), direction.getOpposite().getIndex());
         setDirection(cell,
                      getDirection(cell) &
                            (Direction2.Mask.getIndex() ^ Direction2.getDirection(direction.getOpposite()).getIndex()));
      }
   }

   /**
    * Returns the "turbo" state of the algorithm. Will be true when areas are
    * being traversed after they have been explored before.
    */
   public boolean isInTurboMode()
   {
      return turbo;
   }

   /**
    * Returns the instance of how the algorithm understands the maze. For
    * Floodfill this is an integer for each cell representing the expected
    * distance from the center.
    */
   public int[][] getUnderstandingInt()
   {
      return distance;
   }

   @Override
   /**
    * Sets the instance of the robot model to use.
    */
   public void setRobotLocation(RobotModel model)
   {
      this.robotLocation = model;
      distance = null; //insures that initialize() works right
   }

   private void modifiedFloodfill()
   {
      //Dimension size = maze.getSize();
      Vector<MazeCell> queue = new Vector<MazeCell>();
      Vector<MazeCell> badQueue = new Vector<MazeCell>();
      MazeCell cell;
      MazeCell here = robotLocation.getCurrentLocation();
      int currentDistance;
      boolean speedy;

      if ( (goal == TO_CENTER) && (speedRun == true) && (speedRunCapable == true))
      {
         speedy = true;
      }
      else
      {
         speedy = false;
      }

      //Load up a queue until you find good info
      badQueue.add(here);
      while ( (badQueue.isEmpty() == false)/* && (queue.isEmpty() == true)*/)
      {
         cell = badQueue.get(0);
         badQueue.remove(0);
         this.setDistance(cell, USELESS);
         currentDistance = USELESS;

         //Check to see if accessible
         if (maze.getWall(cell, Direction.North).isSet() == false)
         { //Check to see if it should be added to queue
            if (neighborIsPoisoned(cell, Direction.North))
            {
               //if( queue.isEmpty()){
               badQueue.add(cell.plusY(-1));
               //}
               setDirection(cell.plusY(-1), Direction2.Directionless.getIndex());
            }
            else
            {
               queue.add(cell.plusY(-1));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.South).isSet() == false)
         { //Check to see if it should be added to queue
            if (neighborIsPoisoned(cell, Direction.South))
            {
               //if( queue.isEmpty()){
               badQueue.add(cell.plusY(1));
               //}
               setDirection(cell.plusY(1), Direction2.Directionless.getIndex());
            }
            else
            {
               queue.add(cell.plusY(1));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.West).isSet() == false)
         { //Check to see if it should be added to queue
            if (neighborIsPoisoned(cell, Direction.West))
            {
               //if( queue.isEmpty()){
               badQueue.add(cell.plusX(-1));
               //}
               setDirection(cell.plusX(-1), Direction2.Directionless.getIndex());
            }
            else
            {
               queue.add(cell.plusX(-1));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.East).isSet() == false)
         { //Check to see if it should be added to queue
            if (neighborIsPoisoned(cell, Direction.East))
            {
               //if( queue.isEmpty()){
               badQueue.add(cell.plusX(1));
               //}
               setDirection(cell.plusX(1), Direction2.Directionless.getIndex());
            }
            else
            {
               queue.add(cell.plusX(1));
            }
         }
         this.setDistance(cell, USELESS);
      }

      //Work back from the good knowledge to the current location
      while (queue.isEmpty() == false)
      {
         if (queue.contains(here))
         {
            return;
         }
         cell = queue.get(0);
         queue.remove(0);
         currentDistance = getDistance(cell);

         //Check to see if accessible
         if (maze.getWall(cell, Direction.North).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.North)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.North) == true)))
            {
               queue.add(cell.plusY(-1));
               setDistance(cell.plusY(-1), currentDistance + 1);
               setDirection(cell.plusY(-1), Direction2.South.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.North)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.North) == true)))
            {
               setDirection(cell.plusY(-1), Direction2.South.getIndex() +
                                            getDirection(cell.plusY(-1)));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.South).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.South)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.South))))
            {
               queue.add(cell.plusY(1));
               setDistance(cell.plusY(1), currentDistance + 1);
               setDirection(cell.plusY(1), Direction2.North.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.South)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.South) == true)))
            {
               setDirection(cell.plusY(1), Direction2.North.getIndex() +
                                           getDirection(cell.plusY(1)));
            }
         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.West).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.West)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.West))))
            {
               queue.add(cell.plusX(-1));
               setDistance(cell.plusX(-1), currentDistance + 1);
               setDirection(cell.plusX(-1), Direction2.East.getIndex());
            }
            else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.West)) &&
                     ( (speedy == false) || (getNeighborExplored(cell, Direction.West) == true)))
            {
               setDirection(cell.plusX(-1), Direction2.East.getIndex() +
                                            getDirection(cell.plusX(-1)));
            }

         }

         //Check to see if accessible
         if (maze.getWall(cell, Direction.East).isSet() == false)
         { //Check to see if it should be added to queue
            if ( ( (currentDistance + 1) < getNeighborDistance(cell, Direction.East)) &&
                ( (speedy == false) || (getNeighborExplored(cell, Direction.East))))
            {
               queue.add(cell.plusX(1));
               setDistance(cell.plusX(1), currentDistance + 1);
               setDirection(cell.plusX(1), Direction2.West.getIndex());
            }
         }
         else if ( ( (currentDistance + 1) == getNeighborDistance(cell, Direction.East)) &&
                  ( (speedy == false) || (getNeighborExplored(cell, Direction.East) == true)))
         {
            setDirection(cell.plusY(-1), Direction2.West.getIndex() + getDirection(cell.plusX(1)));
         }
      }

      System.out.println("Failed Modified");
      floodfill();
   }

   private boolean neighborIsPoisoned(MazeCell cell, Direction dir)
   {
      //This function determines if a cell is tainted by bad information
      //This can be tested by finding if the cell has a neighbor that is
      //better other than the one querying it
      if (getNeighborDistance(cell, dir) == 0)
      {
         //Goals are not poisoned
         return false;
      }

      MazeCell here;
      Direction whereFrom = dir.getOpposite();

      if (dir.equals(Direction.North))
      {
         here = cell.plusY(-1);
      }
      else if (dir.equals(Direction.South))
      {
         here = cell.plusY(1);
      }
      else if (dir.equals(Direction.East))
      {
         here = cell.plusX(1);
      }
      else if (dir.equals(Direction.West))
      {
         here = cell.plusX(-1);
      }
      else
      {
         //If directionless was passed then we just act like it's not poisoned
         return false;
      }

      /*
      if(!whereFrom.equals(Direction.North))
      {
         if(getDistance(here)> getNeighborDistance(here,Direction.North)){
      	   return false;
         }
      }
      if(!whereFrom.equals(Direction.South))
      {
         if(getDistance(here)> getNeighborDistance(here,Direction.South)){
      	   return false;
         }
      }
      if(!whereFrom.equals(Direction.West))
      {
         if(getDistance(here)> getNeighborDistance(here,Direction.West)){
      	   return false;
         }
      }
      if(!whereFrom.equals(Direction.East))
      {
         if(getDistance(here)> getNeighborDistance(here,Direction.East)){
      	   return false;
         }
      }
      */

      if ( (hasSameDirection(here, Direction2.getDirection(whereFrom)) == true) &&
          isSameDirection(here, Direction2.getDirection(whereFrom)) == false)
      {
         setDirection(here, getDirection(here) ^ Direction2.getDirection(whereFrom).getIndex());
         return false;
      }

      return isSameDirection(here, Direction2.getDirection(whereFrom));
   }

   public static enum Direction2
   {
      //These should match those values set in Maze.java

      North(1),
      South(2),
      East(4),
      West(8),
      Directionless(0),
      Mask(15);
      private final int index;

      private Direction2(int index)
      {
         this.index = index;
      }

      public int getIndex()
      {
         return this.index;
      }

      public static Direction2 getDirection(Direction dir)
      {
         if (dir.equals(Direction.North))
         {
            return North;
         }
         if (dir.equals(Direction.South))
         {
            return South;
         }
         if (dir.equals(Direction.West))
         {
            return West;
         }
         if (dir.equals(Direction.East))
         {
            return East;
         }
         return Directionless;
      }

      public static Direction getDirection(Direction2 dir)
      {
         if (dir.equals(North))
         {
            return Direction.North;
         }
         if (dir.equals(South))
         {
            return Direction.South;
         }
         if (dir.equals(West))
         {
            return Direction.West;
         }
         if (dir.equals(East))
         {
            return Direction.East;
         }
         return null;
      }

      public static boolean containsDirection(int data, Direction2 dir)
      {
         if ( (data & dir.getIndex()) != 0)
         {
            return true;
         }
         else
         {
            return false;
         }
      }
   }

}
