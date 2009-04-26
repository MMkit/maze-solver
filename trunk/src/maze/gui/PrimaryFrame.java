package maze.gui;

import javax.swing.JFrame;

/**
 * This is the primary frame for the application.
 * @author Luke Last
 */
public final class PrimaryFrame extends JFrame
{
   /**
    * Constructor.
    */
   public PrimaryFrame()
   {
      this.setSize( 800, 700 );

      this.add( new MazeView() );
   }
}
