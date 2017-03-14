package de.tud.cs.peaks.analysis.reflectionUsage;

import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootResult;
import de.tud.cs.peaks.sootconfig.SootRun;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0) {
            FluentOptions options = new FluentOptions().wholeProgramAnalysis().keepLineNumbers().allowPhantomReferences();
            AnalysisTarget target = new AnalysisTarget().processPath(args[0]);

            SootRun sootRun = new SootRun(options, target);
            SootResult res = sootRun.perform();
            System.out.println(res.getCompleteOutput());
            ReflectionAnalysis analysis = new ReflectionAnalysis();
            analysis.performAnalysis(res.getScene().getCallGraph());
            System.out.println("Done");
        } else throw new RuntimeException("args");
    }
}
