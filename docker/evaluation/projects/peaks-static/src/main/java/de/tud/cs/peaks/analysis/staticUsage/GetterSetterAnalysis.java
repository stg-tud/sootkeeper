package de.tud.cs.peaks.analysis.staticUsage;

import soot.Body;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.FieldRef;

/**
 * Provides a simple (over-approximated) check for the existence of getters or
 * rather setters.
 *
 * @author Florian Kübler
 *
 */
public class GetterSetterAnalysis implements IGetterSetterAnalysis {

	/**
	 * Checks all non-private methods of fields declaring class for field
	 * accesses.
	 *
	 * @param field
	 * @return
	 */
	@Override
	public boolean hasGetterOrSetter(SootField field) {
		SootClass sc = field.getDeclaringClass();

		// check every private method with existing body
		for (SootMethod m : sc.getMethods()) {
			if (!m.isPrivate()
					&& !(m.isConstructor() || m.getName().equals("<clinit>"))
					&& m.hasActiveBody()) {
				Body body = m.getActiveBody();
				for (Unit u : body.getUnits()) {

					// at assignments check for field access
					if (u instanceof AssignStmt) {
						AssignStmt stmt = (AssignStmt) u;
						Value left = stmt.getLeftOp();
						Value right = stmt.getRightOp();

						// field-writes with constants are acceptable
						if (left instanceof FieldRef) {
							FieldRef leftField = (FieldRef) left;

							if (leftField.getField().equals(field)
									&& !(right instanceof Constant)) {
								return true;
							}
						}

						// every field-read is considered as violation
						if (right instanceof FieldRef) {
							FieldRef rightField = (FieldRef) right;

							if (rightField.getField().equals(field)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}
}
