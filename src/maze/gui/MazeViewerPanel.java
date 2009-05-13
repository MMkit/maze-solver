package maze.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import maze.Main;
import maze.ai.RobotBase;
import maze.model.MazeInfo;

/**
 * This is the main panel that displays the read only maze that is viewed when
 * the micro mouse animation is run.
 * @author Luke Last
 */
public class MazeViewerPanel extends JPanel
{
   private static final int SIDEBAR_WIDTH = 150;
   private final MazeView myMazeView = new MazeView();
   private final JList mazeList = new JList();
   private final JList aiList = new JList();
   private volatile RobotAnimator currentAnimator = null;
   private final BoundedRangeModel speedSliderModel;
   private static final int SPEED_STEPS = 25;

   public MazeViewerPanel()
   {
      this.setLayout(new BorderLayout());
      this.add(this.myMazeView);

      final JPanel sidePanel = new JPanel();
      this.add(sidePanel, BorderLayout.EAST);
      sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
      sidePanel.setBorder(new TitledBorder("Configuration Options"));
      //Set the side panels fixed width.
      sidePanel.add(Box.createRigidArea(new Dimension(SIDEBAR_WIDTH, 6)));
      sidePanel.setSize(300, 100);
      sidePanel.setMinimumSize(new Dimension(300, 300));

      //Create a panel to hold the list so it will maximize it.
      final JScrollPane mazeListPane = new JScrollPane(this.mazeList);
      sidePanel.add(mazeListPane);
      mazeListPane.setBorder(new TitledBorder("Mazes"));
      ComboBoxModel cbm = Main.getPrimaryFrameInstance().getMazeInfoModel().getMazeInfoComboBoxModel();
      mazeList.setModel(cbm);
      mazeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      mazeList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            int index = ((ListSelectionModel) e.getSource()).getMaxSelectionIndex();
            if (index == -1)
               return;
            Object o = mazeList.getModel().getElementAt(index);
            MazeInfo mi = (MazeInfo) o;
            myMazeView.setModel(mi.getModel());
         }
      });

      //Create a JList to display the AI Algorithms.
      final JScrollPane aiScrollPane = new JScrollPane(this.aiList);
      sidePanel.add(aiScrollPane);
      aiScrollPane.setBorder(new TitledBorder("Available Algorithms"));
      this.aiList.setModel(RobotBase.getRobotListModel());
      this.aiList.setSelectedIndex(0);

      //Create animation speed slider.
      final JSlider speedSlider = new JSlider();
      sidePanel.add(speedSlider);
      speedSlider.setBorder(new TitledBorder("Simulation Speed"));

      this.speedSliderModel = speedSlider.getModel();
      this.speedSliderModel.setMinimum(0);
      this.speedSliderModel.setValue((int)(SPEED_STEPS / 1.4));
      this.speedSliderModel.setMaximum(SPEED_STEPS);
      this.speedSliderModel.addChangeListener(new ChangeListener()
      {

         @Override
         public void stateChanged(ChangeEvent e)
         {
            if (currentAnimator != null)
            {
               currentAnimator.setMovesPerStep(SPEED_STEPS - speedSliderModel.getValue());
            }
         }
      });

      //Add the start animation button.
      final JToggleButton startButton = new JToggleButton(this.startAnimationAction);
      sidePanel.add(startButton);
      startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      startButton.setBorder(new EmptyBorder(8, 5, 8, 5));
      startButton.setMaximumSize(new Dimension(400, 200));

   }

   /**
    * An action used to create menu items and buttons to start the robot
    * animation sequence.
    */
   final Action startAnimationAction = new AbstractAction()
   {
      private static final String START_NAME = "Start Mouse Simulation";
      private static final String STOP_NAME = "Stop Mouse Simulation";
      {
         this.putValue(Action.NAME, START_NAME);
      }

      private void startAnimation()
      {
         this.putValue(Action.NAME, STOP_NAME);
         this.putValue(Action.SELECTED_KEY, true);
         mazeList.setEnabled(false);
         aiList.setEnabled(false);
         Main.getPrimaryFrameInstance().setSimulation(true);
      }

      private void stopAnimation()
      {
         putValue(Action.NAME, START_NAME);
         putValue(Action.SELECTED_KEY, false);
         mazeList.setEnabled(true);
         aiList.setEnabled(true);
         currentAnimator = null;
         Main.getPrimaryFrameInstance().setSimulation(false);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         //If the animator is null that start a new one.
         if (currentAnimator == null)
         {
            if (myMazeView.getModel() == null)
            {
               JOptionPane.showMessageDialog(MazeViewerPanel.this,
                                             "You must select a maze before running a simulation.",
                                             "Simulation Error",
                                             JOptionPane.ERROR_MESSAGE,
                                             null);
               this.putValue(Action.SELECTED_KEY, false);
            }
            else
            {
               //Create a callback to enable the action after the animator is done running.
               final Runnable callback = new Runnable()
               {
                  @Override
                  public void run()
                  {
                     stopAnimation();
                  }
               };
               this.startAnimation();
               try
               {
                  currentAnimator = new RobotAnimator(myMazeView,
                                                      (RobotBase) aiList.getSelectedValue(),
                                                      callback);
                  currentAnimator.setMovesPerStep(SPEED_STEPS - speedSliderModel.getValue());
                  currentAnimator.start();
               }
               catch (Exception ex)
               {

                  this.stopAnimation();
                  ex.printStackTrace();
               }
            }
         }
         else
         { // Animation is already running so stop it.
            currentAnimator.shutdown();
            this.stopAnimation();
         }

      }
   };

}
