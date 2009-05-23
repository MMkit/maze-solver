package maze.gui.mazeeditor;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import javax.swing.filechooser.FileFilter;
import maze.Main;
import maze.gui.MazeList;
import maze.gui.MenuControlled;
import maze.model.MazeInfo;
import maze.model.MazeInfoModel;
import maze.model.MazeModel;

/**
 * This panel creates a GUI to edit mazes.
 * @author John Smith
 */
public class MazeEditor extends JPanel implements MenuControlled
{
   private static final String NEXT_ORIENTATION_ACTION_KEY = "nextOrientation";
   private static final String TOP_HELP =
           "Left-Click: Add walls, Right-Click: " +
           "Remove walls, Middle-Click or R Key: Change template Orientation, "+
           "Wheelup: Grow template, Wheeldown: Shrink template";
   private static final String POPUP_HELP =
           "<html><b>Left-Click</b>: Add walls<br /><b>Right-Click</b>: " +
           "Remove walls<br /><b>Middle-Click</b> or <b>R Key</b>: Change template " +
           "Orientation<br /><b>Wheelup</b>: Grow template<br />" +
           "<b>Wheeldown</b>: Shrink template</btml>";
   private final ImageIcon mPointIcon = Main.getImageResource("gui/mazeeditor/images/Pointer.png");
   private MazeTemplate mCurrentTemplate = null;
   private EditableMazeView mMazeView;
   private MazeList mOpenMazes;
   private MouseAdapter mMouseAdapter = null;
   private int mLastNew = 1;

   private static final MazeTemplate[] mTemplates =
   {
      new BoxTemplate(),
      new StraightTemplate(),
      new CornerTemplate(),
      new CrossTemplate(),
      new ZigZagTemplate(),
      new WaveTemplate(),
      new StaggeredTemplate(),
      new JungleTemplate(),
      new RungTemplate(),
      new GappedTemplate()
   };

   public MazeEditor()
   {
      buildPanel();
   }

   public void newMaze()
   {
      NewMazeDialog dialog;
      String result;
      dialog = new NewMazeDialog(Main.getPrimaryFrameInstance());
      if ((result = dialog.showDialog()) == null)
         return;

      MazeInfoModel mim =
              Main.getPrimaryFrameInstance().getMazeInfoModel();

      if (result.equals(NewMazeDialog.MAZ))
      {
         MazeInfo newMi = mim.createNew("New Maze " + mLastNew, false);
         if (newMi == null)
         {
            JOptionPane.showMessageDialog(MazeEditor.this,
                    "Unable to create new maze", "Maze Creation Error",
                    JOptionPane.OK_OPTION);
            return;
         } // if (newMi == null)
         mLastNew++;
      } // if (result.equals(NewMazeDialog.MAZ))
      else if (result.equals(NewMazeDialog.MZ2))
      {
         MazeInfo newMi = mim.createNew(dialog.getText(), true);
         if (newMi == null)
         {
            JOptionPane.showMessageDialog(MazeEditor.this,
                    "Unable to create new maze", "Maze Creation Error",
                    JOptionPane.OK_OPTION);
            return;
         } // if (newMi == null)
         MazeModel mm = newMi.getModel();
         mm.setSize(dialog.getMazeSize());
      } // else if (result.equals(NewMazeDialog.MZ2))
   }


