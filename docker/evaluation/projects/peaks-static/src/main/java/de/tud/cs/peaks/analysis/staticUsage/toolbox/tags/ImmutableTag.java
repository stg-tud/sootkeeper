package de.tud.cs.peaks.analysis.staticUsage.toolbox.tags;

import de.tud.cs.peaks.toolbox.tags.PeaksTag;
import soot.tagkit.AttributeValueException;

/**
 * User: Patrick Müller
 * Date: 02.07.13
 * Time: 15:41
 */
public class ImmutableTag extends PeaksTag {

    private final boolean isImmutable;

    public ImmutableTag(boolean isImmutable) {
        this.isImmutable = isImmutable;
    }

    @Override
    public String getName() {
        return immutableTag;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        throw new RuntimeException("Immutable has no value for bytecode");
    }

    public boolean isImmutable() {
        return isImmutable;
    }

}
