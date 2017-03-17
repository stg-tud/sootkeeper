package de.tu_darmstadt.stg.sootkeeper.flowdroid;

import de.tu_darmstadt.stg.sootkeeper.flowdroid.base.FlowDroidBaseAnalysis;
import de.tu_darmstadt.stg.sootkeeper.flowdroid.base.FlowDroidConfig;
import de.tu_darmstadt.stg.sootkeeper.flowdroid.base.IntermediateResult;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.source.AndroidSourceSinkManager;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Stream;


@SuppressWarnings("Duplicates")
public class FlowDroidAnalysisService extends AbstractAnalysisService<FlowDroidResult, FlowDroidConfig> {
    private static final String NAME = "flowdroid";

    protected FlowDroidAnalysisService(BundleContext context) throws IllegalClassFormatException, IllegalStateException {
        super(context);
    }

    private static boolean validateAdditionalOptions(FlowDroidConfig flowDroidConfig) {
        if (flowDroidConfig.getTimeout() > 0 && flowDroidConfig.getRepeatCount() > 0) {
            return false;
        }
        InfoflowAndroidConfiguration config = flowDroidConfig.getConfig();
        if (!config.getFlowSensitiveAliasing()
                && config.getCallgraphAlgorithm() != InfoflowConfiguration.CallgraphAlgorithm.OnDemand
                && config.getCallgraphAlgorithm() != InfoflowConfiguration.CallgraphAlgorithm.AutomaticSelection) {
            System.err.println("Flow-insensitive aliasing can only be configured for callgraph "
                    + "algorithms that support this choice.");
            return false;
        }
        return true;
    }

