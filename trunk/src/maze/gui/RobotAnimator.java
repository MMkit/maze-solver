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
   /**
    * Represents the possible states that a robot animator can be in.
    */
   public static enum AnimationStates
   {
      Paused,
      Running,
      Stopped,
   }

   /**
    * Transforms one fraction into another in a non-linear fashion to provide
    * for acceleration and the deceleration. If the input is a linear sequence
    * the output will be a sequence that accelerates from 0 to .5 and the
    * decelerates from .5 to 1.
    * @param input A value between 0 and 1.
    * @return A transformed value between 0 and 1.
    */
   private static final double accelerationTransform(double input)
   {
      double result = 0;
      if (input < .5)
      {
         result = input * input * 2;
      }
      else
      {
         result = .5 + (input - .5) * (2.5 - input);
      }
      // Make sure the result is between 0 and 1.
      result = (result > 1.0) ? 1.0 : result;
      result = (result < 0) ? 0 : result;
      return result;
   }

   /**
    * The state that this animator is currently in.
    */
   private volatile AnimationStates currentState = AnimationStates.Stopped;
   /**
    * A call back method which is run after the animator is stopped.
    */
   private Runnable finishedCallback;
   /**
    * Number of frames of animation to display between steps.
    */
   private int movesPerStep = 10;

   /**
    * The thread running our animation loop.
    */
   private Thread processingThread;

   /**
    * The controller used to simulate the AI algorithm.
    */
   private RobotController robot;

   /**
    * The time to sleep between rendering frames.
    */
   private int sleepTime = 1000 / 50;

   /**
    * the maze view that this animator is attached to and updates with each
    * animation frame.
    */
   private MazeView view;

   /**
    * The maximum frames per second this animator is trying to put out.
    * @return FPS
    */
   public int getFPS()
   {
      return 1000 / this.sleepTime;
   }

   /**
    * Get the number of intermediate frames of animation that take place between
    * each step of the robot.
    */
   public int getMovesPerStep()
   {
      return movesPerStep;
   }

   /**
    * Get the current state that this robot animator is in.
    */
   public AnimationStates getState()
   {
      return this.currentState;
   }

   /**
    * This runs in a background thread and handles the animation loop.
    */
   @Override
   public void run()
   {
      final RobotModelMaster model = this.robot.getRobotModelMaster();
      this.view.setRobotPosition(this.view.getCellCenterInner(model.getCurrentLocation()),
                                 model.getDirection().getRadians());
      this.view.invalidateAllCells();
      this.view.setRobotPathModel(model.getRobotPathModel());
      this.setViewAttributes();
      while (this.currentState != AnimationStates.Stopped && this.robot.isRobotDone() == false)
      {
         try
         {
            //Get the robots current position.
            final Point srcLocation = this.view.getCellCenterInner(model.getCurrentLocation());
            final Direction srcDirection = model.getDirection();
            final double srcRotation = srcDirection.getRadians();

            robot.nextStep(); //Move robot.

            //Get the robots new position.
            final Point destLocation = this.view.getCellCenterInner(model.getCurrentLocation());
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
               double rot = srcRotation +
                            (destRotation - srcRotation) *
                            accelerationTransform(percentage);
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
      this.view.setRobotPathModel(null);
      this.view.loadUnderstanding(null);
      this.view.loadUnderstandingDir(null);
      if (this.finishedCallback != null)
      {
         this.finishedCallback.run();
      }
      this.processingThread = null;
   }

   /**
    * The animator will try and render at most this many frames per second.
    */
   public void setFPS(int framesPerSecond)
   {
      this.sleepTime = 1000 / framesPerSecond;
   }

   public void setMovesPerStep(int movesPerStep)
   {
      if (movesPerStep < 1)
         this.movesPerStep = 1;
      else
         this.movesPerStep = movesPerStep;
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
         if (state == AnimationStates.Stopped && this.processingThread != null)
         {
            this.processingThread.interrupt();
         }
      }
   }

   /**
    * Loads information from the AI algorithm in the controller to the view for
    * drawing.
    */
   private void setViewAttributes()
   {
      this.view.loadUnderstanding(this.robot.getUnderstandingInt());
      this.view.loadUnderstandingDir(this.robot.getUnderstandingDir());
   }

   /**
    * Start and initialize this.
    * @param mazeView The view to be controlled by this animator.
    * @param robotAlgorithm The AI algorithm to use for the animation.
    * @param finishedCallback This is called after the animation is stopped.
    */
   public void start(MazeView mazeView, RobotBase robotAlgorithm, Runnable finishedCallback)
   {
      if (this.processingThread != null && this.processingThread.isAlive())
      {
         this.setState(AnimationStates.Stopped);
         //Make sure the thread is finished before continuing.
         try
         {
            this.processingThread.join();
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }
      this.view = mazeView;
      this.finishedCallback = finishedCallback;
      this.robot = new RobotController(this.view.getModel(), robotAlgorithm);
      this.processingThread = new Thread(this, "Robot Animator");
      this.processingThread.setDaemon(true);
      this.currentState = AnimationStates.Running;
      this.processingThread.start();
   }

}