MazeEditor is freeware.

You can edit walls using mouse click and/or drag.
If you find any errors, let me know. (james@microrobotna.com)
Also, I you have any new maze file(s), which were used for recent  competitions since 2000, please email me.

(Reference)
MAZ file consists of 256 bytes wall info data.

000 byte = (0,0) wall info
001 byte = (0,1) wall info
.
.
256 byte = (15,15) wall info


Each byte contains the following info bits.

  0   0   0   0   W  S  E  N
(MSB)                     (LSB)

N = North Wall Bit (1=exist, 0=no wall)
E = East  Wall Bit (1=exist, 0=no wall)
S = South Wall Bit (1=exist, 0=no wall)
W = West  Wall Bit (1=exist, 0=no wall)

by James Jeong (james@microrobotna.com)
www.microrobotna.com