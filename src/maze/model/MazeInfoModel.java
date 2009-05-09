/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.model;

import java.io.File;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author desolc
 */
public class MazeInfoModel
{
   private static final String NAME_EXT = ".maz";
   DefaultComboBoxModel mMazeInfoModel = new DefaultComboBoxModel();

   public MazeInfoModel()
   {
      File currentDir = new File(".");
      scanDirectory(currentDir);
   }

   public DefaultComboBoxModel getMazeInfoComboBoxModel()
   {
      return mMazeInfoModel;
   }

   public void scanDirectory(File dir)
   {
      if (dir == null || !dir.isDirectory())
         return;

      for (File entry : dir.listFiles())
      {
         if (entry.isFile())
         {
            
            if (entry.getName().toLowerCase().endsWith(NAME_EXT))
            {
               MazeInfo mi;
               if ((mi = MazeInfo.load(entry)) != null)
                  mMazeInfoModel.addElement(mi);
            }
         }
         else if (entry.isDirectory())
            scanDirectory(entry);
      }
   }

   public MazeInfo createNew(String name)
   {
      MazeInfo mi = MazeInfo.createEmptyMaze(name);
      mMazeInfoModel.addElement(mi);
      return mi;
   }

   public boolean addMaze(File file)
   {
      MazeInfo mi;
      for (int i = 0; i < mMazeInfoModel.getSize(); i++)
      {
         try
         {
            mi = (MazeInfo) mMazeInfoModel.getElementAt(i);
            if (mi.getPath().equalsIgnoreCase(file.getCanonicalPath()))
               return false;
         }
         catch (IOException ex)
         {
            return false;
         }
      }
      if ((mi = MazeInfo.load(file)) == null)
         return false;

      mMazeInfoModel.addElement(mi);
      return true;
   }
}
