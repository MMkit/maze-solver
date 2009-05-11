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
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import maze.Main;
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
      final JList mazeList = new JList();
      listPanel.add(mazeList);
      mazeList.setBorder(new TitledBorder("Mazes"));

      //Create a JList to display the AI Algorithms.
      final JPanel aiPanel = new JPanel(new BorderLayout());
      sidePanel.add(aiPanel);
      final JList aiList = new JList();
      aiPanel.add(aiList);
      aiPanel.setBorder(new TitledBorder("Available Algorithms"));

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

      //Temporary
      mazeList.setListData(new Object[]
      {
         "Place Holder"
      });
      aiList.setListData(new Object[]
      {
         "Sweet AI"
      });

      //This is completely a temporary hack that will go away when this is done the right way.
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
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

      private void stopAnimation()
      {
         putValue(Action.NAME, START_NAME);
         putValue(Action.SELECTED_KEY, false);
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
            this.putValue(Action.NAME, STOP_NAME);
            this.putValue(Action.SELECTED_KEY, true);
            currentAnimator = new RobotAnimator(myMazeView, callback);
            currentAnimator.setMovesPerStep(SPEED_STEPS - speedSliderModel.getValue());
            currentAnimator.start();
         }
         else
         { // Animation is already running so stop it.
            currentAnimator.shutdown();
            stopAnimation();
         }

      }
   };

}
