package maze.gui;

import java.io.File;

/**
 * Provides a common set of methods for the maze editor and code editor panels
 * that provide access to file operations from the main file menu.
 * @author Johnathan Smith
 */
public interface MenuControlled
{
   /**
    * Closes the currently displayed item.
    */
   public void close();

   /**
    * Get a description for the types of files that this panel supports.
    * @return Description.
    */
   public String getFileTypeDescription();

   /**
    * Test whether the given file is of the type that this page supports.
    * @param file File to test.
    * @return true if it is a recognized file type.
    */
   public boolean isMyFileType(File file);

   /**
    * Open the file.
    * @param file The file to open.
    */
   public void open(File file);

   /**
    * Save the currently displayed item.
    */
   public void saveCurrent();

   /**
    * Perform cleanup operations like saving files before the application exits.
    * @return true if this page is ready for application exit, false if the exit
    *         should be stopped.
    */
   public boolean canExit();
}