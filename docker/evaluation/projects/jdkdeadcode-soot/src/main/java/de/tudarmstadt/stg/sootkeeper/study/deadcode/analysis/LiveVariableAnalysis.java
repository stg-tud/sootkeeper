package de.tudarmstadt.stg.sootkeeper.study.deadcode.analysis;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class LiveVariableAnalysis extends BackwardFlowAnalysis<Unit, Set<Local>> {

    public LiveVariableAnalysis(DirectedGraph<Unit> graph) {
        super(graph);

        doAnalysis();

    }

    @Override
    protected void flowThrough(Set<Local> in, Unit unit, Set<Local> out) {
        out.addAll(in);
        for (ValueBox defBox : unit.getDefBoxes()) {
            Value v = defBox.getValue();
            if (v instanceof Local) {
                if (!in.contains(v))
                    print(v);
                out.remove(v);
            }
        }

        for (ValueBox useBox : unit.getUseBoxes()) {
            Value v = useBox.getValue();
            if (v instanceof Local) {
                out.add((Local) v);
            }
        }
    }

    @Override
    protected Set<Local> newInitialFlow() {
        return new HashSet<>();
    }

    @Override
    protected Set<Local> entryInitialFlow() {
        return new HashSet<>();
    }

    @Override
    protected void merge(Set<Local> in1, Set<Local> in2, Set<Local> out) {
        out.clear();
        out.addAll(in1);
        out.addAll(in2);
    }

    @Override
    protected void copy(Set<Local> in, Set<Local> out) {
        out.clear();
        out.addAll(in);
    }

    private void print(Value v) {
        File f = new File("results.txt");
        try (FileWriter fw = new FileWriter(f, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
