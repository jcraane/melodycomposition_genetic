package nl.jamiecraane.melodygeneration.plugins;

import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Scale;
import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Strategy which calculates the errors based on the fact if the given notes are on the same scale. The more notes differ
 * from the given scale, the higher the error count.
 * 
 * The default selected scale is {@link Scale#C_MAJOR}
 */
public final class ScaleStrategy extends AbstractMelodyFitnessStrategy {
	private static final int ERROR_COUNT_WHEN_NOTE_NOT_ON_SCALE = 1;

    public String getSelectedScale() {
        return selectedScale;
    }

    private String selectedScale = Scale.C_MAJOR.toString();

    @Override
	// The more difference between current note and note in scale the higher the error count
	public double calculateErrors(IChromosome melody) {
		double errors = 0.0D;
		
		for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene(gene);
            if (Pitch.REST != note.getPitch() && !super.scale.contains(note.getPitch())) {
                errors += ERROR_COUNT_WHEN_NOTE_NOT_ON_SCALE;
            }
        }

		// Adhering to a given scale is quite important so square the result
		return (errors * errors) * 10;
	}

    @Override
    public void init(JPanel container) {
        container.setLayout(new GridLayout(5,5));
        container.setBorder(BorderFactory.createTitledBorder("Select scale"));
        ButtonGroup buttonGroup = new ButtonGroup();
        for (Scale scale : Scale.values()) {
            JRadioButton radioButton = new JRadioButton(scale.toString());
            radioButton.setSelected(scale == Scale.C_MAJOR);
            radioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectedScale = e.getActionCommand();
                }
            });
            buttonGroup.add(radioButton);
			container.add(radioButton);
        }
    }

    public String getName() {
        return "Scale";
    }

    public String toString() {
        return "[ScaleStrategy[scale: " + this.scale + "]]";
    }
}
