package nl.jamiecraane.melodygeneration.plugin.core;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 3-jul-2009
 * Time: 10:21:27
 * To change this template use File | Settings | File Templates.
 */
public interface PluginDiscoverer {
    List<MelodyFitnessStrategy> getAvailablePlugins() ;
}
