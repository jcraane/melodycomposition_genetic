package nl.jamiecraane.melodygeneration;

import nl.jamiecraane.melodygeneration.Duration;
import nl.jamiecraane.melodygeneration.Note;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 16-sep-2009
 * Time: 8:31:53
 * To change this template use File | Settings | File Templates.
 */
public class GeneratedMelody {
    private double fitnessValue;
    private List<Note> melody;
    private List<Duration> noteDurations;

    public GeneratedMelody(double fitnessValue, List<Note> melody, List<Duration> noteDurations) {
        this.fitnessValue = fitnessValue;
        this.melody = melody;
        this.noteDurations = noteDurations;
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public List<Note> getMelody() {
        return melody;
    }

    public List<Duration> getNoteDurations() {
        return noteDurations;
    }
}
