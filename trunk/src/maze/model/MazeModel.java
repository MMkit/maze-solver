package maze.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Observable;
import java.util.TreeSet;

/**
* This is a potential new design for the model API.
* @author Luke Last
*/
public class MazeModel extends Observable
{
   private int width;
   private int height;

   private BitSet cwalls;
   private BitSet rwalls;
   private static final int DEFAULT_SIZE = 16; //A maze is generally 16 x 16
   public static final int NORTH = 0;
   public static final int EAST = 1;
   public static final int SOUTH = 2;
   public static final int WEST = 3;

   //Another constructor should be made to pass in a predetermined maze
   public MazeModel()
   {
      this(DEFAULT_SIZE, DEFAULT_SIZE);
   }

   public MazeModel(MazeModel model)
   {
      width = model.width;
      height = model.height;
      cwalls = (BitSet)model.cwalls.clone();
      rwalls = (BitSet)model.rwalls.clone();
   }

   public MazeModel(int width, int height)
   {
      this.width = width + width%2;
      this.height = height + height%2;
      rwalls = new BitSet(width*(height-1));
      cwalls = new BitSet((width-1)*height);
      setWall(1, height, EAST);
   }

   public void setWall( int x, int y, int dir )
   {
      //Check for the boundary conditions
      //First make sure that we're in the maze
      if( (x<1) || (y<1) || (x>width) || (y>height) )
         return;
      //Second make sure that the wall isn't implied
      if( ((dir == NORTH) && (y == 1) )
            || ( (dir == EAST) && (x == width) )
            || ( (dir == SOUTH) && (y == height) )
            || ( (dir == WEST) && (x == 1) ) )
         return;

      x--;
      y--;

      if (dir == SOUTH)
         y++;
      if (dir == EAST)
         x++;

      if( dir == NORTH || dir == SOUTH )
         rwalls.set(y*height+x);
      else
         cwalls.set(x*width+y);
   }

   public boolean getWall( int x, int y, int dir )
   {
      //Check for the boundary conditions
      if( ( (dir == NORTH) && (y == 1) )
            || ( (dir == EAST) && (x == width) )
            || ( (dir == SOUTH) && (y == height) )
            || ( (dir == WEST) && (x == 1) ) )
         return true;

      if( (x > width) || ( y> height ) || (x<1) || (y<1) )
         return true;
      x--;
      y--;

      if (dir == SOUTH)
         y++;
      if (dir == EAST)
         x++;

      if( dir == NORTH || dir == SOUTH )
         return rwalls.get(y*height+x);
      else
         return cwalls.get(x*width+y);
   }

   public void clearWall( int x, int y, int dir )
   {
      //Check for the boundary conditions
      if( ( (dir == NORTH) && (y == 1) )
            || ( (dir == EAST) && (x == width) )
            || ( (dir == SOUTH) && (y == height) )
            || ( (dir == WEST) && (x == 1) ) )
         return;
      
      if( x > width || y> height || x<1 || y<1)
         return;
      x--;
      y--;

      if (dir == SOUTH)
         y++;
      if (dir == EAST)
         x++;

      if( dir == NORTH || dir == SOUTH )
         rwalls.clear(y*height+x);
      else
         cwalls.clear(x*width+y);
   }

   public boolean isLegal()
   {
      //There are three rules that must be upheld to be a valid maze
      //Rule 1: There is a wall next to the starting square
      if(!getWall(1,height,EAST) || getWall(1,height,NORTH))
         return false;
      //Rule 2: There is one and only one way into the center of the maze
      //Even though I've coded this to accept a variable size in the future
      //I shall still assume here that the total size will always be even      
      int row = height/2, column = width/2;
      if(!isCenterLegal())
         return false;

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if (!isCenterOpen())
         return false;

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for(int i = 1; i <= width; i++)
      {
         for(int j = 1; j <= height; j++)
         {
            if((i==row && j==column) || isPegLegal(i,j))
               continue;
            return false;
         }
      }
      return true;
   }

