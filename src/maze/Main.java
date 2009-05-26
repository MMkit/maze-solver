package maze;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
            primaryFrame = new PrimaryFrame();
            primaryFrame.init();
            primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            primaryFrame.setVisible(true);
         }
      });
   }

   /**
    * Utility method used to get image resources for button images while
    * handling failures gracefully.
    * @param imagePath The path to the image relative to the location of this
    *           class. So starting in the 'maze' package.
    */
   public static ImageIcon getImageResource(String imagePath)
   {
      try
      {
         return new ImageIcon(Main.class.getResource(imagePath));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JOptionPane.showMessageDialog(null,
                                       "Cannot Load Image: " + imagePath + "\nException: " + e,
                                       "Error Loading Resource",
                                       JOptionPane.ERROR_MESSAGE);
         //Return a blank image on load error.
         return new ImageIcon(new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB));
      }
   }
}