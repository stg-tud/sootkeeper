package de.tud.cs.peaks.osgi.framework.api.annotations;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

import java.lang.annotation.*;

/**
 * With this annotation you must annotate analyses, so we put an annotation on your annotation so you can annotate while you annotate
 *
 * @author Florian Kuebler
 * @author Patrick Müller
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DependsOn {

    /**
     * The value of this annotation
     *
     * @return The value of this annotation
     */
    Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>[] value();
}
