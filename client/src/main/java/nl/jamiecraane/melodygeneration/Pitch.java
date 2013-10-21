package nl.jamiecraane.melodygeneration;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a musical note. There is no audible difference between a C# and Db so we only count the sharps. Note that this
 * is not correct from a theoretical point but sufficient for the purpose of generating melodies.
 */
public enum Pitch {
	// Because we only need to specify a custom toString implementation for some enums we use an anonymous class.
	REST(0), C(1), C_SHARP(2) {
		public String toString() {
			return "C#";
		}
	},
	D(3), D_SHARP(4) {
		public String toString() {
			return "D#";
		}
	},
	E(5), F(6), F_SHARP(7) {
		public String toString() {
			return "F#";
		}
	},
	G(8), G_SHARP(9) {
		public String toString() {
			return "G#";
		}
	},
	A(10), A_SHARP(11) {
		public String toString() {
			return "A#";
		}
	},
	B(12);

	private static final Map<Integer, Pitch> indexToEnumMapping = new HashMap<Integer, Pitch>();

	static {
		for (Pitch pitch : Pitch.values()) {
			indexToEnumMapping.put(pitch.noteNumber, pitch);
		}
	}

	private final int noteNumber;

	private Pitch(int noteNumber) {
		this.noteNumber = noteNumber;
    }

    /**
     * @param octave The octave to get this pitc MIDI note number for.
     * @return The MINI note number
     */
    public int toMidiNoteNumber(int octave) {
		return (this.noteNumber + (12 * octave)) - 1;
	}

	/**
	 * This method is needed to get from the IntegerGene allele value to a specific note. In the future a specific NoteGene
	 * (and ScaleGene and RestGene) can be implemented.
	 * 
	 * @param index The index to return the enum value for
	 * @return Pitch for the given index
	 */
	public static Pitch getByIndex(int index) {
		return indexToEnumMapping.get(index);
	}

    /**
     * @return The first semitone above the current pitch.
     */
    public Pitch getNextSemitone() {
		int nextNote = this.noteNumber + 1;
		if (nextNote > 12)
			nextNote = 1;
		return indexToEnumMapping.get(nextNote);
	}

    /**
     * @return The previous semitone below the current pitch.
     */
    public Pitch getPreviousSemitone() {
		int previousNote = this.noteNumber - 1;
		if (previousNote <= 0)
			previousNote = 12;
		return indexToEnumMapping.get(previousNote);
	}

    
    public int getNoteNumber() {
        return noteNumber;
    }

    /**
	 * @param other
	 * @return The difference in semitones between this and the supplied note.
	 * @throws IllegalStateException When this is {@link Pitch#REST}
	 * @throws IllegalArgumentException When other is {@link Pitch#REST}
	 */
	public int getDifferenceInSemiTones(Pitch other) {
		if (this == REST) {
			throw new IllegalStateException("Cannot calculate the semitones from [" + this + "] to [" + other + "]");
		}
		if (other == REST) {
			throw new IllegalArgumentException("The supplied note may not be a rest");
		}

		return Math.abs(this.noteNumber - other.noteNumber);
	}

    public boolean higherThan(Pitch other) {
		return this.noteNumber > other.noteNumber;
	}
}
