package maze.gui;

import maze.model.MazeModel;

/**
 * This is used to tie the different maze views together when needed.
 */
public interface MazeViewInterface
{
   public void setModel(MazeModel model);
}
