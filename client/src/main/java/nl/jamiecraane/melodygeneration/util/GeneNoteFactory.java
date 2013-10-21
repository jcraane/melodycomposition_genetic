package nl.jamiecraane.melodygeneration.util;

import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Duration;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.IntegerGene;
import org.jgap.Gene;

/**
 * Author: Jamie Craane
 * Date: 2-jun-2009
 * Time: 21:29:28
 */
public final class GeneNoteFactory {
    private GeneNoteFactory() {

    }

    /**
     * Factory method which transforms the given gene in a Note instance
     * @param gene Gene which must be transformed in a Note instance
     * @return Note class transformed from the given gene
     * @throws ClassCastException If the given gene is not an instance of CompositeGene
     */
    public static Note fromGene(Gene gene) {
        CompositeGene noteGene = (CompositeGene) gene;
        IntegerGene pitch = (IntegerGene) noteGene.geneAt(0);
        IntegerGene octave = (IntegerGene) noteGene.geneAt(1);
        IntegerGene duration = (IntegerGene) noteGene.geneAt(2);
        return Note.createNote(Pitch.getByIndex(((Integer) pitch.getAllele())), (Integer) octave.getAllele(), Duration.getByIndex((Integer) duration.getAllele()));
    }
}
