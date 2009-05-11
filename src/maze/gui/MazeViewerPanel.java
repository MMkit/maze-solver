package maze.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
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
   private static final int SPEED_STEPS = 20;

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
      final JPanel listPanel = new JPanel(new BorderLayout());
      sidePanel.add(listPanel);

      //Create a JList to hold the current mazes.
      listPanel.add(mazeList);
      mazeList.setBorder(new TitledBorder("Mazes"));
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
      final JPanel aiPanel = new JPanel(new BorderLayout());
      sidePanel.add(aiPanel);
      aiPanel.add(this.aiList);
      aiPanel.setBorder(new TitledBorder("Available Algorithms"));
      this.aiList.setListData(RobotBase.MASTER_AI_LIST);
      this.aiList.setSelectedIndex(0);

      //Add the start animation button.
      final JPanel startButtonPanel = new JPanel(new BorderLayout());
      startButtonPanel.add(new JToggleButton(this.startAnimationAction), BorderLayout.NORTH);
      sidePanel.add(startButtonPanel);

      final JSlider speedSlider = new JSlider();
      sidePanel.add(speedSlider);
      speedSlider.setBorder(new TitledBorder("Simulation Speed"));

      this.speedSliderModel = speedSlider.getModel();
      this.speedSliderModel.setMinimum(0);
      this.speedSliderModel.setValue(SPEED_STEPS / 2);
      this.speedSliderModel.setMaximum(SPEED_STEPS - 2);

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
      }

      private void stopAnimation()
      {
         putValue(Action.NAME, START_NAME);
         putValue(Action.SELECTED_KEY, false);
         mazeList.setEnabled(true);
         aiList.setEnabled(true);
         currentAnimator = null;
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         if (currentAnimator == null)
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
            currentAnimator = new RobotAnimator(myMazeView, (RobotBase)aiList.getSelectedValue(), callback);
            currentAnimator.setMovesPerStep(SPEED_STEPS - speedSliderModel.getValue());
            currentAnimator.start();
         }
         else
         { // Animation is already running so stop it.
            currentAnimator.shutdown();
            this.stopAnimation();
         }

      }
   };

}
