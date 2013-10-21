package nl.jamiecraane.melodygeneration;

import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;
import nl.jamiecraane.melodygeneration.util.MidiDataOutputStream;
import nl.jamiecraane.melodygeneration.util.MidiFileWriter;
import nl.jamiecraane.melodygeneration.util.MidiGeneHelper;
import org.apache.log4j.Logger;
import org.jgap.*;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MelodyGenerator {
    private static final Logger LOG = Logger.getLogger(MelodyGenerator.class);
    private static final int MINIMUM_OCTAVE = 4;
    private static final int MAXIMUM_OCTAVE = 7;
    private JProgressBar progressBar;
    private IChromosome fittestChromosome;
    private GeneratedMelody generatedMelody;

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public MelodyGenerator() {
    }

    /**
     * Called by the UI.
     */
    public void generateMelody(MelodyFitnessFunction melodyFitnessFunction, int numberOfNotes, int evolutions) throws Exception {
        Genotype genotype = this.setupGenoType(melodyFitnessFunction, numberOfNotes);
        this.evolve(genotype, evolutions);
    }

    private Genotype setupGenoType(MelodyFitnessFunction melodyFitnessFunction, int numberOfNotes) throws Exception {
        Configuration.reset();
        Configuration gaConf = new DefaultConfiguration();
        Configuration.resetProperty(Configuration.PROPERTY_FITEVAL_INST);
        gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());

        gaConf.setPreservFittestIndividual(true);
        gaConf.setKeepPopulationSizeConstant(false);

        gaConf.setPopulationSize(40);

        CompositeGene gene = new CompositeGene(gaConf);
        // Add the pitch gene
        gene.addGene(new IntegerGene(gaConf, 0, 12), false);
        // Add the octave gene
        gene.addGene(new IntegerGene(gaConf, MINIMUM_OCTAVE, MAXIMUM_OCTAVE), false);
        // Add the length (from 3 - 5 is from quarter to sixteenth)
        // At the moment only quarter and eight notes are used
        gene.addGene(new IntegerGene(gaConf, 3, 4), false);

        // A size of 16 represent 16 notes
        IChromosome sampleChromosome = new Chromosome(gaConf, gene, numberOfNotes);
        gaConf.setSampleChromosome(sampleChromosome);

        gaConf.setFitnessFunction(melodyFitnessFunction);

        return Genotype.randomInitialGenotype(gaConf);
    }

    public void play() throws InvalidMidiDataException, MidiUnavailableException {
        Sequence sequence = generateMidiSequence();
        this.playSequence(sequence);
    }

    public void save(String path) {
        Sequence sequence = null;
        try {
            sequence = generateMidiSequence();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.writeSequenceToMidiFile(sequence, path);
    }

    private Sequence generateMidiSequence() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 4);
        Track track = sequence.createTrack();

        long ticks = 0;
        for (Gene gene : this.fittestChromosome.getGenes()) {
            CompositeGene note = (CompositeGene) gene;
            Duration duration = Duration.getByIndex((Integer) note.geneAt(2).getAllele());
            ticks += duration.getTicks();
            track.add(new MidiEvent(MidiGeneHelper.toMidiMessage(note), ticks));
            track.add(new MidiEvent(MidiGeneHelper.noteOffMidiMessage(), 0));
        }
        // Because the last note is not played for some reason, we add it again as a workaround
        this.addLastNote(this.fittestChromosome, track, ticks);
        return sequence;
    }


    private void evolve(Genotype genotype, int evolutions) {
        for (int i = 0; i < evolutions; i++) {
            this.progressBar.setValue(i);
            genotype.evolve();
        }
        this.fittestChromosome = genotype.getFittestChromosome();
        this.createMelodyFromChromosome(fittestChromosome);
        this.printSolution();
//        try {
//            this.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public GeneratedMelody getGeneratedMelody() {
        return generatedMelody;
    }

    private void printSolution() {
        System.out.println("generatedMelody.getFitnessValue() = " + generatedMelody.getFitnessValue());
        System.out.print("[");
        for (Note note : generatedMelody.getMelody()) {
            System.out.print(note.getPitch());
            System.out.print(note.getOctave() + "-");
            System.out.print(note.getDuration());
            System.out.print(":");
        }
        System.out.println("]");
    }

    private void createMelodyFromChromosome(IChromosome chromosome) {
        List<Note> notes = new ArrayList<Note>();
        List<Duration> noteDurations = new ArrayList<Duration>();
        for (Gene gene : chromosome.getGenes()) {
            notes.add(GeneNoteFactory.fromGene(gene));
        }

        this.generatedMelody = new GeneratedMelody(chromosome.getFitnessValue(), notes, noteDurations);
    }

    private void playSequence(Sequence sequence) throws InvalidMidiDataException, MidiUnavailableException {
        Sequencer sequencer = null;

        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.getTransmitter().setReceiver(MidiSystem.getReceiver());
            sequencer.setSequence(sequence);
            sequencer.start();
            while (true) {
                if (sequencer.isRunning()) {
                    try {
                        Thread.sleep(1000); // Check every second
                    } catch (InterruptedException ignore) {
                        break;
                    }
                } else {
                    break;
                }
            }
        } finally {
            if (sequencer != null) {
                // Close the MidiDevice & free resources
                sequencer.stop();
                sequencer.close();
            }
        }
    }

    private void addLastNote(IChromosome chromosome, Track track, long ticks) throws InvalidMidiDataException {
        CompositeGene note = (CompositeGene) chromosome.getGene(chromosome.getGenes().length - 1);
        Duration duration = Duration.getByIndex((Integer) note.geneAt(2).getAllele());
        ticks += duration.getTicks();
        track.add(new MidiEvent(MidiGeneHelper.toMidiMessage(note), ticks));
        track.add(new MidiEvent(MidiGeneHelper.noteOffMidiMessage(), 0));
    }

    private void writeSequenceToMidiFile(Sequence sequence, String path) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(path + "/generated-melody-" + System.currentTimeMillis() + ".mid"));
            MidiDataOutputStream dos = new MidiDataOutputStream(os);
            MidiFileWriter midiFileWriter = new MidiFileWriter();
            midiFileWriter.write(sequence, 0, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LOG.error("Unable to close OutputStream", e);
            }
        }
    }
}
