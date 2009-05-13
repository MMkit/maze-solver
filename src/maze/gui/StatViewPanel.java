package maze.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import maze.Main;
import maze.ai.Floodfill;
import maze.ai.LeftWallFollower;
import maze.ai.RightWallFollower;
import maze.ai.RobotBase;
import maze.ai.Tremaux;
import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.MazeInfo;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

public class StatViewPanel extends JPanel{
	private StatTracker tracker;
	private MazeModel maze;
	private RobotBase algorithm;
	
	private DefaultTableModel statTableModel;
	
	private ArrayList<RobotBase> algorithms;
	private ArrayList<String> algorithmNames;
	private JSpinner algorithmSpinner;
	private JComboBox mazes;
	
	public StatViewPanel()
	{
		mazes = new JComboBox();
		ComboBoxModel cbm = Main.getPrimaryFrameInstance().getMazeInfoModel().getMazeInfoComboBoxModel();
		mazes.setModel(cbm);
		
		Box selectionBox = new Box(BoxLayout.X_AXIS);
		selectionBox.add(mazes);
		
		mazes.addActionListener(new ActionListener()
		{
	         public void actionPerformed(ActionEvent e)
	         {
	            Object o = mazes.getSelectedItem();
	            MazeInfo mi = (MazeInfo) o;
	            maze = new Maze(mi.getModel());
	            
				RobotModel robotModel = new RobotModel(new RobotModelMaster(maze,
						new MazeCell(1,16),Direction.North));
				tracker.reload(algorithm, robotModel);
				displayStats();

	         }
	     });
		

		//If time change this to incorporate the RobotBase list
		algorithms = new ArrayList<RobotBase>();
		algorithms.add(new LeftWallFollower());
		algorithms.add(new RightWallFollower());
		algorithms.add(new Tremaux());
		algorithms.add(new Floodfill());
		
		algorithmNames = new ArrayList<String>();
		algorithmNames.add("Left Wall Follower");
		algorithmNames.add("Right Wall Follower");
		algorithmNames.add("Tremaux");
		algorithmNames.add("Floodfill");
		
		SpinnerListModel spinnerListModel = new SpinnerListModel(algorithmNames);
		ChangeListener algorithmChange = new ChangeListener(){
			public void stateChanged(ChangeEvent change){
				String value = (String) algorithmSpinner.getValue();
				int newAlgorithm = algorithmNames.indexOf(value);
				RobotModel robotModel = new RobotModel(new RobotModelMaster(maze,
						new MazeCell(1,16),Direction.North));
				System.out.println(value);
				System.out.println(String.valueOf(newAlgorithm));
				algorithm = algorithms.get(newAlgorithm);
				tracker.reload(algorithm, robotModel);
				displayStats();
			}
		};
		spinnerListModel.addChangeListener(algorithmChange);
		
		algorithmSpinner = new JSpinner(spinnerListModel);
		
		selectionBox.add(algorithmSpinner);
		
		JPanel rightPanel = new JPanel();
		JScrollPane leftSide = new JScrollPane();
		JScrollPane rightSide = new JScrollPane(rightPanel);
		JSplitPane statSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftSide,rightSide);
		this.setLayout(new BorderLayout());
		this.add(statSplitPane, BorderLayout.CENTER);
		BoxLayout boxLayout = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
		rightPanel.setLayout(boxLayout);
		rightPanel.add(selectionBox);
		//Box statBox = new Box(BoxLayout.Y_AXIS);
		//statBox.add(selectionBox);
		
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

		JTable statTable = new JTable(statTableModel);
		statTable.setEnabled(false);
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		statTable.revalidate();

		//statBox.add(statTable);
		
		JScrollPane statPane = new JScrollPane(statTable);
		//statPane.add(statBox);
		
		//this.add(statPane);
		rightPanel.add(statPane);
		//statBox.add(statPane);
		//this.add(statBox);
		//statBox.doLayout();
		
		Object o = mazes.getSelectedItem();
		MazeInfo mi = (MazeInfo) o;
		this.maze = new Maze(mi.getModel());

		this.algorithm = new LeftWallFollower();
		RobotModel robotModel = new RobotModel(new RobotModelMaster(maze,
				new MazeCell(1,16),Direction.North));
		this.tracker = new StatTracker(algorithm,robotModel);
		
		displayStats();
	}

	private void displayStats() {
		
		statTableModel.setValueAt(String.valueOf(tracker.getTotalTraversed()), 0, 1);
		if(tracker.getFirstRunCells() != StatTracker.USELESS)
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
	}
	
}