   public void clearMaze()
   {
      rwalls.clear();
      cwalls.clear();
      setWall(1,height,EAST);
   }

   public boolean isPegLegal( int x, int y )
   {
      //This function is designed to give a lower-level view to the isLegal
      //method by telling about a specific peg

      //The peg location is based off of the cell that it is in the NORTHWEST
      //corner of.  Peg (1,1) is the only peg touching cell (1,1)
      if(x < 1 || y < 1 || x > width || y > height)
         return false;
      //Every peg should have at least one wall attached to it
      if(getWall(x,y,SOUTH) || getWall(x,y,EAST) ||
         getWall(x+1,y+1,NORTH) || getWall(x+1,y+1,WEST))
      {
         //There are only three cases with walls that must be checked
         //First is the starting square
         if( (x == 1) && (y == height) )
         {
            if(getWall(1,height,EAST) && !getWall(1,height,NORTH))
               return true;
            else
               return false;
         }
         
         //Second is that it is not the center peg
         else if(x == (width/2) && y == (height/2))
            return isCenterLegal();
         
         //Third is the pegs surrounding the center square
         else if((x <= width/2 && y <= height/2) &&
                 (x >= width/2 - 1 && y >= height/2 - 1))
            return isCenterOpen();
         //There are no more reasons to check the walls
         else
            return true;
      }
      else
      {
         //The only exception to no wall is the center peg
         if(x == width/2 && y == height/2)
            return true;
         else
            return false;
      }
   }

   public boolean isCenterLegal()
   {
      int row = height/2;
      int column = width/2;
      if (getWall(column,row,SOUTH) || getWall(column,row,EAST) ||
          getWall(column+1,row,SOUTH) || getWall(column,row+1,EAST))
         return false;
      return true;
   }

   public boolean isCenterOpen()
   {
      int walls = 0;
      int row = height/2;
      int column = width/2;
      if(getWall(column,row,NORTH))
         walls++;
      if(getWall(column,row,WEST))
         walls++;
      if(getWall(column+1,row,NORTH))
         walls++;
      if(getWall(column+1,row,EAST))
         walls++;
      if(getWall(column,row+1,WEST))
         walls++;
      if(getWall(column,row+1,SOUTH))
         walls++;
      if(getWall(column+1,row+1,EAST))
         walls++;
      if(getWall(column+1,row+1,SOUTH))
         walls++;

      if(walls != 7)
         return false;
      return true;
   }

   public TreeSet<Point> illegalPegs()
   {
      //This function is basically isLegal with the added capability of
      //returning the peg locations that are bad

      //Can return multiple instances of a Point if that Point is bad for
      //More than one reason.
      //Example: if somehow there were no walls off of peg(0,0) then the
      //Point would be included since

      TreeSet<Point> badPoints = new TreeSet<Point>(new PointCompare());
      //There are rules that must be upheld to be a valid maze
      //Rule 1: There is a wall next to the starting square
      if(getWall(1,width,EAST) == false || getWall(1,height,NORTH))
         badPoints.add(new Point(1,height-1));
      //Rule 2: There is one and only one way into the center of the maze
      //Even though I've coded this to accept a variable size in the future
      //I shall still assume here that the total size will always be even

      int column = width/2;
      int row = height/2;

      if(!isCenterOpen()) {
         badPoints.add(new Point(column-1, row-1));
         badPoints.add(new Point(column-1, row));
         badPoints.add(new Point(column-1, row+1));
         badPoints.add(new Point(column, row-1));
         //badPoints.add(new Point(column, row));
         badPoints.add(new Point(column, row+1));
         badPoints.add(new Point(column+1, row-1));
         badPoints.add(new Point(column+1, row));
         badPoints.add(new Point(column+1, row+1));
      }	//All of the center pegs are connected

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if (!isCenterOpen())
         badPoints.add(new Point(column, row));

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for(int i = 1; i <= width; i++)
      {
         for(int j = 1; j <= height; j++)
         {
            if (!isPegLegal(i,j))
               badPoints.add( new Point(i,j) );
         }
      }

      return badPoints;
   }

