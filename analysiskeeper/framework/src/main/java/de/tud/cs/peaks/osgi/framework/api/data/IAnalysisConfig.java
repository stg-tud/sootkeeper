package de.tud.cs.peaks.osgi.framework.api.data;

/**
 * This is the abstract Class for Analysis Configs.
 * This actually overrides hashCode and equals, so that all inheriting classes need to implement them
 *
 * @author Florian Kuebler
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
