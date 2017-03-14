package de.tud.cs.peaks.toolbox.tags;

import soot.tagkit.AttributeValueException;

public class TransitiveNativeRatingTag extends PeaksTag {
	
	private float rating;
	
	public TransitiveNativeRatingTag(float rating){
		this.rating = rating;
	}
	
	public float getRating(){
		return this.rating;
	}
		
	@Override
	public String getName() {
		return transitiveNativeRatingTag;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		throw new RuntimeException("Native Usage has no value for bytecode");
	}

}
