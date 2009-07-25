package maze.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import maze.Main;
import maze.gui.PrimaryFrame;
import maze.util.Listener;

/**
 * Stores information about a Maze including its file name and model.
 * @author John Smith
 */
public class MazeInfo implements Listener<MazeCell>
{
   private MazeModel mModel;
   private String mPath;
   private String mName;
   private boolean isDirty = false;
   private boolean isMutable = true;
   private boolean isExtended = false;

   public static MazeInfo load(InputStream in, String name)
   {
      if (in == null)
         return null;

      MazeInfo returned = null;
      try
      {
         MazeInfo mi = new MazeInfo();
         MazeModel maze = new MazeModel();
         if (name != null && name.toLowerCase().endsWith(".mz2"))
            maze.loadMaze(in, true); // Is v2 maze.
         else
            maze.loadMaze(in, false);
         mi.mPath = name;
         mi.isMutable = false;
         mi.mName = name;
         mi.mModel = maze;
         mi.mModel.addDelayedListener(mi);
         returned = mi;
      }
      catch (Exception e)
      {}
      return returned;
   }

   public static MazeInfo load(File file)
   {
      if (file == null || !file.isFile())
         return null;
      //DataInputStream dis;
      MazeInfo returned = null;
      try
      {
         MazeInfo mi = new MazeInfo();
         MazeModel maze = new MazeModel();
         mi.mPath = file.getCanonicalPath();
         String name = maze.loadMaze(mi.mPath);
         if (name != null)
         {
            mi.mName = name;
            mi.isExtended = true;
         }
         else
            mi.mName = file.getName();
         mi.mModel = maze;

         mi.mModel.addDelayedListener(mi);
         returned = mi;
      }
      catch (IOException ex)
      {}

      return returned;
   }

   public static MazeInfo createEmptyMaze(String name, boolean extended)
   {
      MazeInfo mi = new MazeInfo();
      mi.mName = name;
      mi.mPath = "";
      mi.mModel = new MazeModel();
      mi.isDirty = true;
      mi.mModel.addDelayedListener(mi);
      mi.isExtended = extended;
      return mi;
   }

   public boolean store()
   {
      try
      {
         if (isExtended)
            mModel.saveMaze(mPath, mName);
         else
            mModel.saveMaze(mPath, null);
         isDirty = false;
      }
      catch (IOException e)
      {
         System.out.println("Saving Maze Error");
         return false;
      }

      return true;
   }

   public String getName()
   {
      return mName;
   }

   public String getPath()
   {
      return mPath;
   }

   public MazeModel getModel()
   {
      return mModel;
   }

   public boolean isDirty()
   {
      return isDirty;
   }

   public void clearDirty()
   {
      if (!isMutable)
         isDirty = false;
   }

   public boolean isMutable()
   {
      return isMutable;
   }

   public boolean isExtended()
   {
      return isExtended;
   }

   public void setName(String name)
   {
      mName = name;
      isExtended = true;
   }

   public void setPath(String path)
   {
      if (!mPath.equalsIgnoreCase(path))
      {
         mPath = path;
         isDirty = true;
      }
   }

   /**
    * Private constructor. Instances must be retrieved from factory methods.
    */
   private MazeInfo()
   {}

   @Override
   public String toString()
   {
      return this.mName;
   }

   public MazeInfo getMutableClone()
   {
      MazeInfo theClone = new MazeInfo();
      theClone.isDirty = isDirty;
      theClone.isExtended = isExtended;
      theClone.isMutable = true;
      theClone.mModel = (MazeModel) mModel.clone();
      theClone.mModel.addListener(theClone);
      theClone.mName = mName;
      theClone.mPath = mPath;
      return theClone;
   }

   @Override
   public void eventFired(MazeCell event)
   {
      this.isDirty = true;
   }

   /**
    * Save a maze.
    * @return The saved maze or null. Can return itself or a new instance.
    */
   public MazeInfo saveMaze()
   {
      final PrimaryFrame frame = Main.getPrimaryFrameInstance();
      DefaultComboBoxModel cbm = frame.getMazeInfoModel().getMazeInfoComboBoxModel();
      TreeSet<String> paths = new TreeSet<String>();
      for (int i = 0; i < cbm.getSize(); i++)
      {
         String path = ((MazeInfo) cbm.getElementAt(i)).getPath();
         if (path.isEmpty() == false)
            paths.add(path.toLowerCase());
      }
      if (this.isDirty())
      {
         MazeInfo toSave = this;
         if (!this.isMutable())
         {
            toSave = this.getMutableClone();
            Box box = new Box(BoxLayout.Y_AXIS);
            JLabel label = new JLabel("What would you like to call the" + " maze?");
            JTextField field = new JTextField();
            box.add(label);
            box.add(field);
            String newName = null;
            while (true)
            {
               int result = JOptionPane.showConfirmDialog(frame,
                                                          box,
                                                          "Maze Name",
                                                          JOptionPane.OK_CANCEL_OPTION,
                                                          JOptionPane.QUESTION_MESSAGE);
               if (result == JOptionPane.OK_OPTION)
               {
                  if (field.getText().length() > 0)
                  {
                     newName = field.getText();
                     break;
                  }
               }
               else
                  break;
            }
            if (newName == null)
               return null;
            this.clearDirty();
            toSave.setName(newName);
            toSave.setPath("");
            frame.getMazeInfoModel().addMaze(toSave);
            toSave.saveMaze();
            return toSave;
         }
         else if (this.getPath() == null || this.getPath().isEmpty())
         {
            JFileChooser chooser = new JFileChooser();
            while (true)
            {
               if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
               {
                  File file = chooser.getSelectedFile();
                  try
                  {
                     if (paths.contains(file.getCanonicalPath().toLowerCase()))
                     {
                        JOptionPane.showMessageDialog(frame,
                                                      "That file is being used by another maze",
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);
                     }
                     else
                     {
                        this.setPath(file.getCanonicalPath());
                        break;
                     }
                  }
                  catch (IOException ex)
                  {
                     JOptionPane.showMessageDialog(frame,
                                                   "Invalid Save File",
                                                   "Error",
                                                   JOptionPane.ERROR_MESSAGE);
                  }
               }
               else
               { // Save dialog not approved.
                  break;
               }
            } // End infinite loop.
         }
         this.store();
      } // if (mi.isDirty())
      return this;
   }
}
