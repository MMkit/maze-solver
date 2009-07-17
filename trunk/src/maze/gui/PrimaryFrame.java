package maze.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
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
import javax.swing.filechooser.FileFilter;

import maze.Main;
import maze.gui.mazeeditor.MazeEditorPage;
import maze.model.MazeInfoModel;

/**
 * This is the primary frame for the application.
 */
public final class PrimaryFrame extends JFrame
{
   private static final String PROJECT_HOMEPAGE = "http://code.google.com/p/maze-solver/";

   /**
    * An ActionListener that changes the swing look and feel.
    */
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

   private static final Dimension DEFAULT_SIZE = new Dimension(1024, 768);
   private ScriptEditorPage codeEditorPanel;
   private final JMenu fileMenu = new JMenu("File");
   private final JTabbedPane mainTabs = new JTabbedPane();
   private MazeEditorPage mazeEditor;
   private MazeViewerPage mazeViewer;
   private final MazeInfoModel mMazeInfoModel = new MazeInfoModel();

   /**
    * Called when the application is closed. Perform application exit
    * cleanup.This gives us a chance to cleanup and prompt to save things.
    * @return true if it is OK for the application to exit. false if the
    *         application should about the exit.
    */
   private boolean canExitingApplication()
   {
      return this.mazeEditor.canExit() && this.codeEditorPanel.canExit();
   }

   /**
    * Bring up the open file dialog and based on the file type selected hand it
    * off to the appropriate panel. The 2 groups of files are AI scripts and
    * maze files. AI scripts are python .py files and maze maps can be .maz or
    * .mz2.
    */
   private void doOpenFile()
   {
      final MenuControlled[] panels =
      {
         codeEditorPanel, mazeEditor
      };
      final JFileChooser fc = new JFileChooser();
      fc.setAcceptAllFileFilterUsed(false);

      // Create a file filter for all our file types.
      FileFilter allFiles = new FileFilter()
      {
         @Override
         public boolean accept(File f)
         {
            if (f.isDirectory())
               return true;
            for (MenuControlled mc : panels)
            {
               if (mc.isMyFileType(f))
                  return true;
            }
            return false;
         }

         @Override
         public String getDescription()
         {
            String msg = "";
            for (MenuControlled mc : panels)
            {
               msg += mc.getFileTypeDescription() + ", ";
            }
            return msg.substring(0, msg.length() - 2);
         }
      };
      fc.addChoosableFileFilter(allFiles);

      // Add filter for each type.
      for (final MenuControlled mc : panels)
      {
         fc.addChoosableFileFilter(new FileFilter()
         {
            @Override
            public boolean accept(File f)
            {
               if (f.isDirectory())
                  return true;
               else
                  return mc.isMyFileType(f);
            }

            @Override
            public String getDescription()
            {
               return mc.getFileTypeDescription();
            }
         });
      }

      fc.setFileFilter(allFiles); // Set initially selected.

      if (fc.showOpenDialog(PrimaryFrame.this) == JFileChooser.APPROVE_OPTION)
      {
         File file = fc.getSelectedFile();
         try
         {
            if (file.exists() && file.canRead())
            {
               // Try to open the file so it throws an exception if the file can not be read.
               FileInputStream fileStream = null;
               try
               {
                  fileStream = new FileInputStream(file);
               }
               finally
               {
                  if (fileStream != null)
                     fileStream.close();
               }
               for (final MenuControlled mc : panels)
               {
                  if (mc.isMyFileType(file))
                  {
                     mainTabs.setSelectedComponent((Component) mc);
                     mc.open(file);
                     return;
                  }
               }
               // If we make it this far then nobody recognized the file type.
               throw new RuntimeException("The type of the selected file was not recognized.");
            }
            else
            {
               throw new SecurityException("File may not exist.");
            }
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            String msg = "There was an error opening the file.\n" + ex.getLocalizedMessage();
            JOptionPane.showMessageDialog(PrimaryFrame.this,
                                          msg,
                                          "File Open Error",
                                          JOptionPane.ERROR_MESSAGE);
         }
      }
   } // End open file.

   /**
    * Exits the application.
    */
   public void exit()
   {
      if (this.canExitingApplication())
      {
         this.setVisible(false);
         System.exit(0);
      }
   }

   /**
    * Get the date that this build was created on.
    * @return Date string or <code>Unknown</code>.
    */
   private String getApplicationBuildDate()
   {
      final String unknown = "Unknown";
      try
      {
         Properties prop = new Properties();
         // This properties file is created by Ant during the build process.
         prop.load(Main.class.getResourceAsStream("build.properties"));
         return prop.getProperty("date", unknown);
      }
      catch (Exception e1)
      {
         return unknown;
      }
   }

