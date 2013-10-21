package nl.jamiecraane.melodygeneration.fitnessfunction;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import org.apache.log4j.Logger;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import java.util.ArrayList;
import java.util.List;

/**
 * Fitness function which calculates the fitness of a given melody. Different strategies to calculate the fitness
 * function may be plugged in. These strategies are all used to calculate the fitness of a given melody. Because the
 * fitness value is based on error-count, the lower the fitness value the better the melody.
 */
@SuppressWarnings({"serial", "ConstantConditions"})
public final class MelodyFitnessFunction extends FitnessFunction {
	private static final Logger LOG = Logger.getLogger(MelodyFitnessFunction.class);
	private final List<MelodyFitnessStrategy> fitnessStrategies = new ArrayList<MelodyFitnessStrategy>();

	void addFitnessStrategy(MelodyFitnessStrategy strategy) {
		this.fitnessStrategies.add(strategy);
	}

	@Override
	protected double evaluate(IChromosome melody) {
        if (this.fitnessStrategies.isEmpty()) {
            throw new IllegalStateException("At least one strategy must be defined for measuring the fitness of the melody");
        }

        double errors = 0.0D;
		StringBuilder logBuilder = null;
		if (LOG.isDebugEnabled()) {
			logBuilder = new StringBuilder();
		}

		for (MelodyFitnessStrategy strategy : this.fitnessStrategies) {
			double currentErrors = strategy.calculateErrors(melody);
			if (LOG.isDebugEnabled()) {
				String strategyName = strategy.getClass().getName();
                logBuilder.append(strategyName.substring(strategyName.lastIndexOf(".") + 1, strategyName.length())).append("[").append(currentErrors).append("]:");				
			}
			errors += currentErrors;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(logBuilder.toString());
		}
		return errors * errors;
	}

    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        for (MelodyFitnessStrategy strategy : this.fitnessStrategies) {
            toStringBuilder.append(strategy);
        }
        return toStringBuilder.toString();
    }
}
