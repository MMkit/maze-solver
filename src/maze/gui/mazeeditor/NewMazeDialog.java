/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.AbstractSpinnerModel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author desolc
 */
public class NewMazeDialog extends JDialog
{
   public static final String MAZ = "maz";
   public static final String MZ2 = "mz2";
   private JTextField mName;
   private JSpinner mWidth, mHeight;
   private ButtonGroup mGroup;
   private JRadioButton mOldSelect, mNewSelect;
   private JLabel mWidthLabel, mHeightLabel, mNameLabel;
   private String mSelection = null;

   public NewMazeDialog(Frame owner)
   {
      super(owner, "New Maze", true);

      JPanel mainPanel = new JPanel(new GridBagLayout());

      // Create Button Group and radio buttons
      mGroup = new ButtonGroup();
      mOldSelect = new JRadioButton(".maz (A standard 16x16 with no name)");
      mOldSelect.setActionCommand(".maz");
      ActionListener l = new RadioButtonAction(mOldSelect);
      mOldSelect.addActionListener(l);
      mNewSelect = new JRadioButton(".mz2 (Custom Size and Name)");
      mNewSelect.setActionCommand(".mz2");
      mNewSelect.addActionListener(l);
      mGroup.add(mOldSelect);
      mGroup.setSelected(mOldSelect.getModel(), true);
      mGroup.add(mNewSelect);

      // Add radio buttons
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.ipadx = 0;
      gbc.gridx = gbc.gridy = 0;
      gbc.gridwidth = 2;
      gbc.anchor = GridBagConstraints.WEST;
      mainPanel.add(mOldSelect, gbc);
      gbc.gridy++;
      mainPanel.add(mNewSelect, gbc);

      // Create and add the labels for the maze size spinners
      mWidthLabel = new JLabel("Width");
      mHeightLabel = new JLabel("Height");
      gbc.gridy = 2;
      gbc.gridwidth = 1;
      mainPanel.add(mWidthLabel, gbc);
      gbc.gridx = 1;
      mainPanel.add(mHeightLabel, gbc);

      // Create and add the maze size spinners
      mWidth = new JSpinner(new MazeSizeSpinnerModel());
      mHeight = new JSpinner(new MazeSizeSpinnerModel());
      mWidth.setFocusable(false);
      mWidth.setRequestFocusEnabled(false);
      mHeight.setFocusable(false);
      mHeight.setRequestFocusEnabled(false);
      gbc.gridx--;
      gbc.gridy++;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = .5;
      mainPanel.add(mWidth, gbc);
      gbc.gridx++;
      mainPanel.add(mHeight, gbc);
      gbc.weightx = 0;
      gbc.fill = GridBagConstraints.NONE;

      // Create and add name label
      mNameLabel = new JLabel("Name");
      gbc.gridy++;
      gbc.gridx--;
      gbc.gridwidth = 2;
      mainPanel.add(mNameLabel, gbc);

      // Create and add name text field
      mName = new JTextField();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridy++;
      mainPanel.add(mName, gbc);
      gbc.fill = GridBagConstraints.NONE;

      // Create listener for OK button and text field Enter action
      l = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            if (mNewSelect.isSelected())
            {
               if (mName.getText() == null || mName.getText().equals(""))
                  mName.setBackground(Color.PINK);
               else
               {
                  mSelection = MZ2;
                  setVisible(false);
               }
            }
            else
            {
               mSelection = MAZ;
               setVisible(false);
            }
         }
      };

      // Add ActionListener to name field
      mName.addActionListener(l);

      // Create Ok button, add it and add the ActionListener to it
      JButton ok = new JButton("Ok");
      ok.addActionListener(l);
      gbc.gridy++;
      gbc.anchor = GridBagConstraints.CENTER;
      mainPanel.add(ok, gbc);

      // Set a border for the main panel and it to the dialog
      mainPanel.setBorder(new EmptyBorder(5,5,5,5));
      add(mainPanel);

      setResizable(false);
      setOpsEnabled(false);

      pack();
      Point loc = owner.getLocation();
      Dimension size = owner.getSize();
      this.setBounds(loc.x+size.width/2-this.getWidth()/2,
                     loc.y+size.height/2-this.getHeight()/2,
                     getWidth(), getHeight());
   }

   public String showDialog()
   {
      mSelection = null;
      setVisible(true);
      return mSelection;
   }

   public String getText()
   {
      return mName.getText();
   }

   public Dimension getMazeSize()
   {
      return new Dimension((Integer)mWidth.getValue(),
                           (Integer)mHeight.getValue());
   }

   private void setOpsEnabled(boolean enabled)
   {
      mHeight.setEnabled(enabled);
      mWidth.setEnabled(enabled);
      mName.setEnabled(enabled);
      mWidthLabel.setEnabled(enabled);
      mHeightLabel.setEnabled(enabled);
      mName.setEnabled(enabled);
      mNameLabel.setEnabled(enabled);
      mName.requestFocus();
   }

   class RadioButtonAction implements ActionListener
   {
      private JRadioButton last;
      public RadioButtonAction(JRadioButton last)
      {
         this.last = last;
      }
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
         if (e.getSource().equals(last))
            return;
         if (e.getActionCommand().equals(".maz"))
            setOpsEnabled(false);
         else
            setOpsEnabled(true);
         last = (JRadioButton)e.getSource();
      }

   }
}

class MazeSizeSpinnerModel extends AbstractSpinnerModel
{
   private int mCurrentValue = 16;
   @Override
   public Object getValue()
   {
      return mCurrentValue;
   }

   @Override
   public void setValue(Object value)
   {
      int val;
      if (value instanceof Integer)
         val = (Integer)value;
      else if (value instanceof String)
      {
         try
         {
            val = Integer.parseInt((String)value);
         }
         catch (NumberFormatException ex)
         {
            return;
         }
      }
      else
         return;

      if (val <= 0)
         return;

      if (val%2 == 1)
         val++;
      mCurrentValue = val;
   }

   @Override
   public Object getNextValue()
   {
      mCurrentValue+=2;
      fireStateChanged();
      return mCurrentValue;
   }

   @Override
   public Object getPreviousValue()
   {
      mCurrentValue = Math.max(mCurrentValue-2, 4);
      fireStateChanged();
      return mCurrentValue;
   }
}


