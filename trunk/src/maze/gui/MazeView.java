package maze.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Observable;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.PegLocation;
import maze.model.MazeModel.MazeWall;

/**
 * A view of the maze model. This swing component will render a GUI of a maze
 * model displaying the maze, allowing the maze to be edited, and animating the
 * robot in the maze.
 * @author Luke Last
 */
public class MazeView extends JPanel implements ComponentListener
{
	//Temporary model.
	//private MazeModel model = new maze.model.MazeModelStub();

	/**
	 * The maze model that stores the configuration of the maze.
	 */
	private MazeModel model = new maze.model.Maze();
	/**
	 * This holds the sizes of the cells and walls.
	 */
	private final CellSizeModel csm = new CellSizeModel();
	/**
	 * Holds the active cell that the mouse is hovering over.
	 */
	private MazeCell active;

	/**
	 * Holds the pre-rendered background
	 */
	private BufferedImage background = null;

	public MazeModel getModel()
	{
		return model;
	}

	public void setModel(MazeModel model)
	{
		this.model = model;
	}

	/**
	 * Constructor.
	 */
	public MazeView()
	{
		//Create an event handler to handle mouse events.
		MouseAdapter mouseAdapter = new MouseAdapter()
		{

			@Override
			public void mouseMoved(MouseEvent e)
			{
				try
				{
					active = getHostMazeCell(e.getPoint());
					repaint();
				}
				catch (Exception ex)
				{
				}
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				try
				{
					active = getHostMazeCell(e.getPoint());
					MazeWall wall = getWall(e.getPoint());
					if (SwingUtilities.isLeftMouseButton(e))
					{
						wall.set(true);
					}
					else if (SwingUtilities.isRightMouseButton(e))
					{
						wall.set(false);
					}
					repaint();
				}
				catch (Exception ex)
				{
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				try
				{
					final MazeWall wall = getWall(e.getPoint());
					//Flip the status of the wall.
					wall.set(!wall.isSet());
					repaint();
				}
				catch (Exception ex)
				{
				}
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addComponentListener(this);
	}

	/**
	 * This is where the graphics of this component gets painted.
	 */
	@Override
	protected void paintComponent(final Graphics arg)
	{
		final Graphics2D g = (Graphics2D) arg;
		this.csm.setCellWidth(this.getWidth() / this.model.getSize().width);
		this.csm.setCellHeight(this.getHeight() / this.model.getSize().height);
		this.csm.setWallWidth(this.csm.getCellWidth() / 4);
		this.csm.setWallHeight(this.csm.getCellHeight() / 4);
		this.redrawAll(g);

	}

	private void redrawAll(final Graphics2D g)
	{
		final int mazeWidth = this.model.getSize().width * this.csm.getCellWidth();
		final int mazeHeight = this.model.getSize().height * this.csm.getCellHeight();
		final GradientPaint bgPaint = new GradientPaint(new Point(0, 0),
																										Color.WHITE,
																										new Point(mazeWidth / 2, mazeHeight / 2),
																										new Color(229, 236, 255),
																										true);
		final GradientPaint wallPaintEmpty = new GradientPaint(new Point(0, 0), //Gradient start corner.
																													 new Color(229, 236, 255), //Really light blue.
																													 new Point(mazeWidth / 2,
																																		 mazeHeight / 2),
																													 new Color(204, 218, 255), //Light blue.
																													 true);
		final GradientPaint wallPaintSet = new GradientPaint(new Point(0, 0),
																												 new Color(0, 94, 189),
																												 new Point(mazeWidth / 2,
																																	 mazeHeight / 2),
																												 new Color(0, 56, 112),
																												 true);
		if (background == null)
		{
			background = new BufferedImage(getWidth(), getHeight(),
																		 BufferedImage.TYPE_INT_ARGB);
			Graphics2D bgg = (Graphics2D)background.getGraphics();
			// Fill the
			bgg.setColor(Color.white);
			bgg.fillRect(0, 0, this.getWidth(), this.getHeight());
			//Paint the gradient maze background.
			bgg.setPaint(bgPaint);
			bgg.fillRect(0, 0, getWidth(), getHeight());


			bgg.setPaint(wallPaintEmpty);
			int[] ws = {csm.getWallWidth(), csm.getWallHeight()};
			int[] cs = {csm.getCellWidth(), csm.getCellHeight()};
			Dimension size = model.getSize();

			// Draw inactive walls
			for (int i = 0; i < size.width+1; i++)
				bgg.fillRect(i*cs[0] - ws[0]/2, 0, ws[0], mazeHeight);
			for (int i = 0; i < size.height+1; i++)
				bgg.fillRect(0, i*cs[1] - ws[1]/2, mazeWidth, ws[1]);

			// Draw outer walls
			bgg.setPaint(wallPaintSet);
			bgg.fillRect(-csm.getWallWidthHalf(), -csm.getWallHeightHalf(), mazeWidth, csm.getWallHeight());
			bgg.fillRect(-csm.getWallWidthHalf(), -csm.getWallHeightHalf(), csm.getWallWidth(), mazeHeight);
			bgg.fillRect(0, mazeHeight-csm.getWallHeightHalf(), mazeWidth, csm.getWallHeight());
			bgg.fillRect(mazeWidth-csm.getWallWidthHalf(), 0, csm.getWallWidth(), mazeHeight);

			// Draw pegs
			bgg.setColor(Color.BLACK);
			for (int i = 0; i <size.width+1; i++)
				for (int j =0; j < size.height+1; j++)
					bgg.fillRect(i*cs[0] - ws[0]/2, j*cs[1] - ws[1]/2, ws[0], ws[1]);

			
		}

		g.drawImage(background, 0, 0, null);

		for (int x = 0; x < this.model.getSize().width; x++)
		{
			for (int y = 0; y < this.model.getSize().height; y++)
			{
				final MazeCell cell = new MazeCell(x + 1, y + 1);

				final EnumSet<Direction> wallsToPaint = EnumSet.of(Direction.South,
																													 Direction.East);

				//We are painting the first cell in the row.
				if (cell.getX() == 1)
					wallsToPaint.add(Direction.West);

				//We are painting the top horizontal row.
				if (cell.getY() == 1)
					wallsToPaint.add(Direction.North);
				g.setPaint(wallPaintSet);
				for (Direction wall : wallsToPaint)
				{
					if (this.model.getWall(cell, wall).isSet())
						g.fill(this.getWallLocation(cell, wall));
				}

				if (this.active != null && this.active.equals(cell))
				{
					g.setColor(Color.YELLOW);
					g.fillRect(cell.getXZeroBased() *
									this.csm.getCellWidth() +
									this.csm.getWallWidthHalf(),
										 cell.getYZeroBased() *
									this.csm.getCellHeight() +
									this.csm.getWallHeightHalf(),
										 this.csm.getCellWidth() - this.csm.getWallWidth(),
										 this.csm.getCellHeight() - this.csm.getWallHeight());
				}

			} //End y loop.
		} //End x loop.
	} //End method.

	/**
	 * Turns a maze cell into global coordinates for the center of the cell.
	 */
	private Point getCellCenter(MazeCell cell)
	{
		return new Point((cell.getXZeroBased() * this.csm.getCellWidth()) +
						this.csm.getCellWidthHalf(),
										 (cell.getYZeroBased() * this.csm.getCellHeight()) +
						this.csm.getCellHeightHalf());
	}

	/**
	 * Get a rectangle covering the wall segment with component relative
	 * coordinates.
	 */
	private Rectangle getWallLocation(MazeCell cell, Direction wall)
	{
		Point center = this.getCellCenter(cell);
		if (wall == Direction.North)
		{
			return new Rectangle(center.x -
							(this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf()),
													 center.y -
							(this.csm.getCellHeightHalf() + this.csm.getWallHeightHalf()),
													 this.csm.getCellWidth() - this.csm.getWallWidth(),
													 this.csm.getWallHeight());
		}
		else if (wall == Direction.South)
		{
			return new Rectangle(center.x -
							(this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf()),
													 center.y +
							(this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf()),
													 this.csm.getCellWidth() - this.csm.getWallWidth(),
													 this.csm.getWallHeight());
		}
		else if (wall == Direction.East)
		{
			return new Rectangle(center.x +
							(this.csm.getCellWidthHalf() - this.csm.getWallWidthHalf()),
													 center.y -
							(this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf()),
													 this.csm.getWallWidth(),
													 this.csm.getCellHeight() - this.csm.getWallHeight());
		}
		else if (wall == Direction.West)
		{
			return new Rectangle(center.x -
							(this.csm.getCellWidthHalf() + this.csm.getWallWidthHalf()),
													 center.y -
							(this.csm.getCellHeightHalf() - this.csm.getWallHeightHalf()),
													 this.csm.getWallWidth(),
													 this.csm.getCellHeight() - this.csm.getWallHeight());
		}
		return new Rectangle(); //should never get here.
	}

	/**
	 * Get the absolute region of a peg with respect to the given maze cell.
	 */
	private Rectangle getPegRegion(MazeCell cell, PegLocation peg)
	{
		if (peg == PegLocation.TopLeft)
		{
			return new Rectangle((cell.getX() * this.csm.getCellWidth()) -
							this.csm.getWallWidthHalf() -
							this.csm.getCellWidth(),
													 (cell.getY() * this.csm.getCellHeight()) -
							this.csm.getCellHeight() -
							this.csm.getWallHeightHalf(),
													 this.csm.getWallWidth(),
													 this.csm.getWallHeight());
		}
		else if (peg == PegLocation.TopRight)
		{
			return new Rectangle(cell.getX() * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
													 (cell.getY() * this.csm.getCellHeight()) -
							this.csm.getCellHeight() -
							this.csm.getWallHeightHalf(),
													 this.csm.getWallWidth(),
													 this.csm.getWallHeight());
		}
		else if (peg == PegLocation.BottomRight)
		{
			return new Rectangle(cell.getX() * this.csm.getCellWidth() - this.csm.getWallWidthHalf(),
													 cell.getY() *
							this.csm.getCellHeight() -
							this.csm.getWallHeightHalf(),
													 this.csm.getWallWidth(),
													 this.csm.getWallHeight());
		}
		else if (peg == PegLocation.BottomLeft)
		{
			return new Rectangle(cell.getXZeroBased() *
							this.csm.getCellWidth() -
							this.csm.getWallWidthHalf(),
													 cell.getY() *
							this.csm.getCellHeight() -
							this.csm.getWallHeightHalf(),
													 this.csm.getWallWidth(),
													 this.csm.getWallHeight());
		}
		return new Rectangle(); //Should never get here.
	}

	/**
	 * Converts a mouse pointer position into the maze cell that it is in.
	 * @throws Exception If the pointer location is not within the maze.
	 */
	private MazeCell getHostMazeCell(Point pointerLocation) throws Exception
	{
		MazeCell cell = new MazeCell((pointerLocation.x / this.csm.getCellWidth()) + 1,
																 (pointerLocation.y / this.csm.getCellHeight()) + 1);
		if (cell.isInRange(this.model.getSize()))
		{
			return cell;
		}
		else
		{
			throw new Exception("The pointer location is outside of the current maze.");
		}
	}

	/**
	 * @param cell
	 * @param mouseLocation
	 * @return
	 * @throws java.lang.Exception If the mouse pointer isn't actually on a wall.
	 */
	private Direction getWallDirection(MazeCell cell, Point mouseLocation) throws Exception
	{
		for (Direction direction : Direction.values())
		{
			if (getWallLocation(cell, direction).contains(mouseLocation))
			{
				return direction;
			}
		}
		throw new Exception("Not inside a wall.");
	}

	/**
	 * Converts a mouse pointer location into an actual maze wall object.
	 * @param mouseLocation
	 * @return
	 * @throws java.lang.Exception If the mouse pointer isn't actually on a wall.
	 */
	private MazeWall getWall(Point mouseLocation) throws Exception
	{
		final MazeCell cell = getHostMazeCell(mouseLocation);
		return model.getWall(cell, getWallDirection(cell, mouseLocation));
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		background = null;
	}

	@Override
	public void componentMoved(ComponentEvent e){}

	@Override
	public void componentShown(ComponentEvent e){}

	@Override
	public void componentHidden(ComponentEvent e){}
	/**
	 * This model stores the sizes of the cells and wall segments that are drawn
	 * to the screen.
	 */
	static class CellSizeModel extends Observable
	{

		private int cellWidth = 44;
		private int cellHeight = 50;
		private int wallWidth = 10;
		private int wallHeight = 10;

		public void setCellWidth(int cellWidth)
		{
			if ((cellWidth & 1) == 1)
			{
				cellWidth--;
			}
			if (this.cellWidth != cellWidth)
			{
				this.cellWidth = cellWidth;
				super.setChanged();
			}
		}

		public void setCellHeight(int cellHeight)
		{
			if ((cellHeight & 1) == 1)
			{
				cellHeight--;
			}
			if (this.cellHeight != cellHeight)
			{
				this.cellHeight = cellHeight;
				super.setChanged();
			}
		}

		public void setWallWidth(int wallWidth)
		{
			if ((wallWidth & 1) == 1)
			{
				wallWidth++;
			}
			if (this.wallWidth != wallWidth)
			{
				this.wallWidth = wallWidth;
				super.setChanged();
			}
		}

		public void setWallHeight(int wallHeight)
		{
			if ((wallHeight & 1) == 1)
			{
				wallHeight++;
			}
			if (this.wallHeight != wallHeight)
			{
				this.wallHeight = wallHeight;
				super.setChanged();
			}
		}

		public int getCellWidth()
		{
			return cellWidth;
		}

		public int getCellHeight()
		{
			return cellHeight;
		}

		public int getWallWidth()
		{
			return wallWidth;
		}

		public int getWallHeight()
		{
			return wallHeight;
		}

		public int getCellWidthHalf()
		{
			return cellWidth / 2;
		}

		public int getCellHeightHalf()
		{
			return cellHeight / 2;
		}

		public int getWallWidthHalf()
		{
			return wallWidth / 2;
		}

		public int getWallHeightHalf()
		{
			return wallHeight / 2;
		}
	}
}
