package maze;

import  javax.swing.JFrame;
import javax.swing.SwingUtilities;

import maze.gui.CodeEditorPanel;
import maze.gui.PrimaryFrame;

/**
 * The main starting point of the application.
 */
public final class Main
{
   /**
    * Holds a reference to the instance of the primary frame.
    */
   private static PrimaryFrame primaryFrame;

   /**
    * A global way to get an instance of the applications primary frame.
    */
   public static PrimaryFrame getPrimaryFrameInstance()
   {
      return primaryFrame;
   }

   /**
    * Classic main entry point function.
    */
   public static void main( String[] args )
   {
      //Set the GUI to be constructed on the Swing Event Dispatch Thread.
      SwingUtilities.invokeLater( new Runnable()
      {
         @Override
         public void run()
         {
            primaryFrame = new PrimaryFrame();
            primaryFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            primaryFrame.setVisible( true );

            // Test code for the script code editor panel.
            JFrame f = new JFrame();
            f.add( new CodeEditorPanel() );
            f.setSize( 800, 800 );
            f.setVisible( true );
         }
      } );
   }
}