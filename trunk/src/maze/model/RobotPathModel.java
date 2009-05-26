package maze.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maze.util.ListenerSubject;

public final class RobotPathModel extends ListenerSubject<MazeCell> implements Serializable
{
   private final Set<MazeCell> visited = new HashSet<MazeCell>(128);
   private final List<MazeCell> pathCurrent = new ArrayList<MazeCell>(128);
   private final List<MazeCell> pathFirst = new ArrayList<MazeCell>(128);
   final List<MazeCell> pathBest = new ArrayList<MazeCell>(128);
   private final MazeCell startCell;

   public RobotPathModel(MazeCell startingCell)
   {
      this.startCell = startingCell;
   }

   /**
    * Add a new visited cell to the path storage model.
    * @param cell The cell that has just been visited.
    */
   public void addLocation(MazeCell cell)
   {
      this.visited.add(cell);
      this.pathCurrent.add(cell);
      // Notify listeners that this cell has been changed.
      super.notifyListeners(cell);
      // Also signal that the north and west walls may need to be repainted.
      if (cell.getX() > 1)
      {
         super.notifyListeners(cell.plusX(-1));
      }
      if (cell.getY() > 1)
      {
         super.notifyListeners(cell.plusY(-1));
      }
   }

   public boolean hasCellBeenVisited(MazeCell cell)
   {
      return this.visited.contains(cell);
   }

   /**
    * Get the most recent path the robot has taken since the last time it
    * visited the starting cell.
    * @return An in order list of the robots most recent path.
    */
   public List<MazeCell> getPathRecent()
   {
      if (this.pathCurrent.isEmpty())
      {
         return new ArrayList<MazeCell>();
      }
      if (this.startCell.equals(this.pathCurrent.get(this.pathCurrent.size() - 1)))
      {
         return new ArrayList<MazeCell>();
      }
      ArrayList<MazeCell> currentRun = new ArrayList<MazeCell>();
      for (int i = this.pathCurrent.lastIndexOf(this.startCell); i < this.pathCurrent.size(); i++)
      {
         currentRun.add(this.pathCurrent.get(i));
      }
      return currentRun;
   }

   /**
    * Gets the entire current path from the very beginning.
    * @return An in order list of the total path taken by the robot.
    */
   public List<MazeCell> getPathCurrent()
   {
      return this.pathCurrent;
   }

   public List<MazeCell> getPathFirst()
   {
      return this.pathFirst;
   }

   public List<MazeCell> getPathBest()
   {
      return this.pathBest;
   }

}
