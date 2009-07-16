package maze.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import maze.Main;

import org.python.util.PythonInterpreter;

/**
 * Creates a robot AI script editor that allows the creation of Python scripts
 * to control the AI robot.
 * @author Luke Last
 */
public class CodeEditingPanel extends JSplitPane implements MenuControlled
{
   private final JTabbedPane editorTabs = new JTabbedPane();

   /**
    * Constructor.
    */
   public CodeEditingPanel()
   {

      //Set up the documentation browser.
      final JEditorPane docPane = new JEditorPane();
      docPane.setEditable(false);
      docPane.setContentType("text/html");

      try
      {
         docPane.setPage(CodeEditingPanel.class.getResource("html/api.html"));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         docPane.setText("<html><h2>There was an error loading the documentation</h2></html>");
      }

      //Set up the documentation tabs.
      final JTabbedPane docTabs = new JTabbedPane();
      docTabs.setMinimumSize(new Dimension(200, 0));

      docTabs.add("Code Information", CodeInformationPanel.getInstance());
      docTabs.add("API Documentation", new JScrollPane(docPane));

      //Set up split pane.
      this.setContinuousLayout(true);
      this.setLeftComponent(this.editorTabs);
      this.setRightComponent(docTabs);
      this.doLayout();
      this.setResizeWeight(.8);
      //We set the divider after a delay so the HTML pane doesn't mess it up.
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            setDividerLocation(.7);
         }
      });
      this.activatePythonInBackground();
   }

   /**
    * Launches a background thread that initializes the Python interpreter which
    * can take a while when done the first time. We do this early so that the
    * interpreter runs quickly when it is needed.
    */
   private void activatePythonInBackground()
   {
      final Thread thread = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               // Give the application some time to launch before we start burning the cpu.
               Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {}
            new PythonInterpreter();
         }
      };
      thread.setDaemon(true);
      thread.setPriority(Thread.MIN_PRIORITY);
      thread.start();
   }

   /**
    * Creates a new script by prompting the user to choose a template and then
    * creates a new editor tab.
    */
   public void createNewEditor()
   {
      Object[] options = new Object[]
      {
         "Simple Go Straight", "Left Wall Follower"
      };
      Object result = JOptionPane.showInputDialog(maze.Main.getPrimaryFrameInstance(),
                                                  "Select from the list of available starting templates.",
                                                  "Select a Template",
                                                  JOptionPane.QUESTION_MESSAGE,
                                                  null,
                                                  options,
                                                  options[0]);
      final String contents;
      if (options[0].equals(result))
      {
         contents = "GoStraight.py";
      }
      else if (options[1].equals(result))
      {
         contents = "LeftWallFollower.py";
      }
      else
      {
         contents = null;
      }
      if (contents != null)
      {
         CodeEditorPane editor = new CodeEditorPane();
         editor.getTextArea().setText(this.loadScriptFromJar(contents));
         this.editorTabs.add(editor.toString(), editor);
         this.editorTabs.setSelectedComponent(editor);
      }

   }

   /**
    * Opens the given file name from the "maze/ai/python/" directory from the
    * jar file and returns the text contents from the file.
    * @param name
    * @return File contents or an empty string if file not found.
    */
   private String loadScriptFromJar(String name)
   {
      Reader r = null;
      try
      {
         CharBuffer cb = CharBuffer.allocate(1024 * 16);
         r = new InputStreamReader(Main.class.getResourceAsStream("ai/python/" + name), "UTF-8");
         while (0 < r.read(cb))
            ;
         cb.flip();
         return cb.toString();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if (r != null)
               r.close();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      return "";
   }

   /**
    * Closes the currently visible editor tab.
    */
   final Action closeScriptAction = new AbstractAction()
   {
      {
         this.putValue(Action.NAME, "Close AI Script");
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         CodeEditorPane editor = (CodeEditorPane) editorTabs.getSelectedComponent();
         if (editor != null)
         {
            editor.removeFromRobotModel();
            editorTabs.remove(editor);
         }

      }
   };

   /**
    * Saves the contents of the currently selected editor tab.
    */
   @Override
   public void saveCurrent()
   {
      try
      {
         CodeEditorPane editor = (CodeEditorPane) this.editorTabs.getSelectedComponent();
         if (editor != null)
         {
            editor.saveScriptFile();
            this.editorTabs.setTitleAt(this.editorTabs.indexOfComponent(editor), editor.toString());
         }
      }
      catch (RuntimeException ex)
      {
         ex.printStackTrace();
      }
   }

   @Override
   public void close()
   {
      closeScriptAction.actionPerformed(null);
   }

   @Override
   public String getFileTypeDescription()
   {
      return "Python Scripts";
   }

   @Override
   public boolean isMyFileType(File file)
   {
      return file.getName().toLowerCase().endsWith(".py");
   }

   @Override
   public void open(File file)
   {
      CodeEditorPane editor = new CodeEditorPane(file);
      this.editorTabs.add(file.getName(), editor);
      this.editorTabs.setSelectedComponent(editor);
   }
}
