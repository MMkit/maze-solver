package maze.gui;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import maze.ai.Floodfill;
import maze.ai.LeftWallFollower;
import maze.ai.RightWallFollower;
import maze.ai.RobotBase;
import maze.ai.Tremaux;
import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

public class StatViewPanel extends JPanel{
	private StatTracker tracker;
	private MazeModel maze;
	private RobotBase algorithm;
	
	private JTextField uniqueSquares;
	private JTextField firstRunSquares;
	private JTextField firstRunTurns;
	private JTextField bestRunSquares;
	private JTextField bestRunTurns;
	private JTextField bestRunTotalSquares;
	private JTextField bestRunTotalTurns;
	
	private ArrayList<RobotBase> algorithms;
	private ArrayList<String> algorithmNames;
	private JSpinner algorithmSpinner;
	
	public StatViewPanel()
	{
		this.maze = new Maze();
		this.algorithm = new Floodfill();
		RobotModel robotModel = new RobotModel(new RobotModelMaster(maze,
				new MazeCell(1,16),Direction.North));
		this.tracker = new StatTracker(algorithm,robotModel);
		
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
				RobotModel robotModel = new RobotModel(new RobotModelMaster(new Maze(),
						new MazeCell(1,16),Direction.North));
				System.out.println(value);
				System.out.println(String.valueOf(newAlgorithm));
				tracker.reload(algorithms.get(newAlgorithm), robotModel);
				displayStats();
			}
		};
		spinnerListModel.addChangeListener(algorithmChange);
		
		algorithmSpinner = new JSpinner(spinnerListModel);
		
		Box statBox = new Box(BoxLayout.Y_AXIS);
		statBox.add(algorithmSpinner);
		
		uniqueSquares = new JTextField();
		uniqueSquares.setEditable(false);
		firstRunSquares = new JTextField();
		firstRunSquares.setEditable(false);
		firstRunTurns = new JTextField();
		firstRunTurns.setEditable(false);
		bestRunSquares = new JTextField();
		bestRunSquares.setEditable(false);
		bestRunTurns = new JTextField();
		bestRunTurns.setEditable(false);
		bestRunTotalSquares = new JTextField();
		bestRunTotalSquares.setEditable(false);
		bestRunTotalTurns = new JTextField();
		bestRunTotalTurns.setEditable(false);
		
		statBox.add(uniqueSquares);
		statBox.add(firstRunSquares);
		statBox.add(firstRunTurns);
		statBox.add(bestRunSquares);
		statBox.add(bestRunTurns);
		statBox.add(bestRunTotalSquares);
		statBox.add(bestRunTotalTurns);
		this.add(statBox);
		
		displayStats();
	}

	private void displayStats() {
		String uniqueString = "Number of Unique Squares Traversed: ";
		String firstSquares = "Number of Cells Traversed to Reach the Center Once: ";
		String firstTurns = "Number of Turns Taken: ";
		String bestSquares = "Number of Cells Traversed on Best Run: ";
		String bestTurns = "Number of Turns Taken: ";
		String bestTotal = "Total Number of Cells Traversed to Complete Best Run: ";
		String bestTotalTurns = "Number of Turns Taken: ";
		
		uniqueSquares.setText(uniqueString + String.valueOf(tracker.getTotalTraversed()));
		if(tracker.getFirstRunCells() != StatTracker.USELESS)
		{
			firstRunSquares.setText(firstSquares + String.valueOf(tracker.getFirstRunCells()));
			firstRunTurns.setText(firstTurns + String.valueOf(tracker.getFirstRunTurns()));
			bestRunSquares.setText(bestSquares + String.valueOf(tracker.getBestRunCells()));
			bestRunTurns.setText(bestTurns + String.valueOf(tracker.getBestRunTurns()));
			bestRunTotalSquares.setText(bestTotal + String.valueOf(tracker.getThroughBestRunCells()));
			bestRunTotalTurns.setText(bestTotalTurns + String.valueOf(tracker.getThroughBestRunTurns()));
		}
		else
		{
			firstRunSquares.setText(firstSquares + "N/A");
			firstRunTurns.setText(firstTurns + "N/A");
			bestRunSquares.setText(bestSquares + "N/A");
			bestRunTurns.setText(bestTurns + "N/A");
			bestRunTotalSquares.setText(bestTotal + "N/A");
			bestRunTotalTurns.setText(bestTotalTurns + "N/A");
		}
	}
	
}
