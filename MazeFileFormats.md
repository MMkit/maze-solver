# Introduction #

There are 2 file formats used. A (**.MAZ**) file which is used in other applications and has a fixed size 16x16 maze. And the new (**.MZ2**) file format we created which creates smaller files, supports a maze name, and supports mazes of any dimension.


# MAZ File Format #

The .MAZ file format was created by James Jeong for use in an application called MazeEditor. We use this format for compatibility with existing mazes.

The .MAZ file consists of 256 bytes of wall info data. Each byte has a predetermined wall location associated with it.

  * 000 byte = (0,0) wall info
  * 001 byte = (0,1) wall info
  * 002 byte = (0,2) wall info
  * .
  * .
  * 015 byte = (0,15) wall info
  * 016 byte = (1,0) wall info
  * 017 byte = (1,1) wall info
  * .
  * .
  * 254 byte = (15,14) wall info
  * 255 byte = (15,15) wall info

An equation for finding the wall info byte number from (x,y) cell coordinates (0 indexed) is (byte number = 16  x + y).

Cell location (0,0) is the bottom left corner cell, and location (15,15) is the top right corner.

Each byte contains the following info bits. The 4 most significant bits are not used.

(MSB) 0 0 0 0 **W** **S** **E** **N** (LSB)

  * N = North Wall Bit (1=exist, 0=no wall)
  * E = East  Wall Bit (1=exist, 0=no wall)
  * S = South Wall Bit (1=exist, 0=no wall)
  * W = West  Wall Bit (1=exist, 0=no wall)

# MZ2 File Format #

The mz2 file is a binary file of the following format:

**maze\_name width height row\_wall\_bits column\_wall\_bits**

**maze\_name**
a UTF-8 formatted string representing the name of the maze

**width**
a 32-bit signed big-endian integer representing the width of the maze in the number of horizontal cells.

**height**
a 32-bit signed big-endian integer representing the height of the maze in the number of vertical cells.

**row\_wall\_bits**
an array of bytes with individual bits specifying the existance of horizontal walls.  The first byte of the array specifies the existance of the first eight walls, the second byte the next eight and so on.  These bits specify the walls starting at the top left, running left to right, top to bottom.  The least significant bit of the first byte will be the first top left wall and the second LSB is the next wall immediately to the right.   The number of bytes in this array will always be a multiple of 8. The number of useful bits in this array is equal to
`width * (height - 1)`

**column\_wall\_bits**
an array of bytes with individual bits specifying the existence of vertical walls.  The first byte of the array specifies the existence of the first eight walls, the second byte the next eight and so on.  These bits specify the walls starting at the top left, running top to bottom, left to right.  The least significant bit of the first byte will be the first top left wall and the second LSB is the next wall immediately below the first.   The number of bytes in this array will always be a multiple of 8. The number of useful bits in this array is equal to
`height * (width - 1)`

The reason that both of the wall arrays have a multiple of 8 bytes is that the Java BitSet class stores bits in an array of QWORDs or 64-bit longs.  It was not intentional to have these arrays be a multiple of 8 but was merely a side effect of the BitSet.size() method.

## Example ##

The example below of a maze called "Test" shows what the bit values would be based on the walls in the example.  `R[M,N]` specifies the value of the Nth bit of the Mth byte of the row\_wall\_bits byte array.  `C[M,N]` specifies the value of the Nth bit of the Mth byte of the column\_wall\_bits byte array.

This maze will produce a file with the following hex values.<br />
<font color='red'>00 04</font><font color='blue'> 54 65 73 74</font><font color='green'> 00 00 00 06</font><font color='magenta'> 00 00 00 06</font><font color='orange'> ac 02 76 20 00 00 00 00</font><font color='brown'> a5 17 72 05 00 00 00 00</font>

<font color='red'>Length of maze name</font><br />
<font color='blue'>The string "Test"</font><br />
<font color='green'>The width of the maze in number of cells</font><br />
<font color='magenta'>The height of the maze in number of cells</font><br />
<font color='orange'>The array of horizontal wall bits</font><br />
<font color='brown'>The array of vertical wall bits</font>

![http://maze-solver.googlecode.com/files/MZ2_Example.png](http://maze-solver.googlecode.com/files/MZ2_Example.png)