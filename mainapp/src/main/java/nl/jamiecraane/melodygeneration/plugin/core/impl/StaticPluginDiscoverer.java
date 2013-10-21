package nl.jamiecraane.melodygeneration.plugin.core.impl;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.plugin.core.PluginDiscoverer;
import nl.jamiecraane.melodygeneration.plugins.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the PluginDiscoverer interface which statically defines all available plugins.
 */
public class StaticPluginDiscoverer implements PluginDiscoverer {
    public StaticPluginDiscoverer() {
    }

    public List<MelodyFitnessStrategy> getAvailablePlugins() {
        List<MelodyFitnessStrategy> plugins = new ArrayList<MelodyFitnessStrategy>();

        plugins.add(new IntervalStrategy());
        plugins.add(new GlobalPitchDistributionStrategy());
        plugins.add(new ParallelIntervalStrategy());
        plugins.add(new ProportionRestAndNotesStrategy());
        plugins.add(new RepeatingNotesStrategy());
        plugins.add(new ScaleStrategy());

        return plugins;
    }
}