    /**
     * Parses the optional command-line arguments
     *
     * @param args            The array of arguments to parse
     * @param flowDroidConfig
     * @return True if all arguments are valid and could be parsed, otherwise
     * false
     */
    private static boolean parseAdditionalOptions(String[] args, FlowDroidConfig flowDroidConfig) {
        InfoflowAndroidConfiguration config = flowDroidConfig.getConfig();
        int i = 2;
        while (i < args.length) {
            if (args[i].equalsIgnoreCase("--timeout")) {
                System.err.println("This version of flowdroid does not support timeout");
                return false;
            } else if (args[i].equalsIgnoreCase("--systimeout")) {
                System.err.println("This version of flowdroid does not support systimeout");
                return false;
            } else if (args[i].equalsIgnoreCase("--singleflow")) {
                config.setStopAfterFirstFlow(true);
                i++;
            } else if (args[i].equalsIgnoreCase("--implicit")) {
                config.setEnableImplicitFlows(true);
                i++;
            } else if (args[i].equalsIgnoreCase("--nostatic")) {
                config.setEnableStaticFieldTracking(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--aplength")) {
                InfoflowAndroidConfiguration.setAccessPathLength(Integer.valueOf(args[i + 1]));
                i += 2;
            } else if (args[i].equalsIgnoreCase("--cgalgo")) {
                String algo = args[i + 1];
                if (algo.equalsIgnoreCase("AUTO"))
                    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.AutomaticSelection);
                else if (algo.equalsIgnoreCase("CHA"))
                    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
                else if (algo.equalsIgnoreCase("VTA"))
                    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.VTA);
                else if (algo.equalsIgnoreCase("RTA"))
                    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.RTA);
                else if (algo.equalsIgnoreCase("SPARK"))
                    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
                else if (algo.equalsIgnoreCase("GEOM"))
                    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.GEOM);
                else {
                    System.err.println("Invalid callgraph algorithm");
                    return false;
                }
                i += 2;
            } else if (args[i].equalsIgnoreCase("--nocallbacks")) {
                config.setEnableCallbacks(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--noexceptions")) {
                config.setEnableExceptionTracking(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--layoutmode")) {
                String algo = args[i + 1];
                if (algo.equalsIgnoreCase("NONE"))
                    config.setLayoutMatchingMode(AndroidSourceSinkManager.LayoutMatchingMode.NoMatch);
                else if (algo.equalsIgnoreCase("PWD"))
                    config.setLayoutMatchingMode(AndroidSourceSinkManager.LayoutMatchingMode.MatchSensitiveOnly);
                else if (algo.equalsIgnoreCase("ALL"))
                    config.setLayoutMatchingMode(AndroidSourceSinkManager.LayoutMatchingMode.MatchAll);
                else {
                    System.err.println("Invalid layout matching mode");
                    return false;
                }
                i += 2;
            } else if (args[i].equalsIgnoreCase("--aliasflowins")) {
                config.setFlowSensitiveAliasing(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--paths")) {
                config.setComputeResultPaths(true);
                i++;
            } else if (args[i].equalsIgnoreCase("--nopaths")) {
                config.setComputeResultPaths(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--aggressivetw")) {
                flowDroidConfig.setAggressiveTaintWrapper(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--pathalgo")) {
                String algo = args[i + 1];
                if (algo.equalsIgnoreCase("CONTEXTSENSITIVE"))
                    config.setPathBuilder(DefaultPathBuilderFactory.PathBuilder.ContextSensitive);
                else if (algo.equalsIgnoreCase("CONTEXTINSENSITIVE"))
                    config.setPathBuilder(DefaultPathBuilderFactory.PathBuilder.ContextInsensitive);
                else if (algo.equalsIgnoreCase("SOURCESONLY"))
                    config.setPathBuilder(DefaultPathBuilderFactory.PathBuilder.ContextInsensitiveSourceFinder);
                else {
                    System.err.println("Invalid path reconstruction algorithm");
                    return false;
                }
                i += 2;
            } else if (args[i].equalsIgnoreCase("--summarypath")) {
                flowDroidConfig.setSummaryPath(args[i + 1]);
                i += 2;
            } else if (args[i].equalsIgnoreCase("--saveresults")) {
                flowDroidConfig.setResultFilePath(args[i + 1]);
                i += 2;
            } else if (args[i].equalsIgnoreCase("--sysflows")) {
                config.setIgnoreFlowsInSystemPackages(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--notaintwrapper")) {
                flowDroidConfig.setNoTaintWrapper(true);
                i++;
            } else if (args[i].equalsIgnoreCase("--repeatcount")) {
                System.err.println("This version of flowdroid does not support repeatcount");
                return false;
            } else if (args[i].equalsIgnoreCase("--noarraysize")) {
                config.setEnableArraySizeTainting(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--arraysize")) {
                config.setEnableArraySizeTainting(true);
                i++;
            } else if (args[i].equalsIgnoreCase("--notypetightening")) {
                InfoflowAndroidConfiguration.setUseTypeTightening(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--safemode")) {
                InfoflowAndroidConfiguration.setUseThisChainReduction(false);
                i++;
            } else if (args[i].equalsIgnoreCase("--logsourcesandsinks")) {
                config.setLogSourcesAndSinks(true);
                i++;
            } else if (args[i].equalsIgnoreCase("--callbackanalyzer")) {
                String algo = args[i + 1];
                if (algo.equalsIgnoreCase("DEFAULT"))
                    config.setCallbackAnalyzer(InfoflowAndroidConfiguration.CallbackAnalyzer.Default);
                else if (algo.equalsIgnoreCase("FAST"))
                    config.setCallbackAnalyzer(InfoflowAndroidConfiguration.CallbackAnalyzer.Fast);
                else {
                    System.err.println("Invalid callback analysis algorithm");
                    return false;
                }
                i += 2;
            } else if (args[i].equalsIgnoreCase("--maxthreadnum")) {
                config.setMaxThreadNum(Integer.valueOf(args[i + 1]));
                i += 2;
            } else if (args[i].equalsIgnoreCase("--arraysizetainting")) {
                config.setEnableArraySizeTainting(true);
                i++;
            } else
                i++;
        }
        return true;
    }

    private static InfoflowResults runAnalysisFD(final FlowDroidConfig flowDroidConfig) {
        return null;
    }

    private static void printUsage() {
        System.out.println("FlowDroid (c) Secure Software Engineering Group @ EC SPRIDE");
        System.out.println();
        System.out.println("Incorrect arguments: [0] = apk-file, [1] = android-jar-directory");
        System.out.println("Optional further parameters:");
        System.out.println("\t--TIMEOUT n Time out after n seconds");
        System.out.println("\t--SYSTIMEOUT n Hard time out (kill process) after n seconds, Unix only");
        System.out.println("\t--SINGLEFLOW Stop after finding first leak");
        System.out.println("\t--IMPLICIT Enable implicit flows");
        System.out.println("\t--NOSTATIC Disable static field tracking");
        System.out.println("\t--NOEXCEPTIONS Disable exception tracking");
        System.out.println("\t--APLENGTH n Set access path length to n");
        System.out.println("\t--CGALGO x Use callgraph algorithm x");
        System.out.println("\t--NOCALLBACKS Disable callback analysis");
        System.out.println("\t--LAYOUTMODE x Set UI control analysis mode to x");
        System.out.println("\t--ALIASFLOWINS Use a flow insensitive alias search");
        System.out.println("\t--NOPATHS Do not compute result paths");
        System.out.println("\t--AGGRESSIVETW Use taint wrapper in aggressive mode");
        System.out.println("\t--PATHALGO Use path reconstruction algorithm x");
        System.out.println("\t--LIBSUMTW Use library summary taint wrapper");
        System.out.println("\t--SUMMARYPATH Path to library summaries");
        System.out.println("\t--SYSFLOWS Also analyze classes in system packages");
        System.out.println("\t--NOTAINTWRAPPER Disables the use of taint wrappers");
        System.out.println("\t--NOTYPETIGHTENING Disables the use of taint wrappers");
        System.out.println("\t--LOGSOURCESANDSINKS Print out concrete source/sink instances");
        System.out.println("\t--CALLBACKANALYZER x Uses callback analysis algorithm x");
        System.out.println("\t--MAXTHREADNUM x Sets the maximum number of threads to be used by the analysis to x");
        System.out.println();
        System.out.println("Supported callgraph algorithms: AUTO, CHA, RTA, VTA, SPARK, GEOM");
        System.out.println("Supported layout mode algorithms: NONE, PWD, ALL");
        System.out.println("Supported path algorithms: CONTEXTSENSITIVE, CONTEXTINSENSITIVE, SOURCESONLY");
        System.out.println("Supported callback algorithms: DEFAULT, FAST");
    }

    @Override
    protected List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.singletonList(FlowDroidBaseAnalysis.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public FlowDroidResult runAnalysis(FlowDroidConfig config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map) {
        if (config == null) {
            System.out.println("Configuration was invalid aborting analysis");
            return null;
        }
        File outputDir = new File("JimpleOutput");
        if (outputDir.isDirectory()) {
            boolean success = true;
            for (File f : outputDir.listFiles()) {
                success = success && f.delete();
            }
            if (!success) {
                System.err.println("Cleanup of output directory " + outputDir + " failed!");
            }
            outputDir.delete();
        }
        // Run the analysis
        IAnalysisResult result = map.get(FlowDroidBaseAnalysis.class);
        if (result instanceof IntermediateResult) {
            ((IntermediateResult) result).getSplitInfoflow().runAnalysisSecondStep();
        }
        return new FlowDroidResult();
    }

    @Override
    public IAnalysisConfig convertConfig(FlowDroidConfig config, Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> aClass) {
        return config;
    }

    @Override
    public FlowDroidConfig parseConfig(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.out.println("Alternatively you can provide 1 Argument:");
            System.out.println("A file where the previously listed arguments are on a line of their own");
            System.out.println("Comment lines start with #");
            return null;
        }
        if (args.length == 1) {
            if (args[0].endsWith(".conf")) {
                try {
                    args = readArgsFromFile(args[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error");
                    return null;
                }
            } else {
                return null;
            }
        }
        FlowDroidConfig flowDroidConfig = new FlowDroidConfig();


        if (!parseAdditionalOptions(args, flowDroidConfig))
            return null;
        if (!validateAdditionalOptions(flowDroidConfig))
            return null;
        if (flowDroidConfig.getRepeatCount() <= 0)
            return null;

        File apkFile = new File(args[0]);
        if (apkFile.isDirectory()) {
            System.err.println("This version of flowdroid does not support directories");
            return null;
        } else {
            //apk is a file so grab the extension
            String extension = apkFile.getName().substring(apkFile.getName().lastIndexOf("."));
            if (!extension.equalsIgnoreCase(".apk")) {
                System.err.println("Invalid input file format: " + extension);
                return null;
            }
        }
        flowDroidConfig.setApkFile(apkFile.getAbsolutePath());
        flowDroidConfig.setAndroidJarPath(args[1]);
        return flowDroidConfig;
    }

    private String[] readArgsFromFile(String configFile) throws IOException {
        String correctReplacement = Matcher.quoteReplacement(System.getProperty("user.home"));
        Path configPath = Paths.get(configFile.replaceFirst("^~", correctReplacement));
        return Files.readAllLines(configPath)
                .stream().filter(s -> !s.startsWith("#")).flatMap(s -> {
                    if (s.startsWith("-")) {
                        return Arrays.stream(s.split("\\s+"));
                    } else return Stream.of(s);
                }).toArray(String[]::new);
    }
}
