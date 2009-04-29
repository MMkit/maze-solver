package maze.gui;

import javax.swing.JFrame;

/**
 * This is the primary frame for the application.
 */
public final class PrimaryFrame extends JFrame
{
   /**
    * Constructor.
    */
   public PrimaryFrame()
   {
      this.setSize( 1000, 750 );

      this.add( new MazeView() );
   }
}
