package maze.model;

import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import maze.util.ListenerSubject;

/**
 * Represents all the walls of a This class extends
 * {@link maze.util.ListenerSubject} which allows it to notify listeners when a
 * wall changes and provide the {@link maze.model.MazeCell} that contains the
 * changed wall.
 */
public final class MazeModel extends ListenerSubject<MazeCell> implements Cloneable, Serializable
{
   /**
    * A <code>MazeWall</code> object represents the model for a single wall in a
    * maze. It has a method to check whether the wall is set/enabled or not. And
    * to change its status.
    */
   public static interface MazeWall
   {
      public boolean isSet();

      public void set(boolean value);
   }

   /**
    * A maze is generally 16 x 16
    */
   private static final int DEFAULT_SIZE = 16;

   public static final int EAST = 1;
   public static final int NORTH = 0;
   public static final int SOUTH = 2;
   public static final int WEST = 3;
   /**
    * Column walls.
    */
   private BitSet cwalls;
   /**
    * Number of rows or height of this maze.
    */
   private int height;
   /**
    * Row walls.
    */
   private BitSet rwalls;
   /**
    * Number of columns or width of this maze.
    */
   private int width;

   /**
    * Create a maze with the default size.
    */
   public MazeModel()
   {
      this(DEFAULT_SIZE, DEFAULT_SIZE);
   }

   /**
    * Construct a new maze model with the given dimensions.
    * @param width The width or number of columns of the new maze.
    * @param height The height or number of rows of the new maze.
    */
   public MazeModel(int width, int height)
   {
      this.width = width + width % 2;
      this.height = height + height % 2;
      rwalls = new BitSet(width * (height - 1));
      cwalls = new BitSet( (width - 1) * height);
      setWall(1, height, EAST);
   }

   /**
    * Clears the 4 walls around the center peg as is required by the rules for a
    * legal maze. After calling this a call to {@link #isCenterLegal()} should
    * return true.
    */
   public void clearCenterWalls()
   {
      final int centerY = height / 2;
      final int centerX = width / 2;
      clearWall(centerX, centerY, SOUTH);
      clearWall(centerX, centerY, EAST);
      clearWall(centerX + 1, centerY, SOUTH);
      clearWall(centerX, centerY + 1, EAST);
   }

   /**
    * Clear all the walls of the maze and set the default corner wall.
    */
   public void clearMaze()
   {
      rwalls.clear();
      cwalls.clear();
      setWall(1, height, EAST);
   }

