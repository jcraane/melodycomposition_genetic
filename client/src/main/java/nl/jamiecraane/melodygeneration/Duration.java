package nl.jamiecraane.melodygeneration;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiEvent;

/**
 * Enum representing a duration.
 */
public enum Duration {
	WHOLE(1,16), HALF(2,8), QUARTER(3,4), EIGTH(4,2), SIXTEENTH(5,1);
	
	private static final Map<Integer, Duration> indexToEnumMapping = new HashMap<Integer, Duration>();

    private final int index;

    private final long ticks;
	
	static {
		for (Duration note : Duration.values()) {
			indexToEnumMapping.put(note.index, note);
		}
	}

	private Duration(int index, long ticks) {
		this.index = index;
		this.ticks = ticks;
	}
	
	/**
	 * @return The MIDI ticks for creating a {@link MidiEvent}.
	 */
	public long getTicks() {
		return this.ticks;
	}

    public int getIndex() {
        return index;
    }

    /**
	 * Returns the Duration belonging to the given index. 
	 * @param index The index to return the enum value for
	 * @return Duration for the given index
	 */
	public static Duration getByIndex(int index) {
		return indexToEnumMapping.get(index);
	}
}
