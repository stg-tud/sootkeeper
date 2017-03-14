package de.tud.cs.peaks.toolbox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.tud.cs.peaks.toolbox.tags.ImmutableTag;
import de.tud.cs.peaks.toolbox.tags.PeaksTag;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.tagkit.Tag;

import static de.tud.cs.peaks.analysis.staticUsage.StaticUsageAnalysis.isSideEffectFree;


public class ImmutableCheck {

	private static final String[] knownImmutablesAsArray = {
			"java.lang.String", "java.lang.Character", "java.lang.Byte",
			"java.lang.Short", "java.lang.Integer", "java.lang.Long",
			"java.lang.Float", "java.lang.Double", "java.lang.Boolean",
			"java.math.BigInteger", "java.math.BigDecimal",
			"java.lang.StackTraceElement", "long", "int", "byte", "short",
			"float", "double", "boolean", "char" };

	private final List<String> knownImmutables = Arrays
			.asList(knownImmutablesAsArray);
	private final HashSet<Type> visited;
	private final TypeSwitch typeSwitch = new PeaksTypeSwitch();

	public ImmutableCheck() {
		visited = new HashSet<>();
	}

	/**
	 * Checks if the field is of an immutable Type
	 * 
	 * @param field
	 *            The field to be checked
	 * @return Returns whether the given Field is Immutable.
	 */
	public boolean isImmutable(SootField field) {
		if (field.isFinal()) {
			field.getType().apply(typeSwitch);
			return (boolean) typeSwitch.getResult();
		}
		return false;
	}

	/*
	 * Determine whether the Class is immutable.
	 */
	private boolean checkClassForImmutability(SootClass clazz) {
		visited.add(clazz.getType());
		// Class already tagged?
		Tag tag = clazz.getTag(PeaksTag.immutableTag);
		if (tag != null) {
			return ((ImmutableTag) tag).isImmutable();
		}

		boolean isImmutable = true;
		// All fields have to be immutable
		for (SootField sootField : clazz.getFields()) {
			isImmutable = checkFieldForImmutability(sootField, clazz);
			if (!isImmutable) {
				break; // Can Stop here
			}
		}
		// All
		for (SootMethod sootMethod : clazz.getMethods()) {
			if (!sootMethod.isConstructor()
					&& !isSideEffectFree(sootMethod, true)) {
				isImmutable = false;
				break;
			}
		}
		clazz.addTag(new ImmutableTag(isImmutable));
		return isImmutable;
	}

	/*
	 * When checking a class for immutability you need to check fields. Field
	 * has to be read only.
	 */
	private boolean checkFieldForImmutability(SootField sootField,
			SootClass declaringClass) {
		if (!visited.contains(sootField.getType()) && !isImmutable(sootField)) {
			if (sootField.isPrivate()) {
				for (SootMethod sootMethod : declaringClass.getMethods()) {
					if (checkMethodForFieldAccess(sootField, sootMethod)) {
						return false;
					} // Check whether private field is written to (=de-facto final)
				}
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean checkMethodForFieldAccess(SootField sootField,
			SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			for (Unit unit : sootMethod.getActiveBody().getUnits()) {
				if (unit instanceof AssignStmt) {
					Value leftOp = ((AssignStmt) unit).getLeftOp();
					if (leftOp instanceof FieldRef) {
						if (((FieldRef) leftOp).getField().equals(sootField)) {
							return true;
						}
					}
				}
			}
		} else {
			// If no body is present we have to assume the worst
			return true;
		}
		return false;
	}

	private class PeaksTypeSwitch extends TypeSwitch {
		@Override
		public void caseArrayType(ArrayType t) {
			t.baseType.apply(this);
		}

		@Override
		public void caseRefType(RefType t) {
			SootClass sc = t.getSootClass();
			if (knownImmutables.contains(sc.getName())) {
				setResult(true);
			} else if (!visited.contains(sc.getType())) {
				setResult(checkClassForImmutability(sc));
			}
		}

		@Override
		public void defaultCase(Type t) {
			if (t instanceof PrimType) {
				setResult(true);
			} else {
				setResult(false);
			}
		}
	}

}
