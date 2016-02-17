package de.tud.cs.peaks.osgi.framework.api;

import java.util.Map;
import java.util.concurrent.Future;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

/**
 * 
 * @author Florian Kuebler
 *
 * @param <Result>
 * @param <Config>
 */
public interface IAnalysisService<Result extends IAnalysisResult, Config extends IAnalysisConfig> {

	/**
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 
	 * @return
     */
	Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> getApiClass();

	/**
	 * 
	 * @param config
	 * @param previousResults
	 * @return
	 */
	Result runAnalysis(Config config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> previousResults);
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	Future<Result> performAnalysis(final Config config);

	/**
	 * 
	 * @param config
	 * @param serviceClass
	 * @return
	 */
	IAnalysisConfig convertConfig(IAnalysisConfig config, Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass);

	/**
	 * 
	 * @param config
	 * @return
	 */
	Config parseConfig(Object config);
}
