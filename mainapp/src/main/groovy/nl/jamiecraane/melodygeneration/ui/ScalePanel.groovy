/**
 * 
 */
package nl.jamiecraane.melodygeneration.ui

import javax.swing.JPanel
import nl.jamiecraane.melodygeneration.Scale

import javax.swing.BoxLayout
import java.awt.GridLayout
import javax.swing.ButtonGroup
import javax.swing.JRadioButton
import java.awt.event.ActionListener
import javax.swing.BorderFactory

public final class ScalePanel extends JPanel {
	String selectedScale = Scale.C_MAJOR.toString();
	
	public ScalePanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel scalePanel = new JPanel(new GridLayout(3,4));		
		scalePanel.setBorder(BorderFactory.createTitledBorder("Select scale"));
		ButtonGroup buttonGroup = new ButtonGroup();
		Scale.values().each { Scale scale ->
			JRadioButton radioButton = new JRadioButton(scale.toString());
			radioButton.addActionListener( { selectedScale = it.actionCommand } as ActionListener )
            radioButton.selected = scale == Scale.C_MAJOR
            buttonGroup.add(radioButton);
			scalePanel.add(radioButton);
		}
		this.add(scalePanel);
	}
}
