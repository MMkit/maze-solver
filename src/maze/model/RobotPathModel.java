package maze.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maze.util.ListenerSubject;

public final class RobotPathModel extends ListenerSubject<MazeCell>
{
   private final Set<MazeCell> visited = new HashSet<MazeCell>(128);
   private final List<MazeCell> currentPath = new ArrayList<MazeCell>(128);

   final List<MazeCell> firstPath = new ArrayList<MazeCell>(128);
   final List<MazeCell> bestPath = new ArrayList<MazeCell>(128);

   public void addLocation(MazeCell cell)
   {
      this.visited.add(cell);
      this.currentPath.add(cell);
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

   public List<MazeCell> getPathCurrent()
   {
      return this.currentPath;
   }

   public List<MazeCell> getPathFirst()
   {
      return this.firstPath;
   }

   public List<MazeCell> getPathBest()
   {
      return this.bestPath;
   }

}
