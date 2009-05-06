package maze.gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);
      
      // Save
      JMenuItem fileSave = new JMenuItem("Save");
      fileMenu.add(fileSave);

      // load
      JMenuItem fileLoad = new JMenuItem("Load");
      fileMenu.add(fileLoad);
      
      // exit
      JMenuItem fileExit = new JMenuItem("Exit");
      fileMenu.add(fileExit);
      
      //Maze Option item
      JMenu mazeMenu = new JMenu("Maze Options");
      menuBar.add(mazeMenu);

      // Load Maze
      JMenuItem mazeLoad = new JMenuItem("Load Maze");
      mazeMenu.add(mazeLoad);

      // Save Maze
      JMenuItem mazeSave = new JMenuItem("Save Maze");
      mazeMenu.add(mazeSave);
      
      // maze templetes
      JMenuItem mazeTemp = new JMenuItem("Maze Templates");
      mazeMenu.add(mazeTemp);
       
      // Mouse options
      JMenu mouseMenu = new JMenu("Mouse Options");
      menuBar.add(mouseMenu);
      
      // Load AI
      JMenuItem mouseLoad = new JMenuItem("Load AI");
      mouseMenu.add(mouseLoad);
      
      // Save AI
      JMenuItem mouseSave = new JMenuItem("Save AI");
      mouseMenu.add(mouseSave);
      
      // Algorithms
      JMenuItem mouseAlgorithms = new JMenuItem("AI Algrithms");
      mouseMenu.add(mouseAlgorithms);
      
      // Mouse Speed
      JMenuItem mouseSpeed = new JMenuItem("Mouse Speed");
      mouseMenu.add(mouseSpeed);
      
      // Display Settings
      JMenuItem mouseDisplay = new JMenuItem("Display Settings");
      mouseMenu.add(mouseDisplay);
      
      this.setJMenuBar(menuBar);
       
      this.setSize( 1000, 750 );

      this.add( new MazeView() );
   }
}
