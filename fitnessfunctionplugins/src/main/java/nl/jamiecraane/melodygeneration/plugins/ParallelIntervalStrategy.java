package nl.jamiecraane.melodygeneration.plugins;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Scale;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;
import nl.jamiecraane.melodygeneration.Scale.Interval;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * When two notes are a certain distance apart and move the same interval at the same time in the same direction, they are called parallel intervals. 
 * Some parallel intervals sound good, like third and sixths. This strategy makes use of that knowledge and calculates the fitness of a melody
 * for the existence of good sounding parallel intervals.
 * 
 * TODO Implement the logic to minimize the intervals in a melody that sound bad (like fifths and octaves).
 */
public final class ParallelIntervalStrategy extends AbstractMelodyFitnessStrategy {
	private int numberOfParallelIntervalsThatSoundGood = 0;
	
	private int numberOfParallelIntervalsThatSoundBad = 0;

	private EnumSet<Interval> intervalsThatSoundGoodInParallel = EnumSet.of(Interval.THIRD, Interval.SIXTH);
	
	private EnumSet<Interval> intervalsThatSoundBadInParallel = EnumSet.of(Interval.FIFTH);	

	private Scale scale = Scale.C_MAJOR;

    private JSpinner parallelIntervalSpinner;

    /**
	 * Sets the number of parallel intervals that sound good the melody should contain. Default is 0.
	 * @param numberOfParallelIntervalsThatSoundGood
	 */
	public void setNumberOfParallelIntervalsThatSoundGood(int numberOfParallelIntervalsThatSoundGood) {
		this.numberOfParallelIntervalsThatSoundGood = numberOfParallelIntervalsThatSoundGood;
	}

	@Override
	public double calculateErrors(IChromosome melody) {
		double errors = 1.0D;

		int numberOfParallelIntervals = 0;
		List<NoteInterval> intervals = new ArrayList<NoteInterval>();
		Pitch previousPitch = null;
		for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene(gene);
            Pitch currentPitch = note.getPitch();
            if (previousPitch != null) {
                intervals.add(new NoteInterval(super.scale.getInterval(previousPitch, currentPitch), previousPitch));
            }

            previousPitch = currentPitch;
        }

		numberOfParallelIntervals = this.calculateNumberOfParallelIntervals(intervals);
        errors += Math.abs(this.numberOfParallelIntervalsThatSoundGood - numberOfParallelIntervals);

		if (Double.compare(errors, 1.0D) == 0) {
			errors = 0.0D;
		}

		return errors * errors;
	}

    @Override
    public void init(JPanel container) {
        container.setLayout(new MigLayout());
        container.add(new JLabel("Number of good sounding parallel intervals"));
        parallelIntervalSpinner = new JSpinner(new SpinnerNumberModel(2,0,100,1));
        container.add(parallelIntervalSpinner);
//        label(text: "Number of good sounding parallel intervals")
//	    parallelIntervalSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 2)
    }

    public void bind() {
        this.numberOfParallelIntervalsThatSoundGood = (Integer) this.parallelIntervalSpinner.getValue();
    }

    public String getName() {
        return "Parallel intervals";
    }

    private int calculateNumberOfParallelIntervals(List<NoteInterval> intervals) {
		int numberOfParallelIntervals = 0;
		NoteInterval[] intervalArray = intervals.toArray(new NoteInterval[intervals.size()]);
		NoteInterval previousInterval = null;
		for (int i = 0; i < intervalArray.length; i++) {
			if (previousInterval != null) {
				int nextIntervalindex = getIndexOfNextInterval(i);
				if (nextIntervalindex < intervalArray.length) {
					NoteInterval nextInterval = intervalArray[nextIntervalindex];
					if (parallelInterval(previousInterval, nextInterval)) {
						numberOfParallelIntervals++;
						previousInterval = intervalArray[nextIntervalindex];
						i = nextIntervalindex;
					} else {
						previousInterval = intervalArray[i];
					}
				}
			} else {
				previousInterval = intervalArray[i];
			}
		}

		return numberOfParallelIntervals;
	}

	private int getIndexOfNextInterval(int i) {
		return i + 1;
	}

	private boolean parallelInterval(NoteInterval previousInterval, NoteInterval nextInterval) {
		return intervalsThatSoundGoodInParallel.contains(previousInterval.getInterval()) && 
			   previousInterval.getInterval() == nextInterval.getInterval() &&
			   nextInterval.getStartNoteOfInterval().higherThan(previousInterval.getStartNoteOfInterval());
	}
	
	private static class NoteInterval {
		private Interval interval;
		private Pitch startPitchOfInterval;
		
		public NoteInterval(Interval interval, Pitch startPitchOfInterval) {
			this.interval = interval;
			this.startPitchOfInterval = startPitchOfInterval;
		}

		public Interval getInterval() {
			return interval;
		}

		public Pitch getStartNoteOfInterval() {
			return startPitchOfInterval;
		}
	}

    public String toString() {
        return "[ParallelIntervalStrategy[numberOfParallelIntervalsThatSoundGood: " + this.numberOfParallelIntervalsThatSoundGood + "]]";
    }
}
