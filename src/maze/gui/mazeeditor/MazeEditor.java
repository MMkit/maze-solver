/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze.gui.mazeeditor;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;

import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import maze.gui.MazeView;

/**
 *
 * @author desolc
 */
public class MazeEditor extends JPanel
{

   private MazeView mMazeView;
   private JList mOpenMazes;
   private static final String[] mIconNames =
   {
      "Pointer", "Zig Zag", "Wave",
      "Box", "Staggered", "Cross",
      "Corner", "Straight"
   };
   private static final ImageIcon[] mIcons =
   {
      new ImageIcon("Pointer.png"),
      new ImageIcon("ZigZag.png"),
      new ImageIcon("Wave.PNG"),
      new ImageIcon("Box.png"),
      new ImageIcon("Staggered.png"),
      new ImageIcon("Cross.png"),
      new ImageIcon("Corner.png"),
      new ImageIcon("Straight.png")
   };

   public MazeEditor()
   {
      buildPanel();
   }

   void buildPanel()
   {
      setLayout(new BorderLayout());
      mMazeView = new MazeView();
      mMazeView.setEditable(true);
      final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      splitPane.setResizeWeight(.8);
      splitPane.setOneTouchExpandable(true);
      JPanel leftPanel = new JPanel();
      leftPanel.setLayout(new BorderLayout());
      leftPanel.add(mMazeView, BorderLayout.CENTER);
      splitPane.setLeftComponent(leftPanel);

      mOpenMazes = new JList();
      JPanel rightPanel = new JPanel();
      rightPanel.setLayout(new BorderLayout());
      rightPanel.add(mOpenMazes, BorderLayout.CENTER);
      mOpenMazes.setBorder(new TitledBorder("Mazes"));
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
               notShown = false;
            } // if (notShown)
         } // public void componentShown(ComponentEvent e)
      }); // addComponentListener(new ComponentAdapter()

      JToolBar tBar = new JToolBar();
      tBar.setOrientation(JToolBar.VERTICAL);
      tBar.setFloatable(false);
      ButtonGroup bg = new ButtonGroup();
      for (int i = 0; i < Math.min(mIconNames.length, mIcons.length); i++)
      {
         ImageIcon scaled = new ImageIcon(mIcons[i].getImage().getScaledInstance(40, 40, 0));
         JToggleButton tb = new JToggleButton(scaled);
         tb.setToolTipText(mIconNames[i]);
         bg.add(tb);
         if (i == 0)
         {
            bg.setSelected(tb.getModel(), true);
         }
         tBar.add(tb);
      }

      add(tBar, BorderLayout.WEST);
   } // void buildPanel()
}
