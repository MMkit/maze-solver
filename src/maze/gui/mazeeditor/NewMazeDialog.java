/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractSpinnerModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeListener;

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

      mGroup = new ButtonGroup();

      mOldSelect = new JRadioButton(".maz");
      ActionListener l = new RadioButtonAction(mOldSelect);
      mOldSelect.addActionListener(l);
      mNewSelect = new JRadioButton(".mz2");
      mNewSelect.addActionListener(l);
      mWidth = new JSpinner(new MazeSizeSpinnerModel());
      mHeight = new JSpinner(new MazeSizeSpinnerModel());
      mGroup.add(mOldSelect);
      mGroup.setSelected(mOldSelect.getModel(), true);
      mGroup.add(mNewSelect);
      Box upDown = new Box(BoxLayout.Y_AXIS);
      Box typeSelect = new Box(BoxLayout.Y_AXIS);
      typeSelect.add(mOldSelect);
      typeSelect.add(mNewSelect);
      upDown.add(typeSelect);
      Box sizeSelect = new Box(BoxLayout.X_AXIS);
      Box labelComp = new Box(BoxLayout.Y_AXIS);
      
      labelComp.add(mWidthLabel = new JLabel("Width"));
      labelComp.add(mWidth);
      sizeSelect.add(labelComp);
      labelComp = new Box(BoxLayout.Y_AXIS);
      labelComp.add(mHeightLabel = new JLabel("Height"));
      labelComp.add(mHeight);
      sizeSelect.add(labelComp);
      labelComp = new Box(BoxLayout.Y_AXIS);
      labelComp.add(mNameLabel = new JLabel("Name"));
      mName = new JTextField();
      labelComp.add(mName);
      upDown.add(sizeSelect);
      upDown.add(labelComp);
      
      setResizable(false);
      setOpsEnabled(false);
      JButton ok = new JButton("Ok");
      ok.addActionListener(new ActionListener()
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
      });

      upDown.add(ok);
      add(upDown);
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
