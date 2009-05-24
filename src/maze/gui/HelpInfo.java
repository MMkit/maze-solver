package maze.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * This show help information
 * @author Norwit Veun
 */

public class HelpInfo extends JPanel implements TreeSelectionListener
{
   private JEditorPane htmlPane;
   private JTree tree;
   private URL helpURL;

   public HelpInfo()
   {
      super(new GridLayout(1, 0));
      //Create the nodes.
      pageInfo p = new pageInfo("Micro Mouse Maze Editor and Simulator", "help.html");
      DefaultMutableTreeNode top = new DefaultMutableTreeNode(p);
      createNodes(top);

      // create single select tree
      tree = new JTree(top);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      //Listen for when the selection changes.
      tree.addTreeSelectionListener(this);

      //Create the scroll pane and add the tree to it. 
      JScrollPane treeView = new JScrollPane(tree);

      //Create the HTML viewing pane.
      htmlPane = new JEditorPane();
      htmlPane.setEditable(false);
      initHelpPage();
      JScrollPane htmlView = new JScrollPane(htmlPane);

      //Add the scroll panes to a split pane.
      JSplitPane splitPane = new JSplitPane();
      splitPane.setLeftComponent(treeView);
      splitPane.setRightComponent(htmlView);

      Dimension minimumSize = new Dimension(100, 50);
      htmlView.setMinimumSize(minimumSize);
      treeView.setMinimumSize(minimumSize);
      splitPane.setDividerLocation(200);
      splitPane.setPreferredSize(new Dimension(500, 300));

      //Add the split pane to this panel.
      add(splitPane);
   }

   public void valueChanged(TreeSelectionEvent e)
   {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

      if (node == null)
         return;

      Object nodeInfo = node.getUserObject();

      pageInfo page = (pageInfo) nodeInfo;
      displayURL(page.pageURL);
   }

   private class pageInfo
   {
      public String pageName;
      public URL pageURL;

      public pageInfo(String page, String filename)
      {
         pageName = page;
         pageURL = HelpInfo.class.getResource("html/" + filename);
         if (pageURL == null)
         {
            System.err.println("Couldn't find file: " + filename);
         }
      }

      public String toString()
      {
         return pageName;
      }
   }

   private void displayURL(URL url)
   {
      try
      {
         if (url != null)
         {
            htmlPane.setPage(url);
         }

         else
         {
            htmlPane.setText("File Not Found");
         }
      }

      catch (IOException e)
      {
         System.err.println("Attempted to read a bad URL: " + url);
      }
   }

   private void createNodes(DefaultMutableTreeNode top)
   {
      DefaultMutableTreeNode category = null;
      DefaultMutableTreeNode page = null;

      category = new DefaultMutableTreeNode(new pageInfo("Micro Mouse Simulator", "sim.html"));
      top.add(category);

      page = new DefaultMutableTreeNode(new pageInfo("Simulation Interface", "sim_feat.html"));
      category.add(page);

      category = new DefaultMutableTreeNode(new pageInfo("Maze Editor", "maze_editor.html"));
      top.add(category);

      category = new DefaultMutableTreeNode(new pageInfo("AI Script Editor", "script_editor.html"));
      top.add(category);

      page = new DefaultMutableTreeNode(new pageInfo("Script API", "api1.html"));
      category.add(page);

      page = new DefaultMutableTreeNode(new pageInfo("Maze Methods", "api2.html"));
      category.add(page);

      category = new DefaultMutableTreeNode(new pageInfo("Statistics Display", "stat.html"));
      top.add(category);
   }

   private void initHelpPage()
   {
      String help = "html/help.html";
      helpURL = HelpInfo.class.getResource(help);
      if (helpURL == null)
      {
         System.err.println("Couldn't open help file: " + help);
      }
      displayURL(helpURL);
   }

   /**
    * Create the GUI and show it.
    */
   public static void createAndShowGUI()
   {
      //Create and set up the window.
      JFrame frame = new JFrame("Help Info");

      //Create and set up the content pane.
      HelpInfo newContentPane = new HelpInfo();
      newContentPane.setOpaque(true); //content panes must be opaque
      frame.setContentPane(newContentPane);
      //Locate it on the master frame.
      frame.setLocation(maze.Main.getPrimaryFrameInstance().getLocation());

      //Display the window.
      frame.setSize(800, 600);
      frame.setVisible(true);
   }
}
