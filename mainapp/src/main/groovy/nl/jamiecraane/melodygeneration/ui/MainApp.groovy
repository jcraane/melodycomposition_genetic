package nl.jamiecraane.melodygeneration.ui

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 22-2-11
 * Time: 19:29
 * To change this template use File | Settings | File Templates.
 */
/**
 * Main application.
 */
import groovy.swing.SwingBuilder
import javax.swing.JPanel
import javax.swing.JTabbedPane
import net.miginfocom.swing.MigLayout
import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy
import nl.jamiecraane.melodygeneration.MelodyGenerator
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunctionBuilder
import nl.jamiecraane.melodygeneration.plugin.core.impl.StaticPluginDiscoverer

import nl.jamiecraane.melodygeneration.GeneratedMelody
import nl.jamiecraane.melodygeneration.Note

MelodyGenerator generator;

// Use the StaticPluginDiscoverer when running from within IDE
def plugins = new StaticPluginDiscoverer().getAvailablePlugins()
//def plugins = DynamicPluginDiscoverer.createPluginDiscoverer().getAvailablePlugins()
plugins.each {
    println it
}
def swing = new SwingBuilder()
tabbedPluginPane = swing.tabbedPane(tabPlacement: JTabbedPane.LEFT) {
    plugins.each { MelodyFitnessStrategy plugin ->
        JPanel container = panel(name: plugin.name, layout: new MigLayout()) {
            JPanel configPanel = panel(constraints: 'wrap')
            plugin.initDefault(configPanel)
            JPanel pluginPane = panel(name: plugin.name)
            plugin.init(pluginPane)
        }
    }
}

def generateMelody = {
    MelodyFitnessFunctionBuilder fitnessFunctionBuilder = new MelodyFitnessFunctionBuilder();

    plugins.each { MelodyFitnessStrategy plugin ->
        plugin.bindDefault();
        if (plugin.isEnabled()) {
            plugin.bind()
            fitnessFunctionBuilder.addStrategy(plugin)
        }
    }

    try {
        generator = new MelodyGenerator();
        MelodyFitnessFunction melodyFitnessFunction = fitnessFunctionBuilder.build();
        println melodyFitnessFunction;
        generator.setProgressBar(myProgressBar)
        generator.generateMelody(fitnessFunctionBuilder.build(), numberOfNotesSpinner.value, Integer.valueOf(numberOfEvolutionsField.text))
    } catch (IllegalStateException e) {
        melodyText.text = e.message
    }
}

generalPanel = swing.panel(layout: new MigLayout()) {
    label(text: "Number of notes")
	numberOfNotesSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 24)
    label(text: "Number of evolutions")
	numberOfEvolutionsField = textField(text: 250)
    label(text: "Path to save melodies (i.e c:/melodies)")
    melodyDir = textField("c:/melodies")
}

generatedMelodyPanel = swing.panel(layout: new MigLayout()) {
    scrollPane() {
        melodyText = textArea(rows: 20, columns: 80)
    }
}

def printMelody = {
    GeneratedMelody generatedMelody = generator.getGeneratedMelody()
    System.out.println("melody = " + generatedMelody)
    if (generatedMelody != null) {
        StringBuilder output = new StringBuilder()
        output << "generatedMelody.getFitnessValue() = " + generatedMelody.getFitnessValue() + "\r\n"
            for (Note note : generatedMelody.getMelody()) {
                output << note.getPitch();
                output << note.getOctave() + "-";
                output << note.getDuration();
                output << ":\r\n";
            }
        melodyText.text = output.toString()
    }
}

actionPanel = swing.panel(layout: new MigLayout()) {
    generateButton = button(text: "Generate",
            actionPerformed: {
                myProgressBar.maximum = Integer.parseInt(numberOfEvolutionsField.text) - 1
                generateButton.enabled = false
                saveButton.enabled = false
                playButton.enabled = false
                doOutside {
                    generateMelody()

                    doLater {
                        saveButton.enabled = true
                        playButton.enabled = true
                        generateButton.enabled = true
                        printMelody()
                    }
                }
            })
    saveButton = button(text: "Save", enabled: false, actionPerformed: {
        doOutside {
            generator.save(melodyDir.text)
        }
    }
    )
    playButton = button(text: "Play", enabled: false, actionPerformed: {
        saveButton.enabled = false
        playButton.enabled = false
        generateButton.enabled = false
        doOutside {
            generator.play()

            doLater {
                saveButton.enabled = true
                playButton.enabled = true
                generateButton.enabled = true
            }
        }
    }
    )
    myProgressBar = progressBar(minimum: 0, value: 0)
}

frame = swing.frame(title: "app", size: [1000, 800], windowClosing: {System.exit(0)}, layout: new MigLayout()) {
    widget(tabbedPluginPane, constraints: 'wrap')
    panel(border: titledBorder(title: "General settings"), constraints: 'wrap') {
        widget(generalPanel);
    }
    panel(border: titledBorder(title: "Actions"), constraints: 'wrap') {
        widget(actionPanel);
    }
    panel(border: titledBorder(title: "Generated Melody")) {
        widget(generatedMelodyPanel);
    }
}
frame.show()