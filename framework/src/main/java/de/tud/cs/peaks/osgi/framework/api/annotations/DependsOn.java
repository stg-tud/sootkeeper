package de.tud.cs.peaks.osgi.framework.api.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

/**
 * 
 * @author Florian Kuebler
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DependsOn {

	/**
	 * 
	 * @return
	 */
	Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>>[] value();
}
