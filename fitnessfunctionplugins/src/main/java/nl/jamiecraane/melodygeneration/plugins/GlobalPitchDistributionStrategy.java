package nl.jamiecraane.melodygeneration.plugins;

import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import net.miginfocom.swing.MigLayout;

/**
 * Strategy which counts large pitch differences as errors.
 */
public final class GlobalPitchDistributionStrategy extends AbstractMelodyFitnessStrategy {
	private double pitchAdherenceThreshold = 0.98D;
	private int maximumPitchDifferenceInSemitones = 8;

    private JSlider distributionSlider;
    private JSpinner distributionSpinner;

    /**
	 * How many percent of the notes in a melody must fall within the maximum pitch difference?
	 * @param pitchAdherenceThreshold Must be between 0.0D and 1.0D. Default = 0.85.
	 */
	public void setPitchAdherenceThreshold(double pitchAdherenceThreshold) {
		this.pitchAdherenceThreshold = pitchAdherenceThreshold;
	}

	/**
	 * What is the maximum difference in pitch between the notes in a melody. Default is 8.
	 * @param maximumPitchDifferenceInSemitones
	 */
	public void setMaximumPitchDifferenceInSemitones(int maximumPitchDifferenceInSemitones) {
		this.maximumPitchDifferenceInSemitones = maximumPitchDifferenceInSemitones;
	}

	@Override
	public double calculateErrors(IChromosome melody) {
		long numberOfNotesThatMustBeInMaximumPitch = Math.round(melody.getGenes().length * this.pitchAdherenceThreshold);
		int numberOfNotesNotInPitchDistribution = 0;
        Note startOfMelody = null;
		for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene(gene);
            if (startOfMelody != null) {
                if (!startOfMelody.isRest() && !note.isRest()) {
                    int differenceInSemitones = startOfMelody.getDifferenceInSemiTones(note);
                    if (differenceInSemitones > this.maximumPitchDifferenceInSemitones) {
                        numberOfNotesNotInPitchDistribution++;
                    }
                }
            } else {
                if (!note.isRest()) {
                    startOfMelody = note;
                }
            }
        }
	
        return Math.abs((melody.getGenes().length - numberOfNotesNotInPitchDistribution) - numberOfNotesThatMustBeInMaximumPitch);
	}

    @Override
    public void init(JPanel container) {
        container.setLayout(new MigLayout());
        container.add(new JLabel("Global pitch distribution threshold"));
        final JLabel distributionLabel = new JLabel(String.valueOf((int)(pitchAdherenceThreshold*100)));
        distributionSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(pitchAdherenceThreshold*100));
        distributionSlider.setPaintLabels(true);
        distributionSlider.setMajorTickSpacing(10);
        distributionSlider.setMinorTickSpacing(1);
        distributionSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                distributionLabel.setText(String.valueOf(source.getValue()));
            }
        });
        container.add(distributionSlider);
        container.add(distributionLabel, "wrap");
        container.add(new JLabel("Maximum pitch difference in semitones"));
        distributionSpinner = new JSpinner(new SpinnerNumberModel(maximumPitchDifferenceInSemitones,0,100,1));
        container.add(distributionSpinner, "span 2");
    }

    public void bind() {
        this.maximumPitchDifferenceInSemitones = (Integer) this.distributionSpinner.getValue();
        this.pitchAdherenceThreshold = this.distributionSlider.getValue() / 100;
    }

    public String getName() {
        return "Global pitch distribution";
    }

    public String toString() {
        return "[GlobalPitchDistributionStrategy[pitchAdherenceThreshold: " + this.pitchAdherenceThreshold + ", maximumPitchDifferenceInSemitones: " + this.maximumPitchDifferenceInSemitones + "]]";
    }
}
