package de.tu_darmstadt.stg.sootkeeper.flowdroid.base;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import soot.G;

public class IntermediateResult implements IAnalysisResult {

    private G.GlobalObjectGetter objectGetter;
    private SplitInfoflow splitInfoflow;

    public IntermediateResult(G.GlobalObjectGetter objectGetter, SplitInfoflow splitInfoflow) {
        this.objectGetter = objectGetter;
        this.splitInfoflow = splitInfoflow;
    }

    public G.GlobalObjectGetter getObjectGetter() {
        return objectGetter;
    }

    public SplitInfoflow getSplitInfoflow() {
        return splitInfoflow;
    }
}
