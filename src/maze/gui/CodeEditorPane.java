package maze.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import maze.ai.PythonScriptRobot;
import maze.ai.RobotBase;
import maze.ai.RobotStep;
import maze.model.Direction;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Creates an AI script text editor in Python.
 * @author Luke Last
 */
public final class CodeEditorPane extends RTextScrollPane implements ActionListener
{
   public static final String NEW_SCRIPT_NAME = "New Python Script";
   private static final String PYTHON_FILE_EXTENSION = ".py";
   private static final String ROBOT_MODEL_VAR_NAME = "maze";
   private final RSyntaxTextArea textArea = new RSyntaxTextArea();
   private transient RobotBase connectedRobot;
   private PythonInterpreter currentInterpreter;
   private final javax.swing.Timer timer = new javax.swing.Timer(1000, this);

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
      Reader r = null;
      try
      {
         CharBuffer cb = CharBuffer.allocate(1024 * 32);
         r = new InputStreamReader(new FileInputStream(fileToOpen), "UTF-8");
         while (0 < r.read(cb))
            ;
         cb.flip();
         this.textArea.setText(cb.toString());
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
   }

   public CodeEditorPane()
   {
      this.timer.setRepeats(false);
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

      this.textArea.addCaretListener(new CaretListener()
      {
         @Override
         public void caretUpdate(CaretEvent e)
         {
            timer.restart();
         }
      });

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
         this.evalScript(false);
         PythonInterpreter interp = this.getCurrentInterpreter();
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
         this.evalScript(false);
         PythonInterpreter interp = this.getCurrentInterpreter();
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
    * Creates a new Python interpreter and sets some initial values like imports
    * and the maze model variable.
    * @return A new interpreter.
    */
   private PythonInterpreter getInitializedInterpreter()
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
      return interp;
   }

   /**
    * Evaluates the code from the editor.
    * @return true if the script evaluated successfully, false if an error
    *         occurred while interpreting the script.
    */
   public boolean evalScript(boolean displayError)
   {
      final PythonInterpreter interp = this.getInitializedInterpreter();
      try
      {
         interp.exec(this.textArea.getText());
      }
      catch (Exception e)
      {
         if (displayError)
         {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                                          "There is an error in the Python AI script.\n" +
                                                e.toString(),
                                          "Script Error",
                                          JOptionPane.ERROR_MESSAGE);
         }
         return false;
      }
      this.currentInterpreter = interp;
      return true;
   }

   public PythonInterpreter getCurrentInterpreter()
   {
      return currentInterpreter;
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

   /**
    * Get the string token that is currently selected or that the cursor is on.
    * @return The selected text or an empty string on error or if nothing is
    *         selected.
    */
   private String getSelectedToken()
   {
      String selectedText = this.textArea.getSelectedText();
      // If text is selected just use that, otherwise select the nearest token.
      if (selectedText == null)
      {
         RSyntaxDocument doc = (RSyntaxDocument) textArea.getDocument();
         doc.readLock();
         try
         {
            // Get the token at the caret position.
            int line = textArea.getCaretLineNumber();
            Token tokenList = textArea.getTokenListForLine(line);
            int dot = this.textArea.getCaret().getDot();
            Token t = RSyntaxUtilities.getTokenAtOffset(tokenList, dot);
            if (t == null)
            {
               // Try to the "left" of the caret.
               dot--;
               try
               {
                  if (dot >= textArea.getLineStartOffset(line))
                  {
                     t = RSyntaxUtilities.getTokenAtOffset(tokenList, dot);
                  }
               }
               catch (BadLocationException ble)
               {
                  ble.printStackTrace(); // Never happens
               }

            }
            if (t != null)
            {
               try
               {
                  // A bug in this method sometimes throws a null pointer exception.
                  selectedText = t.getLexeme();
               }
               catch (NullPointerException e)
               {}
            }
         }
         finally
         {
            doc.readUnlock();
         }
      }
      if (selectedText == null)
         return "";
      else
         return selectedText;
   }

   private void updateMethodsList(String selectedToken)
   {
      try
      {
         final PythonInterpreter interp = this.getCurrentInterpreter();
         PyObject obj = interp.eval("dir(" + selectedToken + ")");
         PyList pyList = (PyList) obj;
         final CodeInformationPanel info = CodeInformationPanel.getInstance();
         info.getListModel().clear();
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
               info.getListModel().addElement(o.toString());
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private String getVariableType(String variableName)
   {
      final PythonInterpreter interp = this.getCurrentInterpreter();
      PyObject selectedObj = interp.get(variableName);
      if (selectedObj != null)

         return selectedObj.getType().toString();
      else
         return "Unknown";
   }

   private String getVariableValue(String variableName)
   {
      final PythonInterpreter interp = this.getCurrentInterpreter();
      PyObject selectedObj = interp.get(variableName);
      if (selectedObj != null)

         return selectedObj.toString();
      else
         return "Unknown";
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      final CodeInformationPanel info = CodeInformationPanel.getInstance();
      boolean evalOK = this.evalScript(false);
      info.setScriptError(!evalOK);
      final String selectedToken = this.getSelectedToken();
      this.updateMethodsList(selectedToken);
      info.setTokenName(selectedToken);
      info.setTokenType(this.getVariableType(selectedToken));
      info.setTokenEval(this.getVariableValue(selectedToken));

   }
}
