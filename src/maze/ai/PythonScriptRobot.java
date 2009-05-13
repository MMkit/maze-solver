package maze.ai;

import maze.gui.CodeEditorPane;

public final class PythonScriptRobot extends RobotBase
{
   private final CodeEditorPane codeEditor;

   public PythonScriptRobot(CodeEditorPane codeEditor)
   {
      this.codeEditor = codeEditor;
   }

   @Override
   public String toString()
   {
      return this.codeEditor.toString();
   }

   @Override
   public void initialize()
   {
      super.initialize();
      this.codeEditor.getInitializedInterpreter();
      this.codeEditor.setRobotModel(super.robotLocation);
   }

   @Override
   public RobotStep nextStep()
   {
      return this.codeEditor.getNextStep();
   }

}