   /**
    * Get the subversion revision this application was built from.
    * @return The Subversion revision number or 0 if unknown.
    */
   private int getApplicationRevision()
   {
      try
      {
         Properties prop = new Properties();
         // This properties file is created by Ant during the build process.
         prop.load(Main.class.getResourceAsStream("build.properties"));
         return Integer.parseInt(prop.getProperty("revision", "0"));
      }
      catch (Exception e1)
      {
         return 0;
      }
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

   /**
    * Initializes the contents of this frame.
    */
   public void init()
   {
      // Set up the application close procedure.
      this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      this.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            exit();
         }
      });

      this.setMinimumSize(new Dimension(750, 550)); // Set minimum to fit on an 800x600 screen.
      // If the screen is small then maximize the frame.
      DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
      if (dm.getWidth() <= DEFAULT_SIZE.width || dm.getHeight() <= DEFAULT_SIZE.height + 25)
      {
         this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      }
      else
      {
         this.setSize(DEFAULT_SIZE);
         // Center the frame on the screen.
         this.setLocation(dm.getWidth() / 2 - DEFAULT_SIZE.width / 2, dm.getHeight() /
                                                                      2 -
                                                                      DEFAULT_SIZE.height /
                                                                      2);
      }

      this.setTitle("Micro Mouse Maze Editor and Simulator");
      this.setIconImage(Main.getImageResource("gui/images/logo.png").getImage());

      // Main menu bar.
      final JMenuBar menuBar = new JMenuBar();
      this.setJMenuBar(menuBar);
      menuBar.add(this.fileMenu);

      //New file menu.
      JMenu newMenu = new JMenu("New");
      fileMenu.add(newMenu);

      //New maze.
      JMenuItem newMaze = new JMenuItem("Maze...");
      newMaze.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            mainTabs.setSelectedComponent(mazeEditor);
            mazeEditor.newMaze();
         }
      });
      newMenu.add(newMaze);

      JMenuItem newScript = new JMenuItem("AI Script...");
      newMenu.add(newScript);
      newScript.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            mainTabs.setSelectedComponent(codeEditorPanel);
            codeEditorPanel.createNewEditor();
         }
      });

      // Open file.
      JMenuItem fileLoad = new JMenuItem("Open...");
      fileLoad.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            doOpenFile();
         }
      });

      fileMenu.add(fileLoad);

      // Save
      JMenuItem fileSave = new JMenuItem("Save");
      fileSave.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Component c = mainTabs.getSelectedComponent();
            if (c instanceof MenuControlled)
            {
               MenuControlled mc = (MenuControlled) c;
               mc.saveCurrent();
            }
         }
      });
      fileMenu.add(fileSave);

      // Close file.
      JMenuItem fileClose = new JMenuItem("Close");
      fileClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Component c = mainTabs.getSelectedComponent();
            if (c instanceof MenuControlled)
            {
               MenuControlled mc = (MenuControlled) c;
               mc.close();
            }
         }
      });
      fileMenu.add(fileClose);

      // exit
      fileMenu.addSeparator();
      JMenuItem fileExit = new JMenuItem("Exit");
      fileMenu.add(fileExit);
      fileExit.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            exit();
         }
      });

      // Help menu.
      JMenu helpMenu = new JMenu("Help");
      menuBar.add(helpMenu);

      //Help
      JMenuItem helpItem = new JMenuItem("Help");
      helpMenu.add(helpItem);

      helpItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            HelpInfo.createAndShowGUI();
         }
      });

      // Look and feel menu.
      String defaultLAF = "";
      JMenu lookAndFeel = new JMenu("Look And Feel");
      helpMenu.add(lookAndFeel);
      ButtonGroup lafBG = new ButtonGroup();
      LookAndFeelListener lafal = new LookAndFeelListener();
      boolean nimbusFound = false;
      for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
      {
         JRadioButtonMenuItem jrbmi = new JRadioButtonMenuItem(laf.getName());
         lafBG.add(jrbmi);
         jrbmi.addActionListener(lafal);
         jrbmi.setActionCommand(laf.getClassName());
         lookAndFeel.add(jrbmi);
         if (laf.getName().equals("Nimbus"))
         {
            defaultLAF = laf.getClassName();
            lafBG.setSelected(jrbmi.getModel(), true);
            nimbusFound = true;
         }
      }
      if (!nimbusFound)
      {
         defaultLAF = UIManager.getSystemLookAndFeelClassName();
         Enumeration<AbstractButton> ab = lafBG.getElements();
         while (ab.hasMoreElements())
         {
            JRadioButtonMenuItem jrbmi = (JRadioButtonMenuItem) ab.nextElement();
            if (UIManager.getSystemLookAndFeelClassName().equals(jrbmi.getActionCommand()))
               lafBG.setSelected(jrbmi.getModel(), true);
         }
      }

      helpMenu.add(this.makeUpdateCheckMenuItem());
      helpMenu.add(this.makeWebsiteMenuItem());

      // Separate the about dialog.
      helpMenu.addSeparator();

      // Help->About menu item.
      helpMenu.add(this.makeAboutMenuItem());

      this.mazeViewer = new MazeViewerPage();
      this.codeEditorPanel = new ScriptEditorPage();
      this.mazeEditor = new MazeEditorPage();

      this.add(mainTabs);
      mainTabs.add("Micro Mouse Simulator", this.mazeViewer);
      mainTabs.add("Maze Editor", this.mazeEditor);
      mainTabs.add("AI Script Editor", this.codeEditorPanel);
      mainTabs.add("Statistics Display", new StatViewPage());

      try
      {
         UIManager.setLookAndFeel(defaultLAF);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      SwingUtilities.updateComponentTreeUI(this);
   } // End init.

   /**
    * Create the Help->About menu item.
    * @return Created item.
    */
   private JMenuItem makeAboutMenuItem()
   {
      final JMenuItem aboutMenuItem = new JMenuItem("About...");
      aboutMenuItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            String m = "<html><b>Build Date:</b> " +
                       getApplicationBuildDate() +
                       "<br /><b>Revision:</b> " +
                       getApplicationRevision() +
                       "<br />You can find more information about this application " +
                       "at the following project page.<br />" +
                       "<a href=\"" +
                       PROJECT_HOMEPAGE +
                       "\">" +
                       "http://code.google.com/p/maze-solver/</a><br />" +
                       "</html>";
            JOptionPane.showMessageDialog(PrimaryFrame.this,
                                          m,
                                          "About",
                                          JOptionPane.INFORMATION_MESSAGE);
         }
      });
      return aboutMenuItem;
   }

   /**
    * Creates a menu item that checks if you have the latest revision of the
    * application.
    * @return Menu item.
    */
   private JMenuItem makeUpdateCheckMenuItem()
   {
      final JMenuItem item = new JMenuItem("Check for Update...");
      item.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Thread t = new Thread()
            {
               @Override
               public void run()
               {
                  try
                  {
                     Reader r = new InputStreamReader(new java.net.URI("http://maze-solver.googlecode.com/svn/wiki/LatestRevision.wiki").toURL().openConnection().getInputStream(),
                                                      "ASCII");
                     CharBuffer cb = CharBuffer.allocate(1024);
                     r.read(cb);
                     cb.flip();
                     System.out.println(cb.toString());
                     final int revision = Integer.parseInt(cb.toString());
                     final int myRevision = getApplicationRevision();
                     if (myRevision == 0)
                     {
                        JOptionPane.showMessageDialog(PrimaryFrame.this,
                                                      "Unable to determine your current version.");
                     }
                     else if (revision == myRevision)
                     {
                        JOptionPane.showMessageDialog(PrimaryFrame.this,
                                                      "You have the latest version.");
                     }
                     else
                     {
                        JOptionPane.showMessageDialog(PrimaryFrame.this,
                                                      "There is a newer version available.\nYou have Revision: " +
                                                            myRevision +
                                                            "\nLatest Revision: " +
                                                            revision);
                     }
                  }
                  catch (Exception e)
                  {
                     e.printStackTrace();
                     JOptionPane.showMessageDialog(PrimaryFrame.this,
                                                   "An error occured while checking for the latest version.\n" +
                                                         e.toString());
                  }
               }
            };
            t.setDaemon(true);
            t.start();
         }
      });
      return item;
   }

   /**
    * Help->Project Web-site menu item launches the project web site in the
    * system browser.
    * @return Menu item.
    */
   private JMenuItem makeWebsiteMenuItem()
   {
      JMenuItem websiteMenuItem = new JMenuItem("Project Website...");
      websiteMenuItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Main.launchInBrowser(PROJECT_HOMEPAGE);
         }
      });
      return websiteMenuItem;
   }

   /**
    * Tells the primary frame if a simulation is currently running or not. We
    * can then disable GUI elements while animating.
    * @param simOn Set to true means the robot simulation is currently running.
    */
   public void setSimulation(boolean simOn)
   {
      this.mainTabs.setEnabled(!simOn);
      this.fileMenu.setEnabled(!simOn);
   }
}