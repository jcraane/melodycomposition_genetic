package nl.jamiecraane.melodygeneration.plugins;

import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * Intervals are measured from the root note of a specific scale to the actual note.
 * When this strategy is used it is implied that the given melody contains as much root notes as there must
 * be major or perfect intervals.
 * For example: when the numberOfMajorIntervals is set to 2, the root note of the scale must
 * appear at least two times in the melody. The second note after the root note must form the 
 * major interval.
 * 
 * There is no support for diminished or augmented intervals yet.
 */
public final class IntervalStrategy extends AbstractMelodyFitnessStrategy {
	// We have a major second, third, sixth and seventh
	private int numberOfMajorIntervals = 1;
	// We have a perfect fourth, fifth
	private int numberOfPerfectIntervals = 1;

    // UI components for manipulating the state of this strategy
    private JSpinner majorIntervalSpinner;
    private JSpinner perfectIntervalSpinner;

	public IntervalStrategy() {
	}

	/**
	 * Sets the number of perfect intervals the melody must contain. Default 0.
	 * @param numberOfPerfectIntervals
	 */
	public void setNumberOfPerfectIntervals(int numberOfPerfectIntervals) {
		this.numberOfPerfectIntervals = numberOfPerfectIntervals;
	}

    /**
     * Sets the numer of major intervals the melody must contain. Default 0.
     * @param numberOfMajorIntervals
     */
    public void setNumberOfMajorIntervals(int numberOfMajorIntervals) {
        this.numberOfMajorIntervals = numberOfMajorIntervals;
    }

    @Override
	public double calculateErrors(IChromosome melody) {
		double errors = 1.0D;
		int actualMajorIntervals = 0, actualPerfectIntervals = 0;
		
        Note previousNote = null;
        for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene(gene);
            if (previousNote != null) {
                if (currentNoteAndPreviousNoteAreNotes(previousNote, note)) {
                    if (super.scale.isMajorInterval(previousNote.getPitch(), note.getPitch())) {
                        actualMajorIntervals++;
                    }
                    if (super.scale.isPerfectInterval(previousNote.getPitch(), note.getPitch())) {
                        actualPerfectIntervals++;
                    }
                }
            }

            if (!note.isRest()) {
                previousNote = note;
            }
        }
		
		errors += Math.abs(this.numberOfMajorIntervals - actualMajorIntervals);
		errors += Math.abs(this.numberOfPerfectIntervals - actualPerfectIntervals);

        if (Double.compare(errors, 1.0D) == 0) {
			errors = 0.0D;
		}
		
		// The interval strategy is quite important so square the result
		return errors * errors;
	}

    @Override
    public void init(JPanel container) {
        container.setLayout(new MigLayout());
        container.add(new JLabel("Number of major intervals"));
        majorIntervalSpinner = new JSpinner(new SpinnerNumberModel(1,0,100,1));
        container.add(majorIntervalSpinner, "wrap");
        container.add(new JLabel("Number of perfect intervals"));
        perfectIntervalSpinner = new JSpinner(new SpinnerNumberModel(1,0,100,1));
        container.add(perfectIntervalSpinner, "wrap");
    }

    public void bind() {
        this.numberOfMajorIntervals = (Integer) this.majorIntervalSpinner.getValue();
        this.numberOfPerfectIntervals = (Integer) this.perfectIntervalSpinner.getValue();
    }

    public String getName() {
        return "Major/Perfect intervals";
    }

    private boolean currentNoteAndPreviousNoteAreNotes(Note previousNote, Note currentNote) {
        return !previousNote.isRest() && !currentNote.isRest();
    }

    public String toString() {
        return "[IntervalStrategy[numberOfMajorIntervals: " + this.numberOfMajorIntervals + ", numberOfPerfect�ntervals: " + this.numberOfPerfectIntervals + "]]";
    }
}
