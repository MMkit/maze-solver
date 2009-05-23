package maze.gui;

/**
 *Provides a common set of methods for the maze editor and code editor panels
 * that provide access to file operations from the main file menu.
 * @author Johnathan Smith
 */
public interface MenuControlled
{
   public void saveCurrent();

   public void open();

   public void close();
}