   public void setSize( Dimension size )
   {
      width = size.width + size.width%2;
      height = size.height + size.height%2;
      rwalls = new BitSet(width*(height-1));
      cwalls = new BitSet((width-1)*height);
   }

   public Dimension getSize()
   {
      return new Dimension(width,height);
   }

   public MazeWall getWall( final MazeCell cell, final Direction direction )
   {
      return new MazeWall()
      {
         @Override
         public boolean isSet()
         {
            return getWall( cell.getX(), cell.getY(), direction.getIndex() );
         }

         @Override
         public void set( boolean value )
         {
            if ( value )
               setWall( cell.getX(), cell.getY(), direction.getIndex() );
            else
               clearWall( cell.getX(), cell.getY(), direction.getIndex() );
         }
      };
   }

   public void saveMaze(String filename) throws IOException
   {
      //This is the formatting of the .MAZ files found online
      //This format is 4 times more inefficient than needs be
      //but it is a format that is already present
      byte[] fileContents = new byte[256];
      for(int i=1; i<=16; i++){
         for(int j=16; j>0; j--){
            MazeCell cell = new MazeCell(i,j);
            byte cellWalls = 0;
            if(this.getWall(cell, Direction.North).isSet()){
               cellWalls =1;
            }
            if(this.getWall(cell, Direction.East).isSet()){
               cellWalls +=2;
            }
            if(this.getWall(cell, Direction.South).isSet()){
               cellWalls +=4;
            }
            if(this.getWall(cell, Direction.West).isSet()){
               cellWalls +=8;
            }

            fileContents[16*(i-1) + (16-j)] = cellWalls;
         }
      }

      //Now for the actual file i/o
      filename.toLowerCase();
      if(filename.endsWith(".maz") == false)
      {
         filename = filename + ".maz";
      }

      File file = new File(filename);
      if(file.exists() == false){
         file.createNewFile();
      }

      FileOutputStream out;
      out = new FileOutputStream(file);
      out.write(fileContents);
      out.close();

   }

   public void loadMaze(String filename) throws FileNotFoundException
   {
      //This is the formatting of the .MAZ files found online
      //This format is 4 times more inefficient than needs be
      //but it is a format that is already present
      byte[] fileContents = new byte[256];

      //Now for the actual file i/o
      filename.toLowerCase();
      //	   if(filename.endsWith(".maz") == false)
      //	   {
      //		   filename = filename + ".maz";
      //	   }

      File file = new File(filename);

      FileInputStream in = null;
      try {
         in = new FileInputStream(file);
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         System.out.println("first place");
         System.out.println(filename);
      }
      try {
         in.read(fileContents);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("2nd place");
      }
      try {
         in.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("3rd place");
      }

      setSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));

      for(int i=1; i<=16; i++){
         for(int j=16; j>0; j--){
            byte cellWalls = fileContents[16*(i-1) + (16-j)];
            if((cellWalls & 1) == 1){
               this.setWall(i,j, NORTH);
            }
            else{
               this.clearWall(i,j,NORTH);
            }
            if((cellWalls & 2) == 2){
               this.setWall(i,j, EAST);
            }
            else{
               this.clearWall(i,j,EAST);
            }
            if((cellWalls & 4) == 4){
               this.setWall(i,j, SOUTH);
            }
            else{
               this.clearWall(i,j,SOUTH);
            }
            if((cellWalls & 8) == 8){
               this.setWall(i,j, WEST);
            }
            else{
               this.clearWall(i,j,WEST);
            }
         }
      }
   }

   public MazeWall getWall( int x, int y, Direction direction )
   {
      return this.getWall( new MazeCell( x, y ), direction );
   }

   public static interface MazeWall
   {
      public boolean isSet();

      public void set( boolean value );
   }

   private static class PointCompare implements Comparator<Point>
   {
      @Override
      public int compare(Point o1, Point o2)
      {
         if (o1.x == o2.x)
            if (o1.y == o2.y)
               return 0;
            else
               return o1.y-o2.y;
         else
            return o1.x-o2.x;
      }
   }
}
