package maze.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;

import maze.gui.mazeeditor.MazeEditor;
import maze.model.MazeInfo;
import maze.model.MazeInfoModel;

/**
 * This is the primary frame for the application.
 */
public final class PrimaryFrame extends JFrame implements WindowListener
{
   private final MazeInfoModel mMazeInfoModel = new MazeInfoModel();   
   private final MazeViewerPanel mazeViewer = new MazeViewerPanel();


   /**
    * Constructor.
    */
   public PrimaryFrame(){}


   /**
    * Initializes the contents of this frame.
    */
   public void init()
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
      fileExit.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            PrimaryFrame.this.dispose();
         }
      });

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
      JMenu mouseAlgorithms = new JMenu("AI Algrithms");
      mouseMenu.add(mouseAlgorithms);

      // Algorithm Radio buttons
      ButtonGroup algorithmGroup = new ButtonGroup();

      // Left-Wall Follower
      JRadioButtonMenuItem leftWallFollowAI = new JRadioButtonMenuItem("Left-Wall Follower", true);
      mouseAlgorithms.add(leftWallFollowAI);
      algorithmGroup.add(leftWallFollowAI);

      // Right-Wall Follower
      JRadioButtonMenuItem rightWallFollowAI = new JRadioButtonMenuItem("Right-Wall Follower");
      mouseAlgorithms.add(rightWallFollowAI);
      algorithmGroup.add(rightWallFollowAI);

      // Tremaux's Method
      JRadioButtonMenuItem tremauxMethodAI = new JRadioButtonMenuItem("Tremaux's Method");
      mouseAlgorithms.add(tremauxMethodAI);
      algorithmGroup.add(tremauxMethodAI);

      // Floodfill
      JRadioButtonMenuItem floodFillAI = new JRadioButtonMenuItem("Floodfill");
      mouseAlgorithms.add(floodFillAI);
      algorithmGroup.add(floodFillAI);

      // Modified Floodfill
      JRadioButtonMenuItem mFloodFillAI = new JRadioButtonMenuItem("Modified Floodfill");
      mouseAlgorithms.add(mFloodFillAI);
      algorithmGroup.add(mFloodFillAI);

      // Telly
      JRadioButtonMenuItem tellyAI = new JRadioButtonMenuItem("Telly");
      mouseAlgorithms.add(tellyAI);
      algorithmGroup.add(tellyAI);

      // Custom?
      JRadioButtonMenuItem customAI = new JRadioButtonMenuItem("Custom");
      mouseAlgorithms.add(customAI);
      algorithmGroup.add(customAI);

      // Mouse Speed
      JMenuItem mouseSpeed = new JMenuItem("Mouse Speed");
      mouseMenu.add(mouseSpeed);

      // Display Settings
      JMenuItem mouseDisplay = new JMenuItem("Display Settings");
      mouseMenu.add(mouseDisplay);

      //Add the start animation item.
      mouseMenu.add(this.mazeViewer.startAnimationAction);

      this.setJMenuBar(menuBar);

      this.setSize(1000, 750);

      JTabbedPane jtp = new JTabbedPane();
      this.add(jtp);

      jtp.add("Micro Mouse Simulator", this.mazeViewer);
      jtp.add("Maze Editor", new MazeEditor());
      jtp.add("AI Script Editor", new CodeEditorPanel());
   }

   /**
    * Returns a MazeInfoModel object containing all of the currently loaded
    * mazes.
    * @return said MazeInfoModel object.
    */
   public MazeInfoModel getMazeInfoModel()
   {
      return mMazeInfoModel;
   }

   public void saveMaze(MazeInfo mi)
   {
      DefaultComboBoxModel cbm = mMazeInfoModel.getMazeInfoComboBoxModel();
      TreeSet<String> paths = new TreeSet<String>();
      for (int i= 0; i < cbm.getSize(); i++)
      {
         String path = ((MazeInfo)cbm.getElementAt(i)).getPath();
         if (!path.equals(""))
            paths.add(path.toLowerCase());
      }
      if (mi.isDirty())
      {
         if (mi.getPath().equals(""))
         {
            JFileChooser chooser = new JFileChooser(".");
            while (true)
            {
               if (chooser.showSaveDialog(this) ==
                   JFileChooser.APPROVE_OPTION)
               {
                  File file = chooser.getSelectedFile();
                  try
                  {
                     if (paths.contains(file.getCanonicalPath().toLowerCase()))
                     {
                        JOptionPane.showMessageDialog(this,
                             "That file is being used by another maze",
                             "Error",
                             JOptionPane.ERROR_MESSAGE);
                     }
                     else
                     {
                        mi.setPath(file.getCanonicalPath());
                        break;
                     }
                  }
                  catch (IOException ex)
                  {
                     JOptionPane.showMessageDialog(this,
                             "Invalid Save File", "Error",
                             JOptionPane.ERROR_MESSAGE);
                  }
               } // if (chooser.showSaveDialog(this) ==
                 //  JFileChooser.APPROVE_OPTION)
               else
                  break;
            } // while (true)
         } // if (mi.getPath().equals(""))
         mi.store();
      } // if (mi.isDirty())
   }

   @Override
   public void windowOpened(WindowEvent e){}

   @Override
   public void windowClosing(WindowEvent e)
   {
      DefaultComboBoxModel cbm = mMazeInfoModel.getMazeInfoComboBoxModel();
      for (int i = 0; i < cbm.getSize(); i++)
      {
         MazeInfo mi = (MazeInfo)cbm.getElementAt(i);
         if (mi.isDirty())
         {
            int result = JOptionPane.showConfirmDialog(this,
                "Would you like to save \"" + mi.getName() + "\"", "Save Maze?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_NO_OPTION)
               saveMaze(mi);
         }
      } // for (int i = 0; i < cbm.getSize(); i++)
   }

   @Override
   public void windowClosed(WindowEvent e){}

   @Override
   public void windowIconified(WindowEvent e){}

   @Override
   public void windowDeiconified(WindowEvent e){}

   @Override
   public void windowActivated(WindowEvent e){}

   @Override
   public void windowDeactivated(WindowEvent e){}
}