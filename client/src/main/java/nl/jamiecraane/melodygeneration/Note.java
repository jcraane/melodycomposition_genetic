package nl.jamiecraane.melodygeneration;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable class representing a musical note containing a pitch and an octave in which the note is played.
 */
public final class Note {
    public static final int MIN_OCTAVE = 0;
    public static final int MAX_OCTAVE = 10;
    private final Pitch pitch;
    private final int octave;
    private final Duration duration;

    private Note(Pitch pitch, int octave, Duration duration) {
        this.pitch = pitch;
        this.octave = octave;
        this.duration = duration;
    }

    /**
     * Returns note instances based on the FlyWeight pattern, see GOF.
     * @param pitch pitch used to create the note
     * @param octave octave used for the giuven note
     * @return Note instance for the given pitch and octave
     */
    public static Note createNote(Pitch pitch, int octave, Duration duration) {
        if (octave < MIN_OCTAVE || octave > MAX_OCTAVE) {
            throw new IllegalArgumentException("Octave must be between " + MIN_OCTAVE + " and " + MAX_OCTAVE +
            ".");
        }
        // Since all notes are created in a static initialiser, no need to lazy-load and lock access to the cache
        return new Note(pitch, octave, duration);
    }

    /**
     * @return The MIDI number representing this note, from 0..127.
     */
    public int toMidiNoteNumber() {
        return this.pitch.toMidiNoteNumber(this.octave);
    }

    /**
     * @return True if this note represents a Pitch.REST, false otherwise
     */
    public boolean isRest() {
        return Pitch.REST == this.pitch;
    }

    /**
     * @return The first semitone above this note.
     */
    public Note getNextSemitone() {
        if (isUpOneOctave()) {
            if (this.octave == MAX_OCTAVE) {
                throw new IllegalStateException("Maximum octave reached");
            }
            return Note.createNote(this.pitch.getNextSemitone(), this.octave + 1, this.duration);
//            return noteCache.get(new Key(this.pitch.getNextSemitone(), this.octave+1));
        } else {
            return Note.createNote(this.pitch.getNextSemitone(), this.octave, this.duration);
//            return noteCache.get(new Key(this.pitch.getNextSemitone(), this.octave));
        }
	}

    /**
     * @return True if the current note is a Pitch.B
     */
    private boolean isUpOneOctave() {
        return Pitch.B == this.pitch;
    }

    /**
     * @return The previous semitone below this note.
     */
    public Note getPreviousSemitone() {
        if (isDownOneOctave()) {
            if (this.octave == MAX_OCTAVE) {
                throw new IllegalStateException("Minimum octave reached");
            }

            return Note.createNote(this.pitch.getPreviousSemitone(), this.octave - 1, this.duration);
//            return noteCache.get(new Key(this.pitch.getPreviousSemitone(), this.octave-1));
        } else {
            return Note.createNote(this.pitch.getPreviousSemitone(), this.octave, this.duration);
//            return noteCache.get(new Key(this.pitch.getPreviousSemitone(), this.octave));
        }
	}

    /**
     * @return True if the current note is a Pitch.C
     */
    private boolean isDownOneOctave() {
        return Pitch.C == this.pitch;
    }

    /**
     * @param other Note to compare this note pitch to.
     * @return True if the supplied note is higher in pitch than this note.
     */
    public boolean higherThan(Note other) {
        return this.pitch.higherThan(other.pitch) &&
                this.octave > other.octave;
    }

    /**
     * @param other Note to calculate the difference in semitones compared to this note.
     * @return The number of semitones between the supplied note and this note.
     */
    public int getDifferenceInSemiTones(Note other) {
        int differenceInSemiTones = this.pitch.getDifferenceInSemiTones(other.pitch);
        differenceInSemiTones += Math.abs(this.octave - other.octave) * 12;
        return differenceInSemiTones;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Note: [pitch: ").append(this.pitch).append("]");
        builder.append(",[octave: ").append(this.octave).append("]]");
        return builder.toString();
    }

    public Pitch getPitch() {
        return pitch;
    }

    public int getOctave() {
        return octave;
    }

    public Duration getDuration() {
        return duration;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        return octave == note.octave && pitch == note.pitch;

        }

    public int hashCode() {
        int result;
        result = (pitch != null ? pitch.hashCode() : 0);
        result = 31 * result + octave;
        return result;
    }

    /**
     * Immutable key class.
     */
    private static class Key {
        private Pitch pitch;
        private int octave;

        public Key(Pitch pitch, int octave) {
            this.pitch = pitch;
            this.octave = octave;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return octave == key.octave && pitch == key.pitch;

            }

        public int hashCode() {
            int result;
            result = (pitch != null ? pitch.hashCode() : 0);
            result = 31 * result + octave;
            return result;
        }
    }
}

