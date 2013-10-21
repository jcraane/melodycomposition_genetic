package nl.jamiecraane.melodygeneration;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.Scale;

import org.jgap.IChromosome;

import javax.swing.*;

/**
 * Convenience class which subclasses can extend when implementing the MelodyFitnessStrategy interface.
 * This class provides a default implementation for the setScale method.
 * This class provides an empoty implementation for the configure method which subclasses can override when needed.
 */
public abstract class AbstractMelodyFitnessStrategy implements MelodyFitnessStrategy {
    protected Scale scale;
    private JCheckBox enabledCheckbox;
    private boolean enabled = true;

    final public void setScale(Scale scale) {
        this.scale = scale;
    }

	abstract public double calculateErrors(IChromosome melody);

    abstract public void init(JPanel container);

    /**
     * Called by the UI to initialize the UI. Places an enabled checkbox on the specified container.
     * @param container
     */
    final public void initDefault(JPanel container) {
        enabledCheckbox = new JCheckBox("Enabled");
        enabledCheckbox.setSelected(true);
        container.add(enabledCheckbox);
    }

    final public void bindDefault() {
        this.enabled = this.enabledCheckbox.isSelected();
    }

    /**
     * Empty implementation of the configure method.
     * Subclasses can implement this method if needed
     *
     * @see MelodyFitnessStrategy#bind()
     */
    public void bind() {
        // Subclasses can implement this method when needed
    }

    public boolean isEnabled() {
        return enabled;
    }
}
