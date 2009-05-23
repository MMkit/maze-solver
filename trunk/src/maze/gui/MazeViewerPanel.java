package maze.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import maze.Main;
import maze.ai.RobotBase;
import maze.gui.RobotAnimator.AnimationStates;

/**
 * This is the main panel that displays the read only maze that is viewed when
 * the micro mouse animation is run.
 * @author Luke Last
 */
public final class MazeViewerPanel extends JPanel
{
   private static final int SIDEBAR_WIDTH = 150;
   private static final int SPEED_STEPS = 25;
   private final MazeView2 myMazeView = new MazeView2();
   private final MazeList mazeList = new MazeList(this.myMazeView);
   private final JList aiList = new JList();
   private final RobotAnimator animator = new RobotAnimator();
   private final BoundedRangeModel speedSliderModel;

   private final ImageIcon iconPlay = Main.getImageResource("gui/images/play.png");
   private final ImageIcon iconPlayOn = Main.getImageResource("gui/images/play-on.png");
   private final ImageIcon iconStop = Main.getImageResource("gui/images/stop-disabled.png");
   private final ImageIcon iconStopRed = Main.getImageResource("gui/images/stop-red.png");
   private final ImageIcon iconPauseDisabled = Main.getImageResource("gui/images/pause-disabled.png");
   private final ImageIcon iconPauseOn = Main.getImageResource("gui/images/pause-on.png");
   private final ImageIcon iconPause = Main.getImageResource("gui/images/pause.png");

