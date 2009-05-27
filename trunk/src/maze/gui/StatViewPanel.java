package maze.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import maze.ai.RobotBase;
import maze.model.MazeInfo;
import maze.model.MazeModel;

/**
 * @author Vincent Frey
 * @author Luke Last
 */
public class StatViewPanel extends JPanel
{
   private StatTracker tracker;
   private MazeModel maze;
   private RobotBase algorithm;
   private final DefaultTableModel statTableModel;
   private final JComboBox algorithmCombo;
   private final MazeView2 mazeView = new MazeView2();
   private final MazeList mazeList = new MazeList(this.mazeView);

   /**
    * This constructor creates the Panel.
    */
   public StatViewPanel()
   {
      // Recalculate statistics every time the panel is displayed.
      super.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentShown(ComponentEvent e)
         {
            displayStats();
         }
      });

      final Box selectionBox = new Box(BoxLayout.Y_AXIS);

      selectionBox.add(mazeList);
      mazeList.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            try
            {
               //Event is called multiple times when mouse is used.
               //Currently we take a performance hit for that.
               if (isVisible())
               {
                  displayStats();
               }
            }
            catch (RuntimeException e1)
            {
               e1.printStackTrace();
            }
         }
      });

      ActionListener algorithmChange = new ActionListener()
      {
         public void actionPerformed(ActionEvent action)
         {
            algorithm = (RobotBase) algorithmCombo.getSelectedItem();
            displayStats();
         }
      };

      algorithmCombo = new JComboBox(RobotBase.getRobotListModel());
      algorithmCombo.addActionListener(algorithmChange);

      selectionBox.add(algorithmCombo);

      JPanel rightPanel = new JPanel();
      JScrollPane leftSide = new JScrollPane(mazeView);
      final JSplitPane statSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                      leftSide,
                                                      rightPanel);
      this.setLayout(new BorderLayout());
      this.add(statSplitPane, BorderLayout.CENTER);
      statSplitPane.setDividerLocation(.6);
      statSplitPane.setResizeWeight(.5);

      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      rightPanel.add(selectionBox);

      statTableModel = new DefaultTableModel();

      String uniqueString = "Number of Unique Squares Traversed: ";
      String firstSquares = "Number of Cells Traversed to Reach the Center Once: ";
      String firstTurns = "Number of Turns Taken: ";
      String bestSquares = "Number of Cells Traversed on Best Run: ";
      String bestTurns = "Number of Turns Taken: ";
      String bestTotal = "Total Number of Cells Traversed to Complete Best Run: ";
      String bestTotalTurns = "Number of Turns Taken: ";
      String[] rowHeadings = new String[7];
      rowHeadings[0] = uniqueString;
      rowHeadings[1] = firstSquares;
      rowHeadings[2] = firstTurns;
      rowHeadings[3] = bestSquares;
      rowHeadings[4] = bestTurns;
      rowHeadings[5] = bestTotal;
      rowHeadings[6] = bestTotalTurns;

      statTableModel.addColumn("Statistics of Interest", rowHeadings);
      statTableModel.addColumn("Values");

      final JTable statTable = new JTable(statTableModel);
      statTable.setEnabled(false);
      //statTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

      JScrollPane statPane = new JScrollPane(statTable);
      rightPanel.add(statPane);

      MazeInfo mi = (MazeInfo) mazeList.getList().getSelectedValue();
      if (mi != null)
      {
         this.maze = new MazeModel(mi.getModel());
      }

      this.algorithm = (RobotBase) algorithmCombo.getSelectedItem();

      // Set the split pane divider in a delayed manner.
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            statSplitPane.setDividerLocation(.6);
            statSplitPane.setResizeWeight(.6);
            statTable.doLayout();
         }
      });
   } // End constructor.

   /**
    * Set values in the statistics table model. This function displays all of
    * the information for the panel
    */
   private void displayStats()
   {
      Object o = mazeList.getList().getSelectedValue();
      MazeInfo mi = (MazeInfo) o;
      if (mi != null)
      {
         this.maze = new MazeModel(mi.getModel());
      }
      if (this.algorithm != null && this.maze != null)
      {
         if (this.tracker == null)
         {
            this.tracker = new StatTracker(algorithm, maze);
         }
         else
         {
            this.tracker.reload(this.algorithm, this.maze);
         }
         //Set the table stat values from the tracker.
         if (this.tracker != null)
         {
            //First lets display the table
            statTableModel.setValueAt(String.valueOf(tracker.getTotalTraversed()), 0, 1);
            if (tracker.wasCenterFound())
            {
               statTableModel.setValueAt(String.valueOf(tracker.getFirstRunCells()), 1, 1);
               statTableModel.setValueAt(String.valueOf(tracker.getFirstRunTurns()), 2, 1);
               statTableModel.setValueAt(String.valueOf(tracker.getBestRunCells()), 3, 1);
               statTableModel.setValueAt(String.valueOf(tracker.getBestRunTurns()), 4, 1);
               statTableModel.setValueAt(String.valueOf(tracker.getThroughBestRunCells()), 5, 1);
               statTableModel.setValueAt(String.valueOf(tracker.getThroughBestRunTurns()), 6, 1);
            }
            else
            {
               statTableModel.setValueAt("N/A", 1, 1);
               statTableModel.setValueAt("N/A", 2, 1);
               statTableModel.setValueAt("N/A", 3, 1);
               statTableModel.setValueAt("N/A", 4, 1);
               statTableModel.setValueAt("N/A", 5, 1);
               statTableModel.setValueAt("N/A", 6, 1);
            }

            //Now lets display the mazeView
            this.mazeView.setRobotPathModel(this.tracker.getRobotPathModel());
            this.mazeView.setModel(maze);
            // Tell the maze view what to draw.
            this.mazeView.setDrawFog(true);
            this.mazeView.setDrawPathCurrent(false);
            this.mazeView.setDrawPathFirst(true);
            this.mazeView.setDrawPathBest(true);
            this.mazeView.setDrawUnderstanding(false);
         }
      }
   }

}