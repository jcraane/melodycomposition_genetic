package nl.jamiecraane.melodygeneration.fitnessfunction;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.Scale;
import nl.jamiecraane.melodygeneration.plugins.IntervalStrategy;
import nl.jamiecraane.melodygeneration.plugins.ParallelIntervalStrategy;
import nl.jamiecraane.melodygeneration.plugins.ScaleStrategy;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for a MelodyFitnessFunction.
 */
public final class MelodyFitnessFunctionBuilder {
    private static final Logger LOG = Logger.getLogger(MelodyFitnessFunctionBuilder.class);
    private Scale scale;
    private final Map<Class, MelodyFitnessStrategy> strategies = new HashMap<Class, MelodyFitnessStrategy>();

    public MelodyFitnessFunctionBuilder addStrategy(MelodyFitnessStrategy melodyFitnessStrategy) {
        if (melodyFitnessStrategy == null) {
            throw new IllegalArgumentException("melodyFitnessStrategy may nog be null");
        }

        if (melodyFitnessStrategy instanceof ScaleStrategy) {
            ScaleStrategy scaleStrategy = (ScaleStrategy) melodyFitnessStrategy;
            this.scale = Scale.fromString(scaleStrategy.getSelectedScale());
        }

        if (this.strategies.put(melodyFitnessStrategy.getClass(), melodyFitnessStrategy) != null) {
            LOG.info(melodyFitnessStrategy.getClass().getName() + " already exists as a strategy. The new strategy is used in place of the old one.");            
        }

        return this;
    }
   
    public MelodyFitnessFunction build() {
        if (this.scale == null) {
            for (MelodyFitnessStrategy strategy : this.strategies.values()) {
                if (strategy instanceof ParallelIntervalStrategy ||
                    strategy instanceof IntervalStrategy) {
                    throw new IllegalStateException("When using the Major/Perfect intervals and/or Parallel intervals the scale must be enabled.");
                }
            }
        }

        if (this.strategies.isEmpty()) {
            throw new IllegalStateException("At least one strategy must be added to compute the fitness value of a melody");
        }

        MelodyFitnessFunction melodyFitnessFunction = new MelodyFitnessFunction();
        for (MelodyFitnessStrategy strategy : this.strategies.values()) {
            strategy.setScale(this.scale);
            melodyFitnessFunction.addFitnessStrategy(strategy);
        }

        return melodyFitnessFunction;
    }
}

