package de.tud.cs.peaks.toolbox.tags;

import soot.tagkit.Tag;

/**
 * User: Patrick Müller Date: 02.07.13 Time: 15:28
 */
public abstract class PeaksTag implements Tag {

	public static final String immutableTag = "Immutability";
	public static final String sideEffectTag = "SideEffects";
	public static final String transitiveNativeRatingTag = "TransitiveNativeRating";
}