   private void buildPanel()
   {
      setLayout(new BorderLayout());
      JLabel instr = new JLabel(TOP_HELP);
      instr.setToolTipText(POPUP_HELP);
      add(instr, BorderLayout.NORTH);

      final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      splitPane.setResizeWeight(.8);
      splitPane.setOneTouchExpandable(true);

      mMazeView = new EditableMazeView();
      mMazeView.setEditable(true);
      splitPane.setLeftComponent(mMazeView);
      mMazeView.setModel(null);
      
      this.mOpenMazes = new MazeList(this.mMazeView);
      JPanel rightPanel = new JPanel();
      rightPanel.setLayout(new BorderLayout());
      rightPanel.add(this.mOpenMazes, BorderLayout.CENTER);

      rightPanel.add(makeNewMazeButton(), BorderLayout.SOUTH);
      splitPane.setRightComponent(rightPanel);
      splitPane.addPropertyChangeListener("dividerLocation",
                                          new PropertyChangeListener()
      {
         @Override
         public void propertyChange(PropertyChangeEvent evt)
         {
            mMazeView.componentResized(null);
         }
      }); // new PropertyChangeListener()

      add(splitPane, BorderLayout.CENTER);
      splitPane.setDividerLocation(.8);

      addComponentListener(new ComponentAdapter()
      {
         boolean notShown = true;
         @Override
         public void componentShown(ComponentEvent e)
         {
            if (notShown)
            {
               splitPane.setDividerLocation(.8);
               mMazeView.componentResized(null);
               notShown = false;
            } // if (notShown)
         } // public void componentShown(ComponentEvent e)
      }); // addComponentListener(new ComponentAdapter()

      JToolBar tBar = new JToolBar();
      tBar.setOrientation(JToolBar.VERTICAL);
      tBar.setFloatable(false);
      ButtonGroup bg = new ButtonGroup();

      ImageIcon scaled = new ImageIcon(mPointIcon.getImage().getScaledInstance(40, 40, 0));
      JToggleButton tb = new JToggleButton(scaled);
      tb.setToolTipText("No Template");
      tb.addActionListener(new TemplateActionListener(null));
      bg.add(tb);
      bg.setSelected(tb.getModel(), true);
      tBar.add(tb);

      for (MazeTemplate mt : mTemplates)
      {
         Image iconImage = mt.getTemplateIcon().getImage();
         scaled = new ImageIcon(iconImage.getScaledInstance(40, 40, 0));
         tb = new JToggleButton(scaled);
         tb.addActionListener(new TemplateActionListener(mt));
         tb.setToolTipText(mt.getTemplateDescription());
         bg.add(tb);
         tBar.add(tb);
      }

      add(tBar, BorderLayout.WEST);
      
      //Add an action for the 'r' key to change the template to the next orientation.
      this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('r'),
                                                                          NEXT_ORIENTATION_ACTION_KEY);
      this.getActionMap().put(NEXT_ORIENTATION_ACTION_KEY, new AbstractAction()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            if (mCurrentTemplate != null)
            {
               mCurrentTemplate.nextOrientation();
               mMazeView.repaint();
            }
         }
      });
      

      mMouseAdapter = new TemplateMouseAdapter();
      
      mMazeView.addMouseListener(mMouseAdapter);
      mMazeView.addMouseMotionListener(mMouseAdapter);
      mMazeView.addMouseWheelListener(mMouseAdapter);
   } // void buildPanel()

   private void setTemplate(MazeTemplate mt)
   {
      if (mt == mCurrentTemplate)
         return;

      mCurrentTemplate = mt;
      if (mCurrentTemplate == null)
         mMazeView.setEditable(true);
      else
      {
         mMazeView.setEditable(false);
         mt.reset();
      }
      mMazeView.setTemplate(mt);
   }

   private JButton makeNewMazeButton()
   {
      JButton newMaze = new JButton("New Maze");
      newMaze.setToolTipText("Create a new maze");
      newMaze.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            newMaze();
         }
      });
      return newMaze;
   }

   @Override
   public void saveCurrent()
   {
      MazeInfo mi = (MazeInfo)mOpenMazes.getList().getSelectedValue();
      if (mi != null)
      {
         mi = Main.getPrimaryFrameInstance().saveMaze(mi);
         mOpenMazes.getList().setSelectedValue(mi, true);
      }
   }

   @Override
   public void open()
   {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new MazeFileFilter());
      if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this))
      {
         MazeInfoModel mim = Main.getPrimaryFrameInstance().getMazeInfoModel();
         File selected = chooser.getSelectedFile();
         String path = "";
         try
         {
            path = selected.getAbsolutePath();
         }
         catch(SecurityException ex)
         {
            String msg = "You do not have permission to open this file";
            JOptionPane.showMessageDialog(this, msg,
                                          "Maze Open Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }
         if (!mim.addMaze(selected))
            JOptionPane.showMessageDialog(this, "<html>Unable to load " +
                                          "<br />" + path + "</html",
                                          "Maze Open Error",
                                          JOptionPane.ERROR_MESSAGE);

      }
   }

   class TemplateActionListener implements ActionListener
   {
      private MazeTemplate mt;
      public TemplateActionListener(MazeTemplate mt)
      {
         this.mt = mt;
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         setTemplate(mt);
      }
   }

   private class TemplateMouseAdapter extends MouseAdapter
   {
      @Override
      public void mouseMoved(MouseEvent e)
      {
         if (mMazeView.getModel() != null && mCurrentTemplate != null)
         {
            mCurrentTemplate.updatePosition(e.getPoint(),
                                            mMazeView.getCellSize());
            mMazeView.repaint();
         }
      } // public void mouseMoved(MouseEvent e)

      @Override
      public void mouseDragged(MouseEvent e)
      {
         if (mMazeView.getModel() != null && mCurrentTemplate != null)
         {
            //boolean left = SwingUtilities.isLeftMouseButton(e);
            //boolean right = SwingUtilities.isRightMouseButton(e);
            mMazeView.repaint();
         }
         mOpenMazes.repaint();
      } // public void mouseDragged(MouseEvent e)

      @Override
      public void mousePressed(MouseEvent e)
      {
         if (mMazeView.getModel() != null && mCurrentTemplate != null)
         {
            if (e.getButton() == MouseEvent.BUTTON2)
               mCurrentTemplate.nextOrientation();
            else if (e.getButton() == MouseEvent.BUTTON1)
               mMazeView.applyTemplate(true);
            else if (e.getButton() == MouseEvent.BUTTON3)
               mMazeView.applyTemplate(false);
            mMazeView.repaint();
         }
         mOpenMazes.repaint();
      } // public void mousePressed(MouseEvent e)
      
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
         if (mMazeView.getModel() == null || mCurrentTemplate == null)
            return;
         int amount = e.getWheelRotation();
         boolean neg = amount < 0;
         amount = Math.abs(amount);
         for (int i = 0; i < amount; i++)
         {
            if (neg)
               mCurrentTemplate.grow();
            else
               mCurrentTemplate.shrink();
         }
         mMazeView.repaint();
      }
   }
}

class MazeFileFilter extends FileFilter
{
   @Override
   public boolean accept(File f)
   {
      if (f.isDirectory())
         return true;
      String lower = f.getName().toLowerCase();
      if (lower.endsWith(".maz") || lower.endsWith(".mz2"))
         return true;
      return false;
   }

   @Override
   public String getDescription()
   {
      return "Maze files (.maz, .mz2)";
   }
}