   /**
    * Clear/disable a wall segment.
    * @param x
    * @param y
    * @param dir
    */
   public void clearWall(int x, int y, int dir)
   {
      //Check for the boundary conditions
      if ( ( (dir == NORTH) && (y == 1)) ||
          ( (dir == EAST) && (x == width)) ||
          ( (dir == SOUTH) && (y == height)) ||
          ( (dir == WEST) && (x == 1)))
         return;

      if (x > width || y > height || x < 1 || y < 1)
         return;
      x--;
      y--;

      if (dir == NORTH)
         y--;
      if (dir == WEST)
         x--;

      if (dir == NORTH || dir == SOUTH)
      {
         if (rwalls.get(y * width + x))
         {
            rwalls.clear(y * width + x);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }
      else
      {
         if (cwalls.get(x * height + y))
         {
            cwalls.clear(x * height + y);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }
   }

   /**
    * Make a deep copy of this maze model.
    */
   @Override
   public MazeModel clone()
   {
      try
      {
         MazeModel mm = (MazeModel) super.clone();
         mm.rwalls = (BitSet) rwalls.clone();
         mm.cwalls = (BitSet) cwalls.clone();
         return mm;
      }
      catch (CloneNotSupportedException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Randomizes all the walls of this maze.
    */
   public void generateRandomMaze()
   {
      this.setAllWalls();

      final Random rand = new Random();
      for (int x = 1; x <= this.getSize().width; x++)
      {
         for (int y = 1; y <= this.getSize().height; y++)
         {
            final MazeCell cell = new MazeCell(x, y);
            this.getWall(cell, Direction.East).set(rand.nextBoolean());
            this.getWall(cell, Direction.South).set(rand.nextBoolean());
         }
      }
      setWall(1, height, EAST); // Set mandatory wall.
      clearWall(1, height, NORTH); // Must be clear.
      clearWall(1, height - 1, NORTH); // Clear just to give it a better chance.
      clearWall(1, height - 2, NORTH); // Clear just to give it a better chance.
      clearWall(1, height - 1, EAST); // Clear just to give it a better chance.
      clearWall(2, height - 1, EAST); // Clear just to give it a better chance.

      this.clearCenterWalls();
      // Open one wall into the center.
      List<MazeWall> winningBorder = this.getWinningBorderWalls();
      for (MazeWall wall : winningBorder)
         wall.set(true);
      winningBorder.get(rand.nextInt(winningBorder.size())).set(false);

   }

   /**
    * Get the size or dimension of this maze in columns by rows.
    * @return The current size of this maze model.
    */
   public Dimension getSize()
   {
      return new Dimension(width, height);
   }

   /**
    * Get the location of where the robot starts in the maze. Using this instead
    * of the hard coded location allows it to be changed in the future.
    * @return Starting cell location.
    */
   public MazeCell getStartingCell()
   {
      return new MazeCell(1, this.height);
   }

   /**
    * Is the wall set/enabled.
    * @param x
    * @param y
    * @param dir Direction of the wall from the cell center.
    * @return true if the wall is set.
    */
   public boolean getWall(int x, int y, int dir)
   {
      //Check for the boundary conditions
      if ( ( (dir == NORTH) && (y == 1)) ||
          ( (dir == EAST) && (x == width)) ||
          ( (dir == SOUTH) && (y == height)) ||
          ( (dir == WEST) && (x == 1)))
         return true;

      if ( (x > width) || (y > height) || (x < 1) || (y < 1))
         return true;
      x--;
      y--;

      if (dir == NORTH)
         y--;
      if (dir == WEST)
         x--;

      if (dir == NORTH || dir == SOUTH)
         return rwalls.get(y * width + x);
      else
         return cwalls.get(x * height + y);
   }

   /**
    * Get an object that can then be checked or changed.
    * @param cell The cell containing the wall you want.
    * @param direction The direction of the wall you want from within the given
    *           cell.
    * @return A <code>MazeWall</code> of the specified location.
    */
   public MazeWall getWall(final MazeCell cell, final Direction direction)
   {
      return new MazeWall()
      {
         @Override
         public boolean isSet()
         {
            return getWall(cell.getX(), cell.getY(), direction.getIndex());
         }

         @Override
         public void set(boolean value)
         {
            if (value)
               setWall(cell.getX(), cell.getY(), direction.getIndex());
            else
               clearWall(cell.getX(), cell.getY(), direction.getIndex());
         }
      };
   }

   /**
    * Get the line of walls that surround the center.
    * @return
    */
   private List<MazeWall> getWinningBorderWalls()
   {
      final List<MazeWall> centerWalls = new ArrayList<MazeWall>();

      MazeCell[] winningCells = this.getWinningCells();
      if (winningCells.length != 4)
         throw new RuntimeException("This was written for winning cell groups of 4 only");
      centerWalls.add(this.getWall(winningCells[0], Direction.West));
      centerWalls.add(this.getWall(winningCells[0], Direction.North));
      centerWalls.add(this.getWall(winningCells[1], Direction.North));
      centerWalls.add(this.getWall(winningCells[1], Direction.East));
      centerWalls.add(this.getWall(winningCells[2], Direction.West));
      centerWalls.add(this.getWall(winningCells[2], Direction.South));
      centerWalls.add(this.getWall(winningCells[3], Direction.South));
      centerWalls.add(this.getWall(winningCells[3], Direction.East));

      return centerWalls;
   }

   /**
    * Gets the maze cells that make up the winning center of the maze. This
    * method should be used to allow the location of the winning cells to be
    * changed later.
    * @return An array of the winning cell locations in order.
    */
   public MazeCell[] getWinningCells()
   {
      final int centerY = height / 2;
      final int centerX = width / 2;
      MazeCell[] result = new MazeCell[]
      {
         new MazeCell(centerX, centerY), new MazeCell(centerX + 1, centerY),
         new MazeCell(centerX, centerY + 1), new MazeCell(centerX + 1, centerY + 1)
      };
      Arrays.sort(result);
      return result;
   }

   /**
    * This function is basically isLegal with the added capability of returning
    * the peg locations that are bad.<br />
    * There are rules that must be upheld to be a valid maze.<br />
    * Rule 1: There is a wall next to the starting square.<br />
    * Rule 2: There is one and only one way into the center of the maze.<br />
    * Even though I've coded this to accept a variable size in the future I
    * shall still assume here that the total size will always be even.
    * @return A <code>set</code> of points where each point represents an
    *         illegal peg.
    */
   public Set<MazeCellPeg> illegalPegs()
   {
      TreeSet<MazeCellPeg> badPoints = new TreeSet<MazeCellPeg>();

      int column = width / 2;
      int row = height / 2;

      if (!isCenterOpen())
      {
         badPoints.add(new MazeCellPeg(column - 1, row - 1));
         badPoints.add(new MazeCellPeg(column - 1, row));
         badPoints.add(new MazeCellPeg(column - 1, row + 1));
         badPoints.add(new MazeCellPeg(column, row - 1));
         badPoints.add(new MazeCellPeg(column, row + 1));
         badPoints.add(new MazeCellPeg(column + 1, row - 1));
         badPoints.add(new MazeCellPeg(column + 1, row));
         badPoints.add(new MazeCellPeg(column + 1, row + 1));
      } //All of the center pegs are connected

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if (!isCenterLegal())
         badPoints.add(new MazeCellPeg(column, row));

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for (int i = 1; i <= width; i++)
      {
         for (int j = 1; j <= height; j++)
         {
            if (!isPegLegal(i, j))
               badPoints.add(new MazeCellPeg(i, j));
         }
      }

      return badPoints;
   }

   /**
    * Is the peg in the center of the maze legal.
    * @return true if the center peg does not have any walls touching it.
    */
   public boolean isCenterLegal()
   {
      int row = height / 2;
      int column = width / 2;
      if (getWall(column, row, SOUTH) ||
          getWall(column, row, EAST) ||
          getWall(column + 1, row, SOUTH) ||
          getWall(column, row + 1, EAST))
         return false;
      return true;
   }

   /**
    * Is there a way in to the maze center.
    * @return true if the center of the maze has one and only one way in.
    */
   public boolean isCenterOpen()
   {
      int walls = 0;
      for (MazeWall wall : this.getWinningBorderWalls())
      {
         if (wall.isSet())
            walls++;
      }
      return walls == 7; // Only 7 out of 8 walls set.
   }

   /**
    * Is this a legal maze. Does not check if there is a clear path from the
    * start to the center.
    * @return true if all the pegs in the maze are legal.
    */
   public boolean isLegal()
   {
      //There are three rules that must be upheld to be a valid maze
      //Rule 1: There is a wall next to the starting square
      if (!getWall(1, height, EAST) || getWall(1, height, NORTH))
         return false;
      //Rule 2: There is one and only one way into the center of the maze
      //Even though I've coded this to accept a variable size in the future
      //I shall still assume here that the total size will always be even      
      int row = height / 2, column = width / 2;
      if (!isCenterLegal())
         return false;

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if (!isCenterOpen())
         return false;

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for (int i = 1; i <= width; i++)
      {
         for (int j = 1; j <= height; j++)
         {
            if ( (i == row && j == column) || isPegLegal(i, j))
               continue;
            return false;
         }
      }
      return true;
   }

   /**
    * The peg is legal if at least one wall is attached to it. The North-West
    * peg of the given Maze Cell is checked.
    * @param x The Cell X coordinate.
    * @param y The Cell Y coordinate.
    * @return true if the peg has at least one wall touching it.
    */
   public boolean isPegLegal(int x, int y)
   {
      //This function is designed to give a lower-level view to the isLegal
      //method by telling about a specific peg

      //The peg location is based off of the cell that it is in the NORTHWEST
      //corner of.  Peg (1,1) is the only peg touching cell (1,1)
      if (x < 1 || y < 1 || x >= width || y >= height)
         return true;
      //Every peg should have at least one wall attached to it
      if (getWall(x, y, SOUTH) ||
          getWall(x, y, EAST) ||
          getWall(x + 1, y + 1, NORTH) ||
          getWall(x + 1, y + 1, WEST))
      {
         //There are only three cases with walls that must be checked
         //First is the starting square
         if ( (x == 1) && (y == height - 1))
         {
            if (getWall(1, height, EAST) && !getWall(1, height, NORTH))
               return true;
            else
               return false;
         }

         //Second is that it is not the center peg
         else if (x == (width / 2) && y == (height / 2))
            return isCenterLegal();

         //Third is the pegs surrounding the center square
         else if ( (x <= width / 2 && y <= height / 2) &&
                  (x >= width / 2 - 1 && y >= height / 2 - 1))
            return isCenterOpen();
         //There are no more reasons to check the walls
         else
            return true;
      }
      else
      {
         //The only exception to no wall is the center peg
         if (x == width / 2 && y == height / 2)
            return true;
         else
            return false;
      }
   }

   public String loadMaze(InputStream in, boolean extended) throws IOException
   {
      if (extended)
      {
         DataInputStream dis = new DataInputStream(in);
         String name = dis.readUTF();
         setSize(new Dimension(dis.readInt(), dis.readInt()));

         int rwallSize = rwalls.size(), cwallSize = cwalls.size();
         for (int i = 0; i < rwallSize;)
         {
            byte bits = dis.readByte();
            for (int j = 0; j < 8 && i < rwallSize; j++, i++)
            {
               rwalls.set(i, (bits & 1) == 1);
               bits >>= 1;
            }
         }
         for (int i = 0; i < cwallSize;)
         {
            byte bits = dis.readByte();
            for (int j = 0; j < 8 && i < cwallSize; j++, i++)
            {
               cwalls.set(i, (bits & 1) == 1);
               bits >>= 1;
            }
         }
         return name;
      }
      else
      {
         //This is the formatting of the .MAZ files found online
         //This format is 4 times more inefficient than needs be
         //but it is a format that is already present
         byte[] fileContents = new byte[256];
         setSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));

         if (in.available() != 256)
            throw new IOException("Invalid format");

         in.read(fileContents);

         for (int i = 1; i <= 16; i++)
         {
            for (int j = 16; j > 0; j--)
            {
               byte cellWalls = fileContents[16 * (i - 1) + (16 - j)];
               if ( (cellWalls & 1) == 1)
                  this.setWall(i, j, NORTH);
               else
                  this.clearWall(i, j, NORTH);

               if ( (cellWalls & 2) == 2)
                  this.setWall(i, j, EAST);
               else
                  this.clearWall(i, j, EAST);

               if ( (cellWalls & 4) == 4)
                  this.setWall(i, j, SOUTH);
               else
                  this.clearWall(i, j, SOUTH);

               if ( (cellWalls & 8) == 8)
                  this.setWall(i, j, WEST);
               else
                  this.clearWall(i, j, WEST);
            } // for(int j=16; j>0; j--)
         } // for(int i=1; i<=16; i++)
         return null;
      }
   }

   public String loadMaze(String filename) throws FileNotFoundException, IOException
   {
      //Now for the actual file i/o
      File file = new File(filename);
      String name;
      FileInputStream in = null;
      in = new FileInputStream(file);
      if (filename.toLowerCase().endsWith("maz"))
         name = loadMaze(in, false); // Simple format.
      else
         name = loadMaze(in, true); // v2 format.
      in.close();

      return name;
   }

   public void saveMaze(String filename, String name) throws IOException
   {
      //This is the formatting of the .MAZ files found online
      //This format is 4 times more inefficient than needs be
      //but it is a format that is already present
      if (name == null)
      {
         byte[] fileContents = new byte[256];
         for (int i = 1; i <= 16; i++)
         {
            for (int j = 16; j > 0; j--)
            {
               MazeCell cell = new MazeCell(i, j);
               byte cellWalls = 0;
               if (this.getWall(cell, Direction.North).isSet())
               {
                  cellWalls = 1;
               }
               if (this.getWall(cell, Direction.East).isSet())
               {
                  cellWalls += 2;
               }
               if (this.getWall(cell, Direction.South).isSet())
               {
                  cellWalls += 4;
               }
               if (this.getWall(cell, Direction.West).isSet())
               {
                  cellWalls += 8;
               }

               fileContents[16 * (i - 1) + (16 - j)] = cellWalls;
            }
         }

         //Now for the actual file i/o
         if (filename.toLowerCase().endsWith(".maz") == false)
            filename = filename + ".maz";

         FileOutputStream out = new FileOutputStream(filename);
         out.write(fileContents);
         out.close();
      } // if (name == null)
      else
      {
         if (filename.toLowerCase().endsWith(".mz2") == false)
            filename = filename + ".mz2";

         FileOutputStream out = new FileOutputStream(filename);
         DataOutputStream dis = new DataOutputStream(out);
         dis.writeUTF(name);
         dis.writeInt(width);
         dis.writeInt(height);

         int rwallSize = rwalls.size(), cwallSize = cwalls.size();
         int rwallArraySize = rwalls.size() / 8;
         if (rwalls.size() % 8 > 0)
            rwallArraySize++;
         int cwallArraySize = cwalls.size() / 8;
         if (cwalls.size() % 8 > 0)
            cwallArraySize++;
         byte[] rwallArray = new byte[rwallArraySize];
         byte[] cwallArray = new byte[cwallArraySize];
         for (int i = 0; i < rwallArray.length; i++)
            rwallArray[i] = 0;
         for (int i = 0; i < cwallArray.length; i++)
            cwallArray[i] = 0;

         int index = 0;

         for (int i = 0; i < rwallSize; i++)
         {
            rwallArray[index] |= ( (rwalls.get(i) ? 1 : 0) << (i % 8));
            if ( (i + 1) % 8 == 0)
               index++;
         }

         index = 0;

         for (int i = 0; i < cwallSize; i++)
         {
            cwallArray[index] |= ( (cwalls.get(i) ? 1 : 0) << (i % 8));
            if ( (i + 1) % 8 == 0)
               index++;
         }

         dis.write(rwallArray);
         dis.write(cwallArray);
         dis.close();
      }

   }

   /**
    * Set/Enable every wall in the maze.
    */
   public void setAllWalls()
   {
      this.rwalls.set(0, this.rwalls.size());
      this.cwalls.set(0, this.cwalls.size());
   }

   /**
    * Set the size dimensions of this maze. The maze dimensions must always be
    * even numbers.
    * @param size The new number of rows and columns that make up the size of
    *           this maze.
    */
   public void setSize(Dimension size)
   {
      width = size.width + size.width % 2;
      height = size.height + size.height % 2;
      rwalls = new BitSet(width * (height - 1));
      cwalls = new BitSet( (width - 1) * height);
   }

   public void setWall(int x, int y, int dir)
   {
      //Check for the boundary conditions
      //First make sure that we're in the maze
      if ( (x < 1) || (y < 1) || (x > width) || (y > height))
         return;
      //Second make sure that the wall isn't implied
      if ( ( (dir == NORTH) && (y == 1)) ||
          ( (dir == EAST) && (x == width)) ||
          ( (dir == SOUTH) && (y == height)) ||
          ( (dir == WEST) && (x == 1)))
         return;

      x--;
      y--;

      if (dir == NORTH)
         y--;
      if (dir == WEST)
         x--;

      if (dir == NORTH || dir == SOUTH)
      {
         if (!rwalls.get(y * width + x))
         {
            rwalls.set(y * width + x);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }
      else
      {
         if (!cwalls.get(x * height + y))
         {
            cwalls.set(x * height + y);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }

   }
}