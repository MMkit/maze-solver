package maze.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import maze.model.Maze;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Creates a robot AI script editor that allows the creation of Python scripts
 * to control the AI robot.
 * @author Luke Last
 */
public class CodeEditorPanel extends JSplitPane
{

   private final RSyntaxTextArea textArea = new RSyntaxTextArea();
   private final String[] excludedStrings = new String[]
   {
      "clone", "finalize", "getClass", "hashCode", "notify", "notifyAll", "wait",
   };

   /**
    * Constructor.
    */
   public CodeEditorPanel()
   {
      //Set up the scroll pane that holds the code editor.
      final RTextScrollPane scrollPane = new RTextScrollPane();
      scrollPane.setViewportView(this.textArea);
      scrollPane.setLineNumbersEnabled(true);

      //Set up the documentation browser.
      final JEditorPane docPane = new JEditorPane();
      docPane.setEditable(false);
      docPane.setContentType("text/html");
      docPane.setText("<html><h2>This page will contain documentation</h2><p>blah blah</p></html>");

      //Set up the documentation tabs.
      final JTabbedPane docTabs = new JTabbedPane();

      docTabs.add("Code Information", new CodeInformationPanel());
      docTabs.add("API Documentation", docPane);

      //Set up the code editor.
      this.textArea.setMarkOccurrences(true);
      this.textArea.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
      this.textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_PYTHON);

      //Set up split pane.
      this.setLeftComponent(scrollPane);
      this.setRightComponent(docTabs);
      this.setResizeWeight(.8);

      JMenuItem menuItem = new JMenuItem("Analyze Object");
      this.textArea.getPopupMenu().addSeparator();
      this.textArea.getPopupMenu().add(menuItem);
      menuItem.addActionListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            analyzeSelection();
         }
      });

      JMenuItem evalMenuItem = new JMenuItem("Evaluate Variable");
      this.textArea.getPopupMenu().add(evalMenuItem);
      evalMenuItem.addActionListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            evaluateSelection();
         }
      });

      Action action = new AbstractAction()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            executeScript();
         }
      };

      this.textArea.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                                                                             InputEvent.SHIFT_DOWN_MASK),
                                                      action);
   }

   private void evaluateSelection()
   {
      try
      {
         PythonInterpreter interp = this.getInitializedInterpreter();
         PyObject obj = interp.eval(this.textArea.getSelectedText());
         JOptionPane.showMessageDialog(this, obj.toString());
      }
      catch (Exception e)
      {
         JOptionPane.showMessageDialog(this,
                                       e.toString(),
                                       "Error Evaluating Variable!",
                                       JOptionPane.ERROR_MESSAGE,
                                       null);
      }
   }

   private void analyzeSelection()
   {
      try
      {
         PythonInterpreter interp = this.getInitializedInterpreter();
         PyObject selectedObj = interp.get(this.textArea.getSelectedText());
         PyObject obj = interp.eval("dir(" + this.textArea.getSelectedText() + ")");
         PyList pyList = (PyList) obj;
         StringBuilder sb = new StringBuilder();
         for (Object o : pyList)
         {
            final String s = o.toString();
            boolean match = false;
            for (String exclude : excludedStrings)
            {
               if (exclude.equals(s))
               {
                  match = true;
                  break;
               }
            }
            if (match == false && (s.startsWith("__") == false || s.equals("__doc__")))
            {
               sb.append(o.toString());
               sb.append('\n');
            }
         }
         String title = "Available Methods";
         if (selectedObj != null)
         {
            title = selectedObj.getType().toString();
         }

         JOptionPane.showMessageDialog(this,
                                       sb.toString(),
                                       title,
                                       JOptionPane.INFORMATION_MESSAGE,
                                       null);
      }
      catch (Exception e2)
      {
         JOptionPane.showMessageDialog(this,
                                       e2.toString(),
                                       "Error analyzing variable",
                                       JOptionPane.ERROR_MESSAGE,
                                       null);
      }
   }

   private PythonInterpreter getInitializedInterpreter()
   {
      final PythonInterpreter interp = new PythonInterpreter();
      interp.exec("from maze.ai import RobotStep");
      interp.exec("from maze.model import WallDirection, MazeCell");
      interp.set("maze", new Maze());

      interp.exec(this.textArea.getText());
      return interp;
   }

   private void executeScript()
   {
      try
      {
         final PythonInterpreter interp = this.getInitializedInterpreter();

         PyObject obj = interp.get("nextStep");
         JOptionPane.showMessageDialog(this, obj.__call__());

         System.out.println(obj.getType());
         System.out.println(obj.toString());
         System.out.println(obj.__call__());
      }
      catch (Exception e)
      {
         JOptionPane.showMessageDialog(this,
                                       e.toString(),
                                       "Script Error",
                                       JOptionPane.ERROR_MESSAGE,
                                       null);
      }
   }

   private final class CodeInformationPanel extends JPanel
   {
   }
}
