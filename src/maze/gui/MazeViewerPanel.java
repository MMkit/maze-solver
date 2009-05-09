package maze.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * This is the main panel that displays the read only maze that is viewed when
 * the micro mouse animation is run.
 * @author Luke Last
 */
public class MazeViewerPanel extends JPanel
{
   private static final int SIDEBAR_WIDTH = 150;
   private final MazeView myMazeView = new MazeView();

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
      startButtonPanel.add(new JButton(this.startAnimationAction), BorderLayout.NORTH);
      sidePanel.add(startButtonPanel);

      //Temporary
      mazeList.setListData(new Object[]
      {
         "Place Holder"
      });
      aiList.setListData(new Object[]
      {
         "Sweet AI"
      });

   }

   /**
    * An action used to create menu items and buttons to start the robot
    * animation sequence.
    */
   final Action startAnimationAction = new AbstractAction()
   {
      {
         this.putValue(Action.NAME, "Start Mouse Animation");
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         this.setEnabled(false);
         //Create a callback to enable the action after the animator is done running.
         Runnable callback = new Runnable()
         {
            @Override
            public void run()
            {
               startAnimationAction.setEnabled(true);
            }
         };
         //TODO Will have to save this reference somewhere so we can talk to it later.
         RobotAnimator animator = new RobotAnimator(myMazeView, callback);
         animator.start();

      }
   };

}
