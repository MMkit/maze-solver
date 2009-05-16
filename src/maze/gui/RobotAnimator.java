package maze.gui;

import java.awt.Point;

import javax.swing.JOptionPane;

import maze.Main;
import maze.ai.RobotBase;
import maze.ai.RobotController;
import maze.model.Direction;
import maze.model.RobotModelMaster;

/**
 * Animates a MicroMouse robot against a maze view.
 * @author Luke Last
 */
public final class RobotAnimator implements Runnable
{
   private MazeView view;
   private RobotController controller;
   /**
    * The state that this animator is currently in.
    */
   private volatile AnimationStates currentState = AnimationStates.Stopped;
   /**
    * A call back method which is run after the animator is stopped.
    */
   private Runnable finishedCallback;
   /**
    * The time to sleep between rendering frames.
    */
   private int sleepTime = 1000 / 50;

   /**
    * Number of frames of animation to display between steps.
    */
   private int movesPerStep = 10;

   /**
    * The thread running our animation loop.
    */
   private volatile Thread processingThread;

   /**
    * Start and initialize this.
    * @param mazeView The view to be controlled by this animator.
    * @param robotAlgorithm The AI algorithm to use for the animation.
    * @param finishedCallback This is called after the animation is stopped.
    */
   public void start(MazeView mazeView, RobotBase robotAlgorithm, Runnable finishedCallback)
   {
      if (this.processingThread != null)
      {
         this.setState(AnimationStates.Stopped);
         //Make sure the thread is finished before continuing.
         while (this.processingThread != null)
         {
            Thread.yield();
         }
      }
      this.view = mazeView;
      this.finishedCallback = finishedCallback;
      this.controller = new RobotController(this.view.getModel(), robotAlgorithm);
      this.processingThread = new Thread(this, "Robot Animator");
      this.processingThread.setDaemon(true);
      this.currentState = AnimationStates.Running;
      this.processingThread.start();
   }

   @Override
   public void run()
   {
      final RobotModelMaster model = this.controller.getRobotModelMaster();
      this.view.setRobotPosition(this.view.getCellCenter(model.getCurrentLocation()),
                                 model.getDirection().getRadians());
      view.setDrawFog(true);
      view.setDrawFirstRun(true);
      view.setDrawBestRun(true);
      view.setDrawCurrentRun(true);
      this.setViewAttributes();
      while (this.currentState != AnimationStates.Stopped && this.controller.isRobotDone() == false)
      {
         try
         {
            //Get the robots current position.
            final Point srcLocation = this.view.getCellCenter(model.getCurrentLocation());
            final Direction srcDirection = model.getDirection();
            final double srcRotation = srcDirection.getRadians();

            controller.nextStep(); //Move robot.

            //Get the robots new position.
            final Point destLocation = this.view.getCellCenter(model.getCurrentLocation());
            final Direction destDirection = model.getDirection();
            //Set our new rotation based on which way we turned.
            final double destRotation;
            if (srcDirection.getLeft() == destDirection)
               destRotation = srcRotation - Math.PI / 2;
            else if (srcDirection.getRight() == destDirection)
               destRotation = srcRotation + Math.PI / 2;
            else
               destRotation = srcRotation; //Didn't rotate.

            //Increment is fraction at a time to the destination position.
            for (int inc = 1; inc <= this.movesPerStep; inc++)
            {
               final double percentage = (double) inc / this.movesPerStep;
               int x = (int) (srcLocation.x + (destLocation.x - srcLocation.x) * percentage);
               int y = (int) (srcLocation.y + (destLocation.y - srcLocation.y) * percentage);
               double rot = srcRotation + (destRotation - srcRotation) * percentage;
               this.view.setRobotPosition(new Point(x, y), rot);
               Thread.sleep(this.sleepTime);
            }
            
            this.setViewAttributes();

            while (this.currentState == AnimationStates.Paused)
            {
               Thread.sleep(100);
            }
         }
         catch (InterruptedException e)
         {
            // Means the thread is being shutdown.
         }
         catch (Exception e)
         {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Main.getPrimaryFrameInstance(),
                                          e.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE,
                                          null);
            this.currentState = AnimationStates.Stopped;
         }
      }
      if (this.finishedCallback != null)
      {
         this.finishedCallback.run();
      }
      this.processingThread = null;
   }

   private void setViewAttributes()
   {
      view.loadUnexplored(controller.getAllUnexplored());
      view.loadFirstRun(controller.getFirstRun());
      view.loadBestRun(controller.getBestRun());
      view.loadCurrentRun(controller.getCurrentRun());

      int[][] understandingInt = controller.getUnderstandingInt();
      Direction[][] understandingDir = controller.getUnderstandingDir();
      if (understandingInt != null)
      {
         view.loadUnderstanding(understandingInt);
         view.setDrawUnderstanding(true);
      }
      else if (understandingDir != null)
      {
         view.loadUnderstandingDir(understandingDir);
         view.setDrawUnderstanding(true);
      }
      else
      {
         view.setDrawUnderstanding(false);
      }
   }

   /**
    * Get the current state that this robot animator is in.
    */
   public AnimationStates getState()
   {
      return this.currentState;
   }

   /**
    * Change the state of this robot animator. If the current state is
    * <code>Stopped</code> you cannot change it here, you must use the
    * <code>start(...)</code> method.
    * @param state The new state you want to go to.
    */
   public void setState(final AnimationStates state)
   {
      if (this.currentState != AnimationStates.Stopped)
      {
         this.currentState = state;
         if (state == AnimationStates.Stopped)
         {
            this.processingThread.interrupt();
         }
      }
   }

   public int getFPS()
   {
      return 1000 / this.sleepTime;
   }

   /**
    * The animator will try and render at most this many frames per second.
    */
   public void setFPS(int framesPerSecond)
   {
      this.sleepTime = 1000 / framesPerSecond;
   }

   /**
    * Get the number of intermediate frames of animation that take place between
    * each step of the robot.
    */
   public int getMovesPerStep()
   {
      return movesPerStep;
   }

   public void setMovesPerStep(int movesPerStep)
   {
      if (movesPerStep < 1)
         this.movesPerStep = 1;
      else
         this.movesPerStep = movesPerStep;
   }

   /**
    * Represents the possible states that a robot animator can be in.
    */
   public static enum AnimationStates
   {
      Running,
      Paused,
      Stopped,
   }

}
