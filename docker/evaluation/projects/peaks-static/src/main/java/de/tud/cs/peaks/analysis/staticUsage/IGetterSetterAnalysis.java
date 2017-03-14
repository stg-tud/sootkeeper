package de.tud.cs.peaks.analysis.staticUsage;

import soot.SootField;

/**
 *
 * @author Florian Kübler
 *
 */
public interface IGetterSetterAnalysis {

	/**
	 * Checks whether fields declaring class has getters or setters for this
	 * field.
	 * 
	 * @param field
	 * @return
	 */
	boolean hasGetterOrSetter(SootField field);

}
