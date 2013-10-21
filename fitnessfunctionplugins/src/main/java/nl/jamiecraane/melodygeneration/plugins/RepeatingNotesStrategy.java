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
 * Strategy that counts errors when consecutive two notes of the same pitch are after each other in a given melody.
 * Please note that this strategy collides with the {@link ProportionRestAndNotesStrategy}. This does not mean
 * these two strategies cannot be used together but one must be aware of the consequences when extreme conditions
 * are specified.
 */
public final class RepeatingNotesStrategy extends AbstractMelodyFitnessStrategy {
	private int duplicateNotesThreshold = 2;
	private int duplicateRestThreshold = 1;

    private JSpinner duplicateNotesSpinner;
    private JSpinner duplicateRestSpinner;

    /**
	 * Sets how many notes of the same pitch may be after each other. Default is 2.
	 * @param duplicateNotesThreshold
	 */
	public void setDuplicateThreshold(int duplicateNotesThreshold) {
        if (duplicateNotesThreshold < 0) {
            throw new IllegalArgumentException("duplicateNotesThreshold must be greater than 0");
        }

        this.duplicateNotesThreshold = duplicateNotesThreshold;
	}

	/**
	 * Sets how many rests each other. Default is 1.
	 * @param duplicateRestThreshold
	 */
	public void setDuplicateRestThreshold(int duplicateRestThreshold) {
        if (duplicateRestThreshold < 0) {
            throw new IllegalArgumentException("duplicateRestThreshold must be greater than 0");
        }

        this.duplicateRestThreshold = duplicateRestThreshold;
	}

	@Override
	public double calculateErrors(IChromosome melody) {
		double errors = 0.0d;

        Note previous = null;
        int sameNoteCount = 1;
		int restCount = 1;
		for (Gene gene : melody.getGenes()) {
			if (previous != null) {
                Note current = GeneNoteFactory.fromGene(gene);
                if (current.equals(previous)) {
					if (current.isRest()) {
						restCount++;
					} else {
						sameNoteCount++;
					}
				} else {
					sameNoteCount = 1;
					restCount = 1;
				}
                previous = GeneNoteFactory.fromGene(gene);
            } else {
                previous = GeneNoteFactory.fromGene(gene);
            }

			if (sameNoteCount > this.duplicateNotesThreshold) {
				errors += 1;
			}
			if (restCount > this.duplicateRestThreshold) {
				errors += 1;
			}
		}

		// Multipy by 2 to make the parameter slightly more important than default
		return errors * 2;
	}

    @Override
    public void init(JPanel container) {
        container.setLayout(new MigLayout());
        container.add(new JLabel("Maximum consecutive duplicate notes"));
        this.duplicateNotesSpinner = new JSpinner(new SpinnerNumberModel(2, 0, null, 1));
        container.add(this.duplicateNotesSpinner, "wrap");
        container.add(new JLabel("Maximum consecutive duplicate rests"));
        this.duplicateRestSpinner = new JSpinner(new SpinnerNumberModel(2, 0, null, 1));
        container.add(this.duplicateRestSpinner, "wrap");
    }

    public void bind() {
        this.duplicateRestThreshold = (Integer) this.duplicateRestSpinner.getValue();
        this.duplicateNotesThreshold = (Integer) this.duplicateNotesSpinner.getValue();
    }

    public String getName() {
        return "Duplicate notes/rests";
    }

    public String toString() {
        return "[RepeatingNotesStrategy[duplicateNotesThreshold: " + this.duplicateNotesThreshold + ", duplicateRestThreshold: " + this.duplicateRestThreshold + "]]";
    }
}