   /**
    * Constructor. Builds the Swing components for this panel.
    */
   public MazeViewerPanel()
   {
      this.setLayout(new BorderLayout());
      this.add(this.myMazeView);

      final JPanel sidePanel = new JPanel();
      this.add(sidePanel, BorderLayout.EAST);
      sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
      //Set the side panels fixed width.
      sidePanel.add(Box.createRigidArea(new Dimension(SIDEBAR_WIDTH, 6)));

      this.mazeList.setToolTipText("<html>A list of available mazes that you can run simulations on."
                                   + "<br>You can load more or create new ones in the maze editor.</html>");
      sidePanel.add(this.mazeList);

      //Create a JList to display the AI Algorithms.
      final JScrollPane aiScrollPane = new JScrollPane(this.aiList);
      sidePanel.add(aiScrollPane);
      aiScrollPane.setBorder(new TitledBorder("Available Algorithms"));
      aiScrollPane.setToolTipText("<html>Available AI algorithms.<br>"
                                  + "You can create more in the script editor.</html>");
      this.aiList.setModel(RobotBase.getRobotListModel());
      this.aiList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      this.aiList.setSelectedIndex(0);

      // Create graphical options checkboxe's.
      final Box optionsPanel = Box.createHorizontalBox();
      sidePanel.add(optionsPanel);
      optionsPanel.setBorder(BorderFactory.createTitledBorder("Simulation Graphics Options"));
      optionsPanel.add(Box.createHorizontalGlue());
      optionsPanel.add(new JCheckBox(this.fogAction));
      optionsPanel.add(Box.createHorizontalGlue());
      optionsPanel.add(new JCheckBox(this.pathCurrentAction));
      optionsPanel.add(Box.createHorizontalGlue());
      optionsPanel.add(new JCheckBox(this.drawUnderstandingAction));
      optionsPanel.add(Box.createHorizontalGlue());
      optionsPanel.add(new JCheckBox(this.themeToggleAction));
      optionsPanel.add(Box.createHorizontalGlue());

      //Create animation speed slider.
      final JPanel sliderPanel = new JPanel();
      sidePanel.add(sliderPanel);
      final JSlider speedSlider = new JSlider(0, SPEED_STEPS, (int) (SPEED_STEPS * .7));
      sliderPanel.add(speedSlider);
      sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
      sliderPanel.setBorder(new TitledBorder("Simulation Speed"));
      sliderPanel.setToolTipText("<html>Controls the animation speed of the Micro Mouse<br> as it travels through the maze.</html>");
      Dictionary<Object, Object> sliderLabels = new Hashtable<Object, Object>();
      sliderLabels.put(0, new JLabel("Slow"));
      sliderLabels.put(SPEED_STEPS, new JLabel("Fast"));
      speedSlider.setLabelTable(sliderLabels);
      speedSlider.setPaintLabels(true);

      this.speedSliderModel = speedSlider.getModel();
      this.speedSliderModel.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            if (animator != null)
            {
               animator.setMovesPerStep(SPEED_STEPS - speedSliderModel.getValue());
            }
         }
      });

      //Build the simulation/animation control buttons.
      final JPanel controlPanel = new JPanel();
      sidePanel.add(controlPanel);
      controlPanel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));
      controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
      controlPanel.add(new JButton(this.stopAnimation));
      controlPanel.add(Box.createHorizontalGlue());
      controlPanel.add(new JButton(this.pauseAnimation));
      controlPanel.add(Box.createHorizontalGlue());
      controlPanel.add(new JButton(this.playAnimation));

      this.setAnimationButtonStates();
   }

   /**
    * This action controls the play animation/simulation button.
    */
   private final Action playAnimation = new AbstractAction()
   {
      {
         this.putValue(Action.SHORT_DESCRIPTION, "Start a simulation.");
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         switch (animator.getState())
         {
            case Stopped :
               simulationStart();
               break;
            case Paused :
               animator.setState(AnimationStates.Running);
               break;
         }
         setAnimationButtonStates();
      }
   };

   /**
    * This action controls the stop animation/simulation button.
    */
   private final Action stopAnimation = new AbstractAction()
   {
      {
         this.putValue(Action.SHORT_DESCRIPTION, "Stop the simulation and reset the maze.");
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         simulationStop();
         setAnimationButtonStates();
      }
   };

   /**
    * This action controls the pause animation/simulation button.
    */
   private final Action pauseAnimation = new AbstractAction()
   {
      {
         this.putValue(Action.SHORT_DESCRIPTION, "Pause/Resume a simulation.");
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         switch (animator.getState())
         {
            case Running :
               animator.setState(AnimationStates.Paused);
               break;
            case Paused :
               animator.setState(AnimationStates.Running);
               break;
            case Stopped :
               break;
         }
         setAnimationButtonStates();
      }
   };

   /**
    * Sets the displayed images on all the simulation buttons from the current
    * state of the animator.
    */
   private void setAnimationButtonStates()
   {
      switch (animator.getState())
      {
         case Stopped :
            playAnimation.putValue(Action.LARGE_ICON_KEY, iconPlay);
            pauseAnimation.putValue(Action.LARGE_ICON_KEY, iconPauseDisabled);
            stopAnimation.putValue(Action.LARGE_ICON_KEY, iconStop);
            break;
         case Paused :
            playAnimation.putValue(Action.LARGE_ICON_KEY, iconPlay);
            pauseAnimation.putValue(Action.LARGE_ICON_KEY, iconPauseOn);
            stopAnimation.putValue(Action.LARGE_ICON_KEY, iconStopRed);
            break;
         case Running :
            playAnimation.putValue(Action.LARGE_ICON_KEY, iconPlayOn);
            pauseAnimation.putValue(Action.LARGE_ICON_KEY, iconPause);
            stopAnimation.putValue(Action.LARGE_ICON_KEY, iconStopRed);
            break;
      }
   }

   /**
    * Start the animation.
    */
   private void simulationStart()
   {
      if (this.animator.getState() == AnimationStates.Stopped)
      {
         if (myMazeView.getModel() == null)
         {
            JOptionPane.showMessageDialog(this,
                                          "You must select a maze before running a simulation.",
                                          "Can Not Run Simulation",
                                          JOptionPane.WARNING_MESSAGE);
         }
         else
         {
            //Create a callback to enable the action after the animator is done running.
            final Runnable callback = new Runnable()
            {
               @Override
               public void run()
               {
                  //We need to make sure that this runs on the swing thread.
                  EventQueue.invokeLater(new Runnable()
                  {
                     @Override
                     public void run()
                     {
                        simulationStop();
                     }
                  });
               }
            };
            try
            {
               Object o = this.aiList.getSelectedValue();
               if (o != null)
               {
                  this.animator.start(this.myMazeView,
                                      (RobotBase) aiList.getSelectedValue(),
                                      callback);
                  this.animator.setMovesPerStep(SPEED_STEPS - speedSliderModel.getValue());
               }
               else
               {
                  JOptionPane.showMessageDialog(this,
                                                "You must select an algorithm before running a simulation.",
                                                "Can Not Run Simulation",
                                                JOptionPane.WARNING_MESSAGE);
                  throw new Exception("No algorithm selected");
               }
            }
            catch (Exception ex)
            {
               ex.printStackTrace();
               this.simulationStop();
               return;
            }
            this.mazeList.getList().setEnabled(false);
            aiList.setEnabled(false);
            Main.getPrimaryFrameInstance().setSimulation(true);
         }
      }
   }

   /**
    * Stop the animation.
    */
   private void simulationStop()
   {
      this.animator.setState(AnimationStates.Stopped);
      this.mazeList.getList().setEnabled(true);
      aiList.setEnabled(true);
      Main.getPrimaryFrameInstance().setSimulation(false);
      this.myMazeView.setRobotPosition(null, 0);
      this.myMazeView.invalidateAllCells();
      this.setAnimationButtonStates();
   }

   /**
    * Control whether fog of war is drawn.
    */
   private final Action fogAction = new AbstractAction()
   {
      {
         this.putValue(Action.NAME, "Fog");
         this.putValue(Action.SHORT_DESCRIPTION, "Displays a fog of war over unexplored cells.");
         this.putValue(Action.SELECTED_KEY, true);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         myMazeView.setDrawFog(this.getValue(Action.SELECTED_KEY) == Boolean.TRUE);
      }
   };

   /**
    * Control whether the current path is drawn.
    */
   private final Action pathCurrentAction = new AbstractAction()
   {
      {
         this.putValue(Action.NAME, "Path");
         this.putValue(Action.SHORT_DESCRIPTION, "Overlays the path the robot has taken.");
         this.putValue(Action.SELECTED_KEY, true);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         boolean draw = this.getValue(Action.SELECTED_KEY) == Boolean.TRUE;
         myMazeView.setDrawPathCurrent(draw);
         myMazeView.setDrawPathBest(draw);
         myMazeView.setDrawPathFirst(draw);
      }
   };

   /**
    * Used to create a check box for toggling on/off whether or not to draw the
    * understanding information.
    */
   private final Action drawUnderstandingAction = new AbstractAction()
   {
      {
         this.putValue(Action.NAME, "Info");
         this.putValue(Action.SHORT_DESCRIPTION,
                       "<html>Displays algorithm specific information on the maze.<br />"
                             + "This only affects certain AI algorithms.</html>");
         this.putValue(Action.SELECTED_KEY, true);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         myMazeView.setDrawUnderstanding(this.getValue(Action.SELECTED_KEY) == Boolean.TRUE);
      }
   };

   private final Action themeToggleAction = new AbstractAction()
   {
      {
         this.putValue(Action.NAME, "Classic");
         this.putValue(Action.SHORT_DESCRIPTION, "Use a simple theme.");
         this.putValue(Action.SELECTED_KEY, false);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         if (this.getValue(Action.SELECTED_KEY) == Boolean.TRUE)
         {
            myMazeView.setPainterDelegate(new MazePainterClassic());
         }
         else
         {
            myMazeView.setPainterDelegate(new MazePainterDefault());
         }
      }
   };

}
