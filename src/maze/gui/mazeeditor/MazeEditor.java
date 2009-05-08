/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.net.URL;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 *
 * @author desolc
 */
public class MazeEditor extends JPanel
{
   private ImageIcon mPointIcon;
   private MazeTemplate mCurrentTemplate = null;
   private EditableMazeView mMazeView;
   private JList mOpenMazes;
   private MouseAdapter mMouseAdapter = null;

   private static final MazeTemplate[] mTemplates =
   {
      new BoxTemplate(),
      new StraightTemplate(),
      new CornerTemplate(),
      new CrossTemplate(),
      new ZigZagTemplate()
   };

   public MazeEditor()
   {
      buildPanel();
   }

   private void buildPanel()
   {
      URL iconResource = BoxTemplate.class.getResource("images/Pointer.png");
      mPointIcon = new ImageIcon(iconResource);

      setLayout(new BorderLayout());
      final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      splitPane.setResizeWeight(.8);
      splitPane.setOneTouchExpandable(true);

      mMazeView = new EditableMazeView();
      mMazeView.setEditable(true);
      splitPane.setLeftComponent(mMazeView);

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

      mMouseAdapter = new MouseAdapter()
      {
         @Override
         public void mouseMoved(MouseEvent e)
         {
            if (mCurrentTemplate != null)
            {
               mCurrentTemplate.updatePosition(e.getPoint());
               mMazeView.repaint();
            }
         } // public void mouseMoved(MouseEvent e)

         @Override
         public void mouseDragged(MouseEvent e)
         {
            if (mCurrentTemplate != null)
            {
               boolean left = SwingUtilities.isLeftMouseButton(e);
               boolean right = SwingUtilities.isRightMouseButton(e);
               mMazeView.repaint();
            }
         } // public void mouseDragged(MouseEvent e)

         @Override
         public void mousePressed(MouseEvent e)
         {
            if (mCurrentTemplate != null)
            {
               if (e.getButton() == MouseEvent.BUTTON2)
                  mCurrentTemplate.nextOrientation();
               else if (e.getButton() == MouseEvent.BUTTON1)
                  mMazeView.applyTemplate(true);
               else if (e.getButton() == MouseEvent.BUTTON3)
                  mMazeView.applyTemplate(false);
               mMazeView.repaint();
            }
         } // public void mousePressed(MouseEvent e)

         @Override
         public void mouseWheelMoved(MouseWheelEvent e)
         {
            if (mCurrentTemplate == null)
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

      };
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
      //mTDP.repaint();
         
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
}
