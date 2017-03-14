package de.tudarmstadt.stg.sootkeeper.study.deadcode.analysis;

import de.tud.cs.peaks.sootconfig.*;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.io.File;
import java.util.Map;

public class MainClass {

    public static void main(String[] args) {
        if (args.length > 1)
            throw new RuntimeException("To many arguments");
        if (args.length == 0)
            throw new RuntimeException("Please provide target as argument");
        String target =  args[0];
        runAnalysis(target);
    }

    public static void runAnalysis(String tgt) {
        G.reset();

        FluentOptions fluentOptions = new FluentOptions()
                .prependClasspath()
                .allowPhantomReferences()
                //.ignoreClasspathErrors()
                .addPhaseOptions(new JimpleBodyCreationPhaseOptions().useOriginalNames());

        AnalysisTarget target = new AnalysisTarget().processPath(tgt);
        SootRun run = new SootRun(fluentOptions, target);
        SootResult res = run.perform();

        // Register the transform
        Transform transform = new Transform("jtp.analysis", new BodyTransformer() {

            @Override
            protected void internalTransform(Body b, String phaseName,
                                             Map<String, String> options) {
                LiveVariableAnalysis ipa = new LiveVariableAnalysis(new ExceptionalUnitGraph(b));
            }

        });

        PackManager.v().getPack("jtp").add(transform);


        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod m : sc.getMethods()) {
                if (m.hasActiveBody())
                    transform.apply(m.getActiveBody());
            }
        }


    }
}
