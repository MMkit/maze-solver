package maze.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maze.util.ListenerSubject;

/**
 * This model class stores a bunch of information about the robots path through
 * the maze. Like which cells have been visited, and what the current, best, and
 * first paths are.
 * @author Luke Last
 */
public final class RobotPathModel extends ListenerSubject<MazeCell> implements Serializable
{
   private final Set<MazeCell> visited = new HashSet<MazeCell>(128);
   private final List<MazeCell> pathCurrent = new ArrayList<MazeCell>(128);
   private final List<MazeCell> pathFirst = new ArrayList<MazeCell>(128);
   final List<MazeCell> pathBest = new ArrayList<MazeCell>(128);
   private final MazeCell startCell;

   /**
    * Constructor.
    * @param startingCell The cell that should be considered to be the starting
    *           point of the maze. This is used to determine the recent path
    *           since hitting the starting cell.
    */
   public RobotPathModel(MazeCell startingCell)
   {
      if (startingCell == null)
         throw new IllegalArgumentException("Starting cell cannot be null.");
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
      //TODO This whole thing is slow.

      if (this.pathCurrent.isEmpty() ||
          this.startCell.equals(this.pathCurrent.get(this.pathCurrent.size() - 1)))
      {
         return Collections.emptyList();
      }
      final ArrayList<MazeCell> currentRun = new ArrayList<MazeCell>();
      for (int i = this.pathCurrent.lastIndexOf(this.startCell); i < this.pathCurrent.size(); i++)
      {
         currentRun.add(this.pathCurrent.get(i));
      }
      return Collections.unmodifiableList(currentRun);
   }

   /**
    * Gets the entire current path from the very beginning.
    * @return An in order list of the total path taken by the robot. The List is
    *         read only.
    */
   public List<MazeCell> getPathCurrent()
   {
      return Collections.unmodifiableList(this.pathCurrent);
   }

   /**
    * Get the first path taken from the start to the center.
    * @return Ordered list of cells.
    */
   public List<MazeCell> getPathFirst()
   {
      return this.pathFirst;
   }

   /**
    * Get the best run that consists of the least number of cells to get from
    * the start to the finish.
    * @return Ordered list of cells.
    */
   public List<MazeCell> getPathBest()
   {
      return this.pathBest;
   }

   /**
    * Get the total number of cells that have been visited.
    * @return Number of cells.
    */
   public int getCellsVisited()
   {
      return this.visited.size();
   }
}