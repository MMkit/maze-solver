package maze.model;

/**
 * This is a wrapper class for {@link RobotModelMaster}.<br />
 * This class exists to provide limited access to the data models for the AI
 * algorithms.
 * @author Luke Last
 */
public class RobotModel
{
   private final RobotModelMaster parent;

   public RobotModel(RobotModelMaster parent)
   {
      this.parent = parent;
   }

   public java.awt.Dimension getMazeSize()
   {
      return this.parent.getMazeSize();
   }

   public boolean isWallFront()
   {
      return this.parent.isWall(this.getDirection());
   }

   public boolean isWallBack()
   {
      return this.parent.isWall(this.getDirection().getOpposite());
   }

   public boolean isWallLeft()
   {
      return this.parent.isWall(this.getDirection().getLeft());
   }

   public boolean isWallRight()
   {
      return this.parent.isWall(this.getDirection().getRight());
   }

   public MazeCell getCurrentLocation()
   {
      return this.parent.getCurrentLocation();
   }

   public Direction getDirection()
   {
      return this.parent.getDirection();
   }
}