package nl.jamiecraane.melodygeneration.ui

import javax.sound.midi.MidiSystem

class MidiUtils {
    public Map getReceivers() {
        def receivers = [:]
        MidiSystem.midiDeviceInfo.each {
            receivers[it.name]  = MidiSystem.getMidiDevice(it)
        }
        return receivers
    }
}