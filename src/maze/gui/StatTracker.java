/**
 * 
 */
package maze.gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import maze.ai.Floodfill;
import maze.ai.RightWallFollower;
import maze.ai.RobotBase;
import maze.ai.RobotStep;
import maze.ai.Tremaux;
import maze.model.Direction;
import maze.model.Maze;
import maze.model.MazeCell;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

/**
 * @author Vincent Frey
 *
 */
public class StatTracker {

	/**
	 * @param args
	 */
	private int totalSquaresTraversed;
	private int totalTurnsTaken;
	private int firstRunSquaresTraversed;
	private int firstRunTurnsTaken;
	private int bestRunSquaresTraversed;
	private int bestRunTurnsTaken;
	private int bestRunTotalSquaresTraversed;
	private int bestRunTotalTurnsTaken;
	private int previousRunTotalSquaresTraversed;
	private int previousRunTotalTurnsTaken;
	private int currentRunSquaresTraversed;
	private int currentRunTurnsTaken;
	private boolean[][] explored;
	public static int USELESS = -1;
	public static int HOPELESS = 10000;
	
	
	
	private RobotBase algorithm;
	private RobotModel mouse;
	private Dimension mazeSize;
	
	public StatTracker(RobotBase algorithm, RobotModel mouse)
	{
		this.algorithm = algorithm;
		this.mouse = mouse;
		
		this.initialize();
		this.recompute();
	}
	
	public void reload(RobotBase algorithm,
			RobotModel mouse)
	{
		this.algorithm = algorithm;
		this.mouse = mouse;
		
		this.initialize();
		this.recompute();
	}
		

	private void initialize() {
		mazeSize = mouse.getMazeSize();
		explored = new boolean[mazeSize.width][mazeSize.width];
		for (int i = 0; i < mazeSize.width; i++)
		{
			for (int j = 0; j < mazeSize.height; j++)
			{
				explored[i][j] = false;
			}
		}
		totalSquaresTraversed = 0;
		totalTurnsTaken = 0;
		firstRunSquaresTraversed = USELESS;
		firstRunTurnsTaken = USELESS;
		bestRunSquaresTraversed = USELESS;
		bestRunTurnsTaken = USELESS;
		bestRunTotalSquaresTraversed = USELESS;
		bestRunTotalTurnsTaken = USELESS;
		previousRunTotalSquaresTraversed = 0;
		previousRunTotalTurnsTaken = 0;
	}
	
	private void recompute()
	{
		setExplored();
		trackARun();
		if( totalSquaresTraversed == HOPELESS )
		{
			if( currentRunSquaresTraversed != HOPELESS )
			{ //Just in case the mouse makes it to the center but not back
				firstRunSquaresTraversed = currentRunSquaresTraversed;
				firstRunTurnsTaken = currentRunTurnsTaken;
				bestRunSquaresTraversed = currentRunSquaresTraversed;
				bestRunTurnsTaken = currentRunTurnsTaken;
				bestRunTotalSquaresTraversed = currentRunSquaresTraversed;
				bestRunTotalTurnsTaken = currentRunTurnsTaken;
			}
			return;
		}
		
		firstRunSquaresTraversed = currentRunSquaresTraversed;
		firstRunTurnsTaken = currentRunTurnsTaken;
		
		do
		{
			bestRunSquaresTraversed = currentRunSquaresTraversed;
			bestRunTurnsTaken = currentRunTurnsTaken;
			bestRunTotalSquaresTraversed = previousRunTotalSquaresTraversed + currentRunSquaresTraversed;
			bestRunTotalTurnsTaken = previousRunTotalTurnsTaken + currentRunTurnsTaken;
			previousRunTotalSquaresTraversed = totalSquaresTraversed;
			previousRunTotalTurnsTaken = totalTurnsTaken;
			trackARun();
		}while(bestRunSquaresTraversed > currentRunSquaresTraversed);
	}

	private void trackARun() {
		RobotStep nextStep;
		
		currentRunSquaresTraversed = 0;
		currentRunTurnsTaken = 0;
		
		while( (totalSquaresTraversed < HOPELESS) && (isAtCenter() == false) )
		{
			nextStep = algorithm.nextStep();
			mouse.takeNextStep(nextStep);
			if(isATurn(nextStep))
			{
				totalTurnsTaken++;
				currentRunTurnsTaken++;
			}
			else
			{
				totalSquaresTraversed++;
				currentRunSquaresTraversed++;
				setExplored();
			}
		}

		while( (totalSquaresTraversed < HOPELESS) && (isAtStart() == false) )
		{
			nextStep = algorithm.nextStep();
			mouse.takeNextStep(nextStep);
			if(isATurn(nextStep))
			{
				totalTurnsTaken++;
			}
			else
			{
				totalSquaresTraversed++;
				setExplored();
			}
		}

	}

	private boolean isAtStart() {
		MazeCell here = mouse.getCurrentLocation();
		MazeCell start = new MazeCell(1, mazeSize.height);
		if( here.equals(start))
		{
			return true;
		}
		return false;
	}

