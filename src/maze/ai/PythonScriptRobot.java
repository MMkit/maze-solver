package maze.ai;

import maze.Main;
import maze.gui.CodeEditorPanel;

public final class PythonScriptRobot extends RobotBase
{
   private CodeEditorPanel codeEditorPanel;

   @Override
   public String toString()
   {
      return "Custom Python Script";
   }

   @Override
   public void initialize()
   {
      super.initialize();
      this.codeEditorPanel = Main.getPrimaryFrameInstance().getCodeEditorPanel();
   }

   @Override
   public RobotStep nextStep()
   {
      return this.codeEditorPanel.getNextStep();
   }

}
