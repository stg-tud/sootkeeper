package de.tud.cs.peaks.analysis.staticUsage;

import java.util.HashSet;
import java.util.Set;

import de.tud.cs.peaks.analysis.staticUsage.toolbox.tags.SideEffectTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import static de.tud.cs.peaks.toolbox.tags.PeaksTag.sideEffectTag;

public class SideEffectAnalysis extends ForwardFlowAnalysis<Unit, Set<Value>> {
	private final SootMethod method;
	private final int currentDepth;
	private final static Logger logger = LoggerFactory.getLogger(SideEffectAnalysis.class);

	private static Set<String> seenStmts;

	private static Set<SootMethod> analysedMethods = new HashSet<>();

	public SideEffectAnalysis(SootMethod sootMethod, boolean first) {
		this(sootMethod, 0);
		if (first) {
			analysedMethods = new HashSet<>();
			analysedMethods.add(sootMethod);//In this code, we do not tolerate redundancy in this code.
			seenStmts = new HashSet<>();
		}

	}

	private SideEffectAnalysis(SootMethod sootMethod, int currentDepth) {
		super(new ExceptionalUnitGraph(sootMethod.getActiveBody()));
		this.method = sootMethod;
		this.currentDepth = currentDepth;
		analysedMethods.add(sootMethod);//In this code, we do not tolerate redundancy in this code.
	}

	@Override
	protected void flowThrough(final Set<Value> in, Unit d, final Set<Value> out) {
		out.addAll(in);
		d.apply(new AbstractStmtSwitch() {
			@Override
			public void caseIdentityStmt(IdentityStmt stmt) {
				Value left = stmt.getLeftOp();
				Value right = stmt.getRightOp();
				if (right instanceof ParameterRef) {
					Type paramType = right.getType();
					if (paramType instanceof ArrayType) {
						// If the parameter is an array ref we need to taint it
						// since it *could* be a field
						out.add(left);
					}
				}
			}

			@Override
			public void caseAssignStmt(AssignStmt stmt) {
				Value left = stmt.getLeftOp();
				Value right = stmt.getRightOp();
				if (right instanceof InvokeExpr) {
					handleInvokeExpr((InvokeExpr) right);
				}
				if (left instanceof Local) {
					boolean isLocal = right instanceof Local;
					boolean isTainted = in.contains(right);
					if (isLocal || !isTainted) {
						out.remove(right);
					}
				} else if (left instanceof ArrayRef) {
					if (in.contains(left)) {
						method.addTag(new SideEffectTag(false));
					}
				} else if (left instanceof FieldRef) { // if we assign to a
														// field that's bad
					method.addTag(new SideEffectTag(false));
				}

			}

			@Override
			public void caseIfStmt(IfStmt stmt) {
				String uniqueString = Integer.toString(stmt.hashCode()) + stmt.toString();
				if (!seenStmts.contains(uniqueString)) {
					seenStmts.add(uniqueString);
					stmt.getTarget().apply(this);
				}
			}

			@Override
			public void caseGotoStmt(GotoStmt stmt) {
				if (stmt.containsInvokeExpr()) {
					stmt.getInvokeExpr().apply(this);
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

			@Override
			public void caseInvokeStmt(InvokeStmt stmt) {
				handleInvokeExpr(stmt.getInvokeExpr());
			}

			private void handleInvokeExpr(InvokeExpr expr) {
					SootMethod target = expr.getMethod();
					if (target.isNative() && !target.hasTag(sideEffectTag)) {
						target.addTag(new SideEffectTag(false));
						method.addTag(new SideEffectTag(false));
					} else if (analysedMethods.contains(target)) {
						logger.debug(
								"Got to recursion in "
										+ method.getDeclaration() + " in "
										+ method.getDeclaringClass() + ":; " + expr);

					} else if (!analyseMethod(target)) {
						method.addTag(new SideEffectTag(false));
					}


			}

			private boolean analyseMethod(SootMethod target) {
				SideEffectTag tag = (SideEffectTag) target
						.getTag(sideEffectTag);
				boolean result = false;
				if (tag == null) {
					if (target.hasActiveBody()) {
						SideEffectAnalysis sideEffectAnalysis = new SideEffectAnalysis(
								target, currentDepth + 1);
						tag = sideEffectAnalysis.doAnalyis();
						result = tag.isSideEffectFree();
					} else if (target.isAbstract()) {
						// this must be bad. we don't know what's going to happen
						target.addTag(new SideEffectTag(false));
					} else if (target.isPhantom() || target.isNative()) {
						// be conservative if we don't know the body its bad!
						target.addTag(new SideEffectTag(false));
					} else {
						logger.debug("Fallback "
									+ target.getDeclaration() + " in "
									+ target.getDeclaringClass().getName());

						target.addTag(new SideEffectTag(false));
					}
				} else {
					result = tag.isSideEffectFree();
				}
				return result;
			}


		});
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

	public SideEffectTag doAnalyis() {
		super.doAnalysis();
		SideEffectTag sideTag = (SideEffectTag) method.getTag(sideEffectTag);
		if (sideTag == null) {
			sideTag = new SideEffectTag(true);
			method.addTag(sideTag);
		}
		return sideTag;
	}

}
