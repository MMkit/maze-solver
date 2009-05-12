package maze.model;

//import java.awt.Dimension;
//import java.io.DataInputStream;
import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Stores information about a Maze including its file name and model.
 * @author desolc
 */
public class MazeInfo implements Observer
{
   private MazeModel mModel;
   private String mPath;
   private String mName;
   private boolean isDirty = false;

   public static MazeInfo load(File file)
   {
      if (file == null || !file.isFile())
         return null;
      //DataInputStream dis;
      MazeInfo returned = null;
      try
      {
         MazeInfo mi = new MazeInfo();
         
         //dis = new DataInputStream(new FileInputStream(file));
         //mi.mName = dis.readUTF();
         /*Dimension d = new Dimension(dis.readInt(), dis.readInt());
         int[] walls = new int[d.height + d.width];
         for (int i = 0; i < walls.length; i++)
            walls[i] = (dis.available() >= Integer.SIZE / 8) ? dis.readInt() : 0;
         */
         Maze maze = new Maze();
         mi.mPath = file.getCanonicalPath();
         maze.loadMaze(mi.mPath);
         mi.mModel = new Maze(maze.getMaze());
         mi.mName = file.getName();
         mi.mModel.addObserver(mi);
         returned = mi;
         //dis.close();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }

      return returned;
   }

   public static MazeInfo createEmptyMaze(String name)
   {
      MazeInfo mi = new MazeInfo();
      mi.mName = name;
      mi.mPath = "";
      mi.mModel = new MazeModelStub();
      mi.isDirty = true;
      return mi;
   }

   public boolean store()
   {
	   Maze maze = new Maze(mModel);
	   try {
		maze.saveMaze(mPath);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("Saving Maze Error");
	}
      
	   return true;
      /*boolean success = false;
      DataOutputStream dos;
      try
      {
         dos = new DataOutputStream(new FileOutputStream(mPath));
         dos.writeUTF(mName);
         Dimension d = mModel.getSize();
         dos.writeInt(d.width);
         dos.writeInt(d.height);
         int[] maze = mModel.getMaze();
         for (int i : maze)
            dos.writeInt(i);
         dos.close();
         
         success = true;
      }
      catch(Exception ex){}

      return success;*/
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

   public void setPath(String path)
   {
      if (mPath.equalsIgnoreCase(path))
         return;
      else
      {
         mPath = path;
         isDirty = true;
      }
   }

   /**
    * Private constructor. Instances must be gotten from factory methods.
    */
   private MazeInfo()
   {
   }

   @Override
   public void update(Observable o, Object arg)
   {
      isDirty = true;
   }
   
   @Override
   public String toString()
   {
      return this.mName;
   }
}
