package de.tud.cs.peaks.analysis.staticUsage.toolbox.tags;

import de.tud.cs.peaks.toolbox.tags.PeaksTag;
import soot.tagkit.AttributeValueException;

/**
 * User: Patrick Müller Date: 02.07.13 Time: 15:43
 */
public class SideEffectTag extends PeaksTag {
	private final boolean isSideEffectFree;

	public SideEffectTag(boolean isSideEffectFree) {
		this.isSideEffectFree = isSideEffectFree;
	}

	@Override
	public String getName() {
		return sideEffectTag;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		throw new RuntimeException("Side Effect Free has no value for bytecode");
	}

	public boolean isSideEffectFree() {
		return isSideEffectFree;
	}
}
