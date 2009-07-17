package maze.gui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Creates a panel to display information about the current python script. This
 * class is a singelton.
 * @author Luke Last
 */
public final class ScriptInfoPanel extends JPanel
{
   private static ScriptInfoPanel instance;
   private final JList list = new JList();
   private final DefaultListModel model = new DefaultListModel();
   private final JLabel labelErrorStatus = new JLabel();
   private final JLabel labelSelectedToken = new JLabel();
   private final JLabel labelTokenValue = new JLabel();
   private final JLabel labelTokenType = new JLabel();

   public synchronized static ScriptInfoPanel getInstance()
   {
      if (instance == null)
         instance = new ScriptInfoPanel();
      return instance;
   }

   /**
    * Constructor, build the panel.
    */
   private ScriptInfoPanel()
   {
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      this.list.setModel(this.model);
      this.add(this.labelErrorStatus);
      this.add(this.labelSelectedToken);
      this.add(this.labelTokenType);
      this.add(this.labelTokenValue);
      final JScrollPane scrollpane = new JScrollPane(this.list);
      scrollpane.setBorder(BorderFactory.createTitledBorder("Available Methods"));
      this.add(scrollpane);
   }

   public JList getList()
   {
      return list;
   }

   public DefaultListModel getListModel()
   {
      return this.model;
   }

   public void setScriptError(boolean scriptHasError)
   {
      if (scriptHasError)
      {
         this.labelErrorStatus.setText("<html>Script Status: <b><font color=red>Errors</font></b>");
      }
      else
      {
         this.labelErrorStatus.setText("<html>Script Status: <b><font color=green>OK</font></b>");
      }
   }

   public void setTokenName(String token)
   {
      if (token.isEmpty())
         this.labelSelectedToken.setText("No token selected");
      else
         this.labelSelectedToken.setText("Selected Token: " + token);
   }

   public void setTokenEval(String tokenValue)
   {
      this.labelTokenValue.setText("Token Eval: " + tokenValue);
   }

   public void setTokenType(String tokenType)
   {
      this.labelTokenType.setText("Token Type: " + tokenType);
   }
}
