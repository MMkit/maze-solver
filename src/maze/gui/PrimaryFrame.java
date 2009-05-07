package maze.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * This is the primary frame for the application.
 */
public final class PrimaryFrame extends JFrame
{
   /**
    * Constructor.
    */
   public PrimaryFrame()
   {
      // menu bar
      JMenuBar menuBar = new JMenuBar();

      //File item
      JMenu fileMenu = new JMenu( "File" );
      menuBar.add( fileMenu );

      // Save
      JMenuItem fileSave = new JMenuItem( "Save" );
      fileMenu.add( fileSave );

      // load
      JMenuItem fileLoad = new JMenuItem( "Load" );
      fileMenu.add( fileLoad );

      // exit
      JMenuItem fileExit = new JMenuItem( "Exit" );
      fileMenu.add( fileExit );
      fileExit.addActionListener( new ActionListener()
      {
         @Override
         public void actionPerformed( ActionEvent e )
         {
            System.exit( 0 );
         }
      } );

      //Maze Option item
      JMenu mazeMenu = new JMenu( "Maze Options" );
      menuBar.add( mazeMenu );

      // Load Maze
      JMenuItem mazeLoad = new JMenuItem( "Load Maze" );
      mazeMenu.add( mazeLoad );

      // Save Maze
      JMenuItem mazeSave = new JMenuItem( "Save Maze" );
      mazeMenu.add( mazeSave );

      // maze templetes
      JMenuItem mazeTemp = new JMenuItem( "Maze Templates" );
      mazeMenu.add( mazeTemp );

      // Mouse options
      JMenu mouseMenu = new JMenu( "Mouse Options" );
      menuBar.add( mouseMenu );

      // Load AI
      JMenuItem mouseLoad = new JMenuItem( "Load AI" );
      mouseMenu.add( mouseLoad );

      // Save AI
      JMenuItem mouseSave = new JMenuItem( "Save AI" );
      mouseMenu.add( mouseSave );

      // Algorithms
      JMenu mouseAlgorithms = new JMenu( "AI Algrithms" );
      mouseMenu.add( mouseAlgorithms );

      // Algorithm Radio buttons
      ButtonGroup algorithmGroup = new ButtonGroup();

      // Left-Wall Follower
      JRadioButtonMenuItem leftWallFollowAI = new JRadioButtonMenuItem( "Left-Wall Follower", true );
      mouseAlgorithms.add( leftWallFollowAI );
      algorithmGroup.add( leftWallFollowAI );

      // Right-Wall Follower
      JRadioButtonMenuItem rightWallFollowAI = new JRadioButtonMenuItem( "Right-Wall Follower" );
      mouseAlgorithms.add( rightWallFollowAI );
      algorithmGroup.add( rightWallFollowAI );

      // Tremaux's Method
      JRadioButtonMenuItem tremauxMethodAI = new JRadioButtonMenuItem( "Tremaux's Method" );
      mouseAlgorithms.add( tremauxMethodAI );
      algorithmGroup.add( tremauxMethodAI );

      // Floodfill
      JRadioButtonMenuItem floodFillAI = new JRadioButtonMenuItem( "Floodfill" );
      mouseAlgorithms.add( floodFillAI );
      algorithmGroup.add( floodFillAI );

      // Modified Floodfill
      JRadioButtonMenuItem mFloodFillAI = new JRadioButtonMenuItem( "Modified Floodfill" );
      mouseAlgorithms.add( mFloodFillAI );
      algorithmGroup.add( mFloodFillAI );

      // Telly
      JRadioButtonMenuItem tellyAI = new JRadioButtonMenuItem( "Telly" );
      mouseAlgorithms.add( tellyAI );
      algorithmGroup.add( tellyAI );

      // Custom?
      JRadioButtonMenuItem customAI = new JRadioButtonMenuItem( "Custom" );
      mouseAlgorithms.add( customAI );
      algorithmGroup.add( customAI );

      // Mouse Speed
      JMenuItem mouseSpeed = new JMenuItem( "Mouse Speed" );
      mouseMenu.add( mouseSpeed );

      // Display Settings
      JMenuItem mouseDisplay = new JMenuItem( "Display Settings" );
      mouseMenu.add( mouseDisplay );

      this.setJMenuBar( menuBar );

      this.setSize( 1000, 750 );

      this.add( new MazeView() );
   }
}
