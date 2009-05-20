package maze.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.DefaultComboBoxModel;

/**
 * @author desolc
 */
public class MazeInfoModel
{
   private static final String[] NAMES = {"APEC2002.MAZ", "DEFAULT.MAZ",
                                          "HITEL01.MAZ", "HITEL02.MAZ",
                                          "LongPath.MAZ", "MINOS03Final.MAZ",
                                          "MM03FINS.MAZ", "SEOUL01.MAZ",
                                          "SEOUL02.MAZ"};
   private static final String[] NAME_EXT = {".mz2", ".maz"};
   final DefaultComboBoxModel mMazeInfoModel = new DefaultComboBoxModel();

   public MazeInfoModel()
   {
      fromJar();
      try
      {
         File currentDir = new File(".");
         scanDirectory(currentDir);
      }
      catch (Exception e)
      {
         System.out.println("Error scanning directory for maze files.");
         e.printStackTrace();
      }
   }

   public DefaultComboBoxModel getMazeInfoComboBoxModel()
   {
      return mMazeInfoModel;
   }

   public void fromJar()
   {
      for (String name : NAMES)
      {
         InputStream in;
         MazeInfo mi;
         
         in = MazeInfo.class.getResourceAsStream("mazeExamples/"+name);
         if (in != null && (mi = MazeInfo.load(in, name)) != null)
            mMazeInfoModel.addElement(mi);
      } // for (String name : names)

   }

   public void scanDirectory(File dir)
   {
      if (dir == null || !dir.isDirectory())
      {
         System.out.println("null directory");
         return;
      }

      for (File entry : dir.listFiles())
      {
         if (entry.isFile())
         {
            for (String ext : NAME_EXT)
            {
               if (entry.getName().toLowerCase().endsWith(ext))
               {
                  MazeInfo mi;
                  if ( (mi = MazeInfo.load(entry)) != null)
                     mMazeInfoModel.addElement(mi);
                  break;
               }
            }
         }
         //else if (entry.isDirectory())
         //   scanDirectory(entry);
      }
   }

   public MazeInfo createNew(String name, boolean extended)
   {
      MazeInfo mi = MazeInfo.createEmptyMaze(name, extended);
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
      if ( (mi = MazeInfo.load(file)) == null)
         return false;

      mMazeInfoModel.addElement(mi);
      return true;
   }
}
