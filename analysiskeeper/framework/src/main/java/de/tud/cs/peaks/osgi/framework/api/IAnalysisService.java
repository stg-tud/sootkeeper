package de.tud.cs.peaks.osgi.framework.api;


import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.Bundle;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * This is the general Interface for all Analyses
 *
 * @param <Result> The Result type of the analysis,
 * @param <Config> The configuration type of the analysis
 * @author Florian Kuebler
 * @see AbstractAnalysisService
 */
public interface IAnalysisService<Result extends IAnalysisResult, Config extends IAnalysisConfig> {

    /**
     * This is the name that will be displayed to the user in the OSGi Framework
     *
     * @return the Name of the Analysis
     */
    String getName();


    /**
     * Runs the actual analysis with the given configuration
     *
     * @param config          the configuration to run
     * @param previousResults the results of other analyses this analysis depends on
     * @return the result of the Analysis
     */
    Result runAnalysis(Config config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> previousResults);

    /**
     * Performs the analysis with the given config, running all required other analyses
     * This is implemented by {@link AbstractAnalysisService}
     *
     * @param config the given config
     * @return a future which will contain the analysis result
     * @see de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn
     * @see AbstractAnalysisService
     */
    Future<Result> performAnalysis(final Config config);

    /**
     * Converts the config of this analysis to a config of the given analysis this depends on.
     *
     * @param config       the config to convert
     * @param serviceClass the required analysis type, for which the config needs to be converted to
     * @return the converted config
     */
    IAnalysisConfig convertConfig(Config config, Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass);

    /**
     * Creates the config for this analysis from commandline arguments
     *
     * @param config the commandline arguments
     * @return the resulting config
     */
    Config parseConfig(String[] config);

    /**
     * The Bundle which this AnalysisService belongs to.
     *
     * @return the corresponding OSGi Bundle
     */
    Bundle getBundle();
}
