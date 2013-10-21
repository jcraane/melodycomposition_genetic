package nl.jamiecraane.melodygeneration.plugins;

import static org.junit.Assert.*;

import nl.jamiecraane.melodygeneration.Duration;
import nl.jamiecraane.melodygeneration.test.MelodyChromosomeBuilder;
import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Scale;
import org.junit.Test;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 14-sep-2009
 * Time: 14:28:21
 * To change this template use File | Settings | File Templates.
 */
public class ScaleStrategyTest {
    @Test
    public void calculateErrorsOfCMajorScaleWhenMelodyIsInCMajor() throws InvalidConfigurationException {
        IChromosome melody = new MelodyChromosomeBuilder().addNote(Pitch.C, 4, Duration.QUARTER).addNote(Pitch.D, 4, Duration.QUARTER).addNote(Pitch.E, 4, Duration.QUARTER).build();

        ScaleStrategy scaleStrategy = new ScaleStrategy();
        scaleStrategy.setScale(Scale.C_MAJOR);
        double errors = scaleStrategy.calculateErrors(melody);
        assertEquals(0.0D, errors, 0.01);     
    }
}
