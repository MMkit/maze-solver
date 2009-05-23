package maze.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import maze.ai.PythonScriptRobot;
import maze.ai.RobotBase;
import maze.ai.RobotStep;
import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Creates an AI script text editor in Python.
 * @author Luke Last
 */
public final class CodeEditorPane extends RTextScrollPane
{
   public static final String NEW_SCRIPT_NAME = "New Python Script";
   private static final String PYTHON_FILE_EXTENSION = ".py";
   private static final String ROBOT_MODEL_VAR_NAME = "maze";
   private final RSyntaxTextArea textArea = new RSyntaxTextArea();
   private RobotBase connectedRobot;
   private PythonInterpreter currentInterpreter;
   /**
    * The File (if any) that is attached to this editor.
    */
   private File scriptFile;

   private final String[] excludedStrings = new String[]
   {
      "clone", "finalize", "getClass", "hashCode", "notify", "notifyAll", "wait",
   };

   public CodeEditorPane(File fileToOpen)
   {
      this();
      this.scriptFile = fileToOpen;
      try
      {
         CharBuffer cb = CharBuffer.allocate(1024 * 16);
         Reader r = new InputStreamReader(new FileInputStream(fileToOpen), "UTF-8");
         while (0 < r.read(cb))
            ;
         cb.flip();
         this.textArea.setText(cb.toString());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public CodeEditorPane()
   {
      //Set up the scroll pane that holds the code editor.
      super.setViewportView(this.textArea);
      super.setLineNumbersEnabled(true);

      //Set up the code editor.
      this.textArea.setMarkOccurrences(true);
      this.textArea.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
      this.textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_PYTHON);

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

      this.addToRobotModel();
   }

   @Override
   public String toString()
   {
      if (this.scriptFile != null)
      {
         return this.scriptFile.getName();
      }
      else
      {
         return NEW_SCRIPT_NAME;
      }
   }

   /**
    * Adds a RobotBase instance to the global Robot list. This new AI instance
    * is connected to this code editor.
    */
   private void addToRobotModel()
   {
      this.connectedRobot = new PythonScriptRobot(this);
      RobotBase.getRobotListModel().addElement(this.connectedRobot);
   }

   /**
    * Removes this script from the global List of algorithms.
    */
   void removeFromRobotModel()
   {
      RobotBase.getRobotListModel().removeElement(this.connectedRobot);
   }

   /**
    * Requests the user to choose a new file name and then saves the script
    * there.
    */
   public void saveScriptFileAs()
   {
      JFileChooser chooser = new JFileChooser();
      int result = chooser.showSaveDialog(this);
      if (result == JFileChooser.APPROVE_OPTION)
      {
         // Make sure the chosen file name has the correct extension.
         if (chooser.getSelectedFile().getName().endsWith(PYTHON_FILE_EXTENSION))
         {
            this.scriptFile = chooser.getSelectedFile();
         }
         else
         {
            this.scriptFile = new File(chooser.getSelectedFile().getAbsolutePath() +
                                       PYTHON_FILE_EXTENSION);
         }
      }
      this.saveToFile();
   }

   /**
    * Saves the current script to the file it is connected to. If it is a new
    * script and has no file that save as is called.
    */
   public void saveScriptFile()
   {
      // If this script doesn't already have a file name do a save as prompt.
      if (this.scriptFile == null)
      {
         this.saveScriptFileAs();
      }
      else
      {
         this.saveToFile();
      }
   }

   /**
    * Internal method to save the script to the current file.
    */
   private void saveToFile()
   {
      if (this.scriptFile != null)
      {
         Writer w = null;
         try
         {
            w = new OutputStreamWriter(new FileOutputStream(this.scriptFile), "UTF-8");
            w.write(this.textArea.getText());
            w.close();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         finally
         {
            try
            {
               w.close();
            }
            catch (IOException e)
            {}
         }
      }
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

   /**
    * @return
    */
   public PythonInterpreter getInitializedInterpreter()
   {
      final PythonInterpreter interp = new PythonInterpreter();
      interp.exec("from maze.ai import RobotStep");
      interp.exec("from maze.model import Direction, MazeCell");
      interp.set("Forward", RobotStep.MoveForward);
      interp.set("Back", RobotStep.MoveBackward);
      interp.set("Left", RobotStep.RotateLeft);
      interp.set("Right", RobotStep.RotateRight);
      //We create and set a dummy maze variable so the user can analyze its methods.
      interp.set(ROBOT_MODEL_VAR_NAME, new RobotModel(new RobotModelMaster(new MazeModel(),
                                                                           new MazeCell(1, 16),
                                                                           Direction.North)));
      interp.exec(this.textArea.getText());
      this.currentInterpreter = interp;
      return interp;
   }

   public void setRobotModel(RobotModel model)
   {
      try
      {
         this.currentInterpreter.set(ROBOT_MODEL_VAR_NAME, model);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public RobotStep getNextStep()
   {
      try
      {
         final PyObject function = this.currentInterpreter.get("nextStep");
         Object result = function.__call__();
         return RobotStep.valueOf(result.toString());
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         //throw new RuntimeException(ex);
         return RobotStep.MoveForward;
      }
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
}
