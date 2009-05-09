package maze.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

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

   public MazeViewerPanel(PrimaryFrame primaryFrame)
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
      startButtonPanel.add(new JButton(primaryFrame.startAnimationAction), BorderLayout.NORTH);
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

}
