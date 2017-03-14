package de.tud.cs.peaks.analysis.staticUsage;

import java.util.HashSet;
import java.util.Set;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.ParameterRef;
import soot.jimple.TableSwitchStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import de.tud.cs.peaks.analysis.staticUsage.toolbox.tags.SideEffectTag;

/**
 * A simple taint analysis computing all fields that were modified by the given
 * method using parameters. NOT tested!
 *
 * @author Florian K�bler
 *
 */
@Deprecated
public class SetterAnalysis extends ForwardFlowAnalysis<Unit, Set<Value>> {

	private final SootMethod method;
	private final Set<String> seenStmts;
	private final Set<SootField> result;

	/**
	 *
	 * @param method
	 *            to analyze. Caller must ensure method has active body
	 * @param field
	 */
	public SetterAnalysis(SootMethod method) {
		super(new ExceptionalUnitGraph(method.getActiveBody()));
		this.method = method;
		this.seenStmts = new HashSet<String>();
		this.result = new HashSet<SootField>();
	}

	public Set<SootField> getResult() {
		return new HashSet<SootField>(result);
	}

	@Override
	protected void flowThrough(final Set<Value> in, Unit d, final Set<Value> out) {
		out.addAll(in);
		d.apply(new AbstractStmtSwitch() {

			@Override
			public void caseIdentityStmt(IdentityStmt stmt) {
				Value left = stmt.getLeftOp();
				Value right = stmt.getRightOp();
				// All parameters are tainted
				if (right instanceof ParameterRef) {
					out.add(left);
				}
			}

			@Override
			public void caseAssignStmt(AssignStmt stmt) {
				Value left = stmt.getLeftOp();
				Value right = stmt.getRightOp();

				if (in.contains(right)) {
					out.add(left);
				}
				if (left instanceof Local) {
					if (in.contains(right)) {
						out.remove(right);
					}
				} else if (left instanceof FieldRef) {
					SootClass fieldsClass = ((FieldRef) left).getField()
							.getDeclaringClass();
					SootClass methodsClass = method.getDeclaringClass();
					if (fieldsClass.equals(methodsClass)) {
						result.add(((FieldRef) left).getField());
					}
					method.addTag(new SideEffectTag(false));
				}

			}

			@Override
			public void caseIfStmt(IfStmt stmt) {
				String uniqueString = Integer.toString(stmt.hashCode())
						+ stmt.toString();
				if (!seenStmts.contains(uniqueString)) {
					seenStmts.add(uniqueString);
					stmt.getTarget().apply(this);
				}
			}

			@Override
			public void caseTableSwitchStmt(TableSwitchStmt stmt) {
				for (Unit target : stmt.getTargets()) {
					target.apply(this);
				}
			}

			@Override
			public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
				for (Unit target : stmt.getTargets()) {
					target.apply(this);
				}
			}

		});
		// TODO Auto-generated method stub

	}

	@Override
	protected Set<Value> newInitialFlow() {
		return new HashSet<>();
	}

	@Override
	protected Set<Value> entryInitialFlow() {
		return new HashSet<>();
	}

	@Override
	protected void merge(Set<Value> in1, Set<Value> in2, Set<Value> out) {
		out.addAll(in1);
		out.addAll(in2);
	}

	@Override
	protected void copy(Set<Value> source, Set<Value> dest) {
		dest.clear();
		dest.addAll(source);
	}

}
