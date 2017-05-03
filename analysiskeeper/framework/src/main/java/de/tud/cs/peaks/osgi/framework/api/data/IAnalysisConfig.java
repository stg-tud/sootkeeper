package de.tud.cs.peaks.osgi.framework.api.data;

/**
 * This is the abstract class for analysis configurations.
 * This actually overrides hashCode and equals, so that all inheriting classes need to implement them.
 *
 * @author Florian Kuebler, Patrick Mueller
 */
public abstract class IAnalysisConfig {
    /**
     * {@inheritDoc}
     */
    public abstract boolean equals(Object other);

    /**
     * {@inheritDoc}
     */
    public abstract int hashCode();
}