	private boolean isAtCenter() {
		MazeCell here = mouse.getCurrentLocation();
		MazeCell goal1 = new MazeCell(mazeSize.width/2, mazeSize.height/2);
		MazeCell goal2 = goal1.plusX(1);
		MazeCell goal3 = goal1.plusY(1);
		MazeCell goal4 = goal3.plusX(1);
		if((here.equals(goal1)) || (here.equals(goal2)) 
				|| (here.equals(goal3)) || (here.equals(goal4)))
		{
			return true;
		}
		return false;
	}

	private boolean isATurn(RobotStep nextStep) {
		if( (nextStep == RobotStep.RotateLeft) || (nextStep == RobotStep.RotateRight))
		{
			return true;
		}
		return false;
	}

	private void setExplored() {
		MazeCell here = mouse.getCurrentLocation();
		explored[here.getX() - 1][here.getY() - 1] = true;
	}

	public int getTotalTraversed() {
		int unique = 0;
		for (int i = 0; i < mazeSize.width; i++)
		{
			for (int j = 0; j < mazeSize.height; j++)
			{
				if(explored[i][j] == true)
				{
					unique++;
				}
			}
		}
		return unique;
	}
	
	public int getFirstRunCells()
	{
		return firstRunSquaresTraversed;
	}
	
	public int getFirstRunTurns()
	{
		return firstRunTurnsTaken;
	}

	public int getBestRunCells()
	{
		return bestRunSquaresTraversed;
	}
	
	public int getBestRunTurns()
	{
		return bestRunTurnsTaken;
	}

	public int getThroughBestRunCells()
	{
		return bestRunTotalSquaresTraversed;
	}
	
	public int getThroughBestRunTurns()
	{
		return bestRunTotalTurnsTaken;
	}

	public static void main(String[] args) {
		Runnable runner = new Runnable() {
		      
			public void run() {
				Floodfill flood = new Floodfill();
		        RobotModelMaster master = new RobotModelMaster(new Maze(),new MazeCell(1,16), Direction.North);
		        RobotModel mouse = new RobotModel(master);
		        flood.setRobotLocation(mouse);
		        flood.initialize();
		        
		        StatTracker tracker = new StatTracker(flood, mouse);
		        
		        System.out.println("Total Squares Traversed = " + String.valueOf(tracker.getTotalTraversed()));
		        System.out.println("First Run Squares = " + String.valueOf(tracker.getFirstRunCells()));
		        System.out.println("First Run Turns = " + String.valueOf(tracker.getFirstRunTurns()));
		        System.out.println("Best Run Squares = " + String.valueOf(tracker.getBestRunCells()));
		        System.out.println("Best Run Turns = " + String.valueOf(tracker.getBestRunTurns()));
		        System.out.println("Total Run Squares = " + String.valueOf(tracker.getThroughBestRunCells()));
		        System.out.println("Total Run Turns = " + String.valueOf(tracker.getThroughBestRunTurns()));
		        
		        RightWallFollower righty = new RightWallFollower();
		        righty.setRobotLocation(mouse);
		        righty.initialize();
		        tracker.reload(righty, mouse);
		        
		        System.out.println("Total Squares Traversed = " + String.valueOf(tracker.getTotalTraversed()));
		        System.out.println("First Run Squares = " + String.valueOf(tracker.getFirstRunCells()));
		        System.out.println("First Run Turns = " + String.valueOf(tracker.getFirstRunTurns()));
		        System.out.println("Best Run Squares = " + String.valueOf(tracker.getBestRunCells()));
		        System.out.println("Best Run Turns = " + String.valueOf(tracker.getBestRunTurns()));
		        System.out.println("Total Run Squares = " + String.valueOf(tracker.getThroughBestRunCells()));
		        System.out.println("Total Run Turns = " + String.valueOf(tracker.getThroughBestRunTurns()));
		        
		        Tremaux frenchie = new Tremaux();
		        frenchie.setRobotLocation(mouse);
		        frenchie.initialize();
		        tracker.reload(frenchie, mouse);
		        
		        System.out.println("Total Squares Traversed = " + String.valueOf(tracker.getTotalTraversed()));
		        System.out.println("First Run Squares = " + String.valueOf(tracker.getFirstRunCells()));
		        System.out.println("First Run Turns = " + String.valueOf(tracker.getFirstRunTurns()));
		        System.out.println("Best Run Squares = " + String.valueOf(tracker.getBestRunCells()));
		        System.out.println("Best Run Turns = " + String.valueOf(tracker.getBestRunTurns()));
		        System.out.println("Total Run Squares = " + String.valueOf(tracker.getThroughBestRunCells()));
		        System.out.println("Total Run Turns = " + String.valueOf(tracker.getThroughBestRunTurns()));
		        
		        
			}
		};
		EventQueue.invokeLater(runner);
	}

}
