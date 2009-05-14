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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import maze.gui.mazeeditor.MazeEditor;
import maze.model.MazeInfo;
import maze.model.MazeInfoModel;

/**
 * This is the primary frame for the application.
 */
public final class PrimaryFrame extends JFrame implements WindowListener
{
   private final MazeInfoModel mMazeInfoModel = new MazeInfoModel();
   private MazeViewerPanel mazeViewer;
   private CodeEditingPanel codeEditorPanel;
   private final JTabbedPane mainTabs = new JTabbedPane();
   private final JMenu mazeMenu = new JMenu("Maze Options");
   private final JMenu mouseMenu = new JMenu("Mouse Options");

   /**
    * Constructor.
    */
   public PrimaryFrame()
   {}

   /**
    * Initializes the contents of this frame.
    */
   public void init()
   {
      this.mazeViewer = new MazeViewerPanel();
      this.codeEditorPanel = new CodeEditingPanel();

      // menu bar
      JMenuBar menuBar = new JMenuBar();

      //File item
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);

      //New file menu.
      JMenu newMenu = new JMenu("New");
      fileMenu.add(newMenu);

      //New maze.
      JMenuItem newMaze = new JMenuItem("Maze");
      newMenu.add(newMaze);

      JMenuItem newScript = new JMenuItem("AI Script");
      newMenu.add(newScript);
      newScript.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            codeEditorPanel.createNewEditor();
            mainTabs.setSelectedComponent(codeEditorPanel);
         }
      });

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

      menuBar.add(mazeMenu);

      ActionListener mazeActionListener = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String command = e.getActionCommand();
            System.out.println(command);
            //   		  if(command.equals("Save Maze")) {
            //  			  saveMaze();
            //   		  }
         }
      };

      // Load Maze
      JMenuItem mazeLoad = new JMenuItem("Load Maze");
      mazeLoad.addActionListener(mazeActionListener);
      mazeMenu.add(mazeLoad);

      // Save Maze
      JMenuItem mazeSave = new JMenuItem("Save Maze");
      mazeSave.addActionListener(mazeActionListener);
      mazeMenu.add(mazeSave);

      // maze templetes
      JMenuItem mazeTemp = new JMenuItem("Maze Templates");
      mazeTemp.addActionListener(mazeActionListener);
      mazeMenu.add(mazeTemp);

      // Mouse options

      menuBar.add(mouseMenu);

      // Load AI
      JMenuItem mouseLoad = new JMenuItem("Load AI Script");
      mouseMenu.add(mouseLoad);
      mouseLoad.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            codeEditorPanel.openScript();
            mainTabs.setSelectedComponent(codeEditorPanel);
         }
      });

      // Save Python AI script.
      JMenuItem mouseSave = new JMenuItem("Save AI Script");
      mouseMenu.add(mouseSave);
      mouseSave.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            codeEditorPanel.saveScript();
         }
      });

      // Close AI script.
      this.mouseMenu.add(new JMenuItem(this.codeEditorPanel.closeScriptAction));

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

      this.setJMenuBar(menuBar);

      this.setSize(1000, 750);

      this.add(mainTabs);

      mainTabs.add("Micro Mouse Simulator", this.mazeViewer);
      mainTabs.add("Maze Editor", new MazeEditor());
      mainTabs.add("AI Script Editor", this.codeEditorPanel);
      mainTabs.add("Statistics Display", new StatViewPanel());

      JMenu lookAndFeel = new JMenu("Look And Feel");
      ButtonGroup lafBG = new ButtonGroup();
      LookAndFeelListener lafal = new LookAndFeelListener();

      for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
      {
         JRadioButtonMenuItem jrbmi = new JRadioButtonMenuItem(laf.getName());
         lafBG.add(jrbmi);
         jrbmi.addActionListener(lafal);
         jrbmi.setActionCommand(laf.getClassName());
         lookAndFeel.add(jrbmi);
         if (laf.getName().equals("Nimbus"))
            lafBG.setSelected(jrbmi.getModel(), true);
      }
      menuBar.add(lookAndFeel);
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
      for (int i = 0; i < cbm.getSize(); i++)
      {
         String path = ((MazeInfo) cbm.getElementAt(i)).getPath();
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
               if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
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
                                                   "Invalid Save File",
                                                   "Error",
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
   public void windowOpened(WindowEvent e)
   {}

   @Override
   public void windowClosing(WindowEvent e)
   {
      DefaultComboBoxModel cbm = mMazeInfoModel.getMazeInfoComboBoxModel();
      for (int i = 0; i < cbm.getSize(); i++)
      {
         MazeInfo mi = (MazeInfo) cbm.getElementAt(i);
         if (mi.isDirty())
         {
            int result = JOptionPane.showConfirmDialog(this,
                                                       "Would you like to save \"" + mi.getName() + "\"",
                                                       "Save Maze?",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_NO_OPTION)
               saveMaze(mi);
         }
      } // for (int i = 0; i < cbm.getSize(); i++)
   }

   @Override
   public void windowClosed(WindowEvent e)
   {}

   @Override
   public void windowIconified(WindowEvent e)
   {}

   @Override
   public void windowDeiconified(WindowEvent e)
   {}

   @Override
   public void windowActivated(WindowEvent e)
   {}

   @Override
   public void windowDeactivated(WindowEvent e)
   {}

   public CodeEditingPanel getCodeEditorPanel()
   {
      return this.codeEditorPanel;
   }

   /**
    * Tells the primary frame if a simulation is currently running or not. We
    * can then disable GUI elements while animating.
    * @param simOn Set to true means the robot simulation is currently running.
    */
   public void setSimulation(boolean simOn)
   {
      this.mainTabs.setEnabled(!simOn);
      this.mazeMenu.setEnabled(!simOn);
      this.mouseMenu.setEnabled(!simOn);
   }

   private class LookAndFeelListener implements ActionListener
   {
      @Override
      public void actionPerformed(ActionEvent e)
      {
         try
         {
            UIManager.setLookAndFeel(e.getActionCommand());
            SwingUtilities.updateComponentTreeUI(PrimaryFrame.this);
         }
         catch (Exception ex)
         {}
      }
   }
}