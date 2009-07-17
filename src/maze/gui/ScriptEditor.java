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
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public final class ScriptEditor extends RTextScrollPane implements ActionListener
{
   private static final String[] excludedStrings = new String[]
   {
      "clone", "finalize", "getClass", "hashCode", "notify", "notifyAll", "wait",
   };
   public static final String NEW_SCRIPT_NAME = "New Python Script";
   private static final String PYTHON_FILE_EXTENSION = ".py";
   private static final String ROBOT_MODEL_VAR_NAME = "maze";
   private final javax.swing.Timer codeEvalTimer = new javax.swing.Timer(1000, this);
   private transient RobotBase connectedRobot;
   private PythonInterpreter currentInterpreter;
   private boolean isDirty = false;

   /**
    * The File (if any) that is attached to this editor.
    */
   private File scriptFile;

   private final RSyntaxTextArea textArea = new RSyntaxTextArea();

   /**
    * Construct an empty editor.
    */
   public ScriptEditor()
   {

      //Set up the scroll pane that holds the code editor.
      super.setViewportView(this.textArea);
      super.setLineNumbersEnabled(true);

      //Set up the code editor.
      this.textArea.setMarkOccurrences(true);
      this.textArea.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
      this.textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_PYTHON);

      // Start the timer when the caret is moved in the document.
      this.codeEvalTimer.setRepeats(false);
      this.textArea.addCaretListener(new CaretListener()
      {
         @Override
         public void caretUpdate(CaretEvent e)
         {
            codeEvalTimer.restart();
         }
      });

      this.textArea.getDocument().addDocumentListener(new DocumentListener()
      {

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            isDirty = true;
         }

         @Override
         public void insertUpdate(DocumentEvent e)
         {
            isDirty = true;
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            isDirty = true;
         }
      });

      this.addToRobotModel();
   }

   /**
    * Create an editor from an existing file.
    * @param fileToOpen Python script file.
    */
   public ScriptEditor(File fileToOpen)
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
      this.isDirty = false;
   }

   /**
    * Timer event action. This is fired when the code information data should be
    * updated.
    * @see #codeEvalTimer
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final ScriptInfoPanel info = ScriptInfoPanel.getInstance();
      boolean evalOK = this.evalScript(false);
      info.setScriptError(!evalOK);
      final String selectedToken = this.getSelectedToken();
      this.updateMethodsList(selectedToken);
      info.setTokenName(selectedToken);
      info.setTokenType(this.getVariableType(selectedToken));
      info.setTokenEval(this.getVariableValue(selectedToken));

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
    * Evaluates the code from the editor.
    * @return true if the script evaluated successfully, false if an error
    *         occurred while interpreting the script.
    */
   public boolean evalScript(boolean displayError)
   {
      final PythonInterpreter interp = this.getInitializedInterpreter();
      this.currentInterpreter = interp;
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
      return true;
   }

   public PythonInterpreter getCurrentInterpreter()
   {
      return currentInterpreter;
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
    * Used to adapt this script into robot script.
    * @see PythonScriptRobot
    * @return Next step.
    */
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

   /**
    * Has this script been changed since being saved last.
    * @return true if this script has not been saved.
    */
   public boolean isDirty()
   {
      return isDirty;
   }

   /**
    * Removes this script from the global List of algorithms.
    */
   void removeFromRobotModel()
   {
      RobotBase.getRobotListModel().removeElement(this.connectedRobot);
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
         this.isDirty = false;
      }
   }

   /**
    * Dirty means the file has been changed since last being saved.
    * @param isDirty true if dirty.
    */
   public void setDirty(boolean isDirty)
   {
      this.isDirty = isDirty;
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

   private void updateMethodsList(String selectedToken)
   {
      try
      {
         final PythonInterpreter interp = this.getCurrentInterpreter();
         PyObject obj = interp.eval("dir(" + selectedToken + ")");
         PyList pyList = (PyList) obj;
         final ScriptInfoPanel info = ScriptInfoPanel.getInstance();
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
}
