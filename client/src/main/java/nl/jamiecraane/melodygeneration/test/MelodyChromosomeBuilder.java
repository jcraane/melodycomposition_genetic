package nl.jamiecraane.melodygeneration.test;

import org.jgap.*;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.DefaultConfiguration;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.Duration;

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 14-sep-2009
 * Time: 8:59:51
 * To change this template use File | Settings | File Templates.
 *
 * Builder class for easy construction of a sequence of notes to a chromosome representing the given melody.
 */
public class MelodyChromosomeBuilder {
    private List<Gene> melody = new ArrayList<Gene>();
    private Configuration gaConf = new DefaultConfiguration();

    public MelodyChromosomeBuilder() throws InvalidConfigurationException {
        Configuration.reset();
         Chromosome melodyChromosome = new Chromosome(gaConf);
         Gene[] genes = new Gene[melody.size()];
    }

    /**
     * Adds a note to the mleody build by this builder.
     * @param pitch The pitch for the note
     * @param octave The octave in which the pitch (note) resides
     * @param duration The duration of the note
     * @throws InvalidConfigurationException
     */
    public MelodyChromosomeBuilder addNote(Pitch pitch, int octave, Duration duration) throws InvalidConfigurationException {
        Gene pitchGene = new IntegerGene(gaConf);
        pitchGene.setAllele(pitch.getNoteNumber());
        Gene octaveGene = new IntegerGene(gaConf);
        octaveGene.setAllele(octave);
        Gene durationGene = new IntegerGene(gaConf);
        durationGene.setAllele(duration.getIndex());

        CompositeGene compGene = new CompositeGene(gaConf);
        compGene.addGene(pitchGene);
        compGene.addGene(octaveGene);
        compGene.addGene(durationGene);
        melody.add(compGene);

        return this;
    }

    /**
     * Before building the melody chromosome at least on note should have been
     * added to this builder.
     * @return A fully constructed melody in the for of a Chromosome.
     */
    public IChromosome build() throws InvalidConfigurationException {
        Gene[] genes = new Gene[melody.size()];
        Chromosome melodyChromosome = new Chromosome(gaConf);

        if (melody.size() == 0) {
            throw new IllegalStateException("There should be at least one note added with the addNote() method before building the chromosome");
        }

        for (int i = 0; i < genes.length; i++) {
            genes[i] = melody.get(i);
        }

        melodyChromosome.setGenes(genes);
        return melodyChromosome;
    }
}
