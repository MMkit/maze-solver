package maze;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
   public static void main(String[] args)
   {
      //Set the GUI to be constructed on the Swing Event Dispatch Thread.
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println(Arrays.toString(UIManager.getInstalledLookAndFeels()));
               //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
               //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
               //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
               UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
               
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
            primaryFrame = new PrimaryFrame();
            primaryFrame.init();
            primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            primaryFrame.setVisible(true);
         }
      });
   }
}