package maze.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import maze.ai.RobotStep;

/**
 * @author Luke Last
 */
public class RobotModel
{
   private final RobotModelMaster parent;

   public RobotModel(RobotModelMaster parent)
   {
      this.parent = parent;
   }

   public Dimension getMazeSize()
   {
      return this.parent.getMazeSize();
   }

   public boolean isWallFront()
   {
      return this.parent.isWallFront();
   }

   public boolean isWallBack()
   {
      return this.parent.isWallBack();
   }

   public boolean isWallLeft()
   {
      return this.parent.isWallLeft();
   }

   public boolean isWallRight()
   {
      return this.parent.isWallRight();
   }

   public MazeCell getCurrentLocation()
   {
      return this.parent.getCurrentLocation();
   }

   public Direction getDirection()
   {
      return this.parent.getDirection();
   }

   public List<MazeCell> getHistory()
   {
      return new ArrayList<MazeCell>(this.parent.getHistory());
   }

   public List<MazeCell> getPathTaken()
   {
      return new ArrayList<MazeCell>(this.parent.getPathTaken());
   }

   @Deprecated
   public void takeNextStep(RobotStep nextStep)
   {
      this.parent.takeNextStep(nextStep);
   }

   public boolean isExplored(MazeCell location)
   {
      return this.parent.isExplored(location);
   }

   @Deprecated
   public Set<MazeCell> getNonHistory()
   {
      return this.parent.getNonHistory();
   }

   @Deprecated
   public List<MazeCell> getFirstRun()
   {
      return this.parent.getFirstRun();
   }

   @Deprecated
   public List<MazeCell> getBestRun()
   {
      return this.parent.getBestRun();
   }

}
