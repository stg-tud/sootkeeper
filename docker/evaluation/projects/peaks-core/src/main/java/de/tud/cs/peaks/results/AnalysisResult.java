package de.tud.cs.peaks.results;

import de.tud.cs.peaks.misc.PeaksEdge;
import de.tud.cs.peaks.misc.PeaksField;
import de.tud.cs.peaks.misc.PeaksHost;
import de.tud.cs.peaks.misc.PeaksMethod;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AnalysisResult {
	private final String analysisVersion;
	private final AnalysisType type;

	/**
	 * Internal Sets for found Methods
	 */
	private final Set<PeaksMethod> publicMethodsFound = new HashSet<>();
	private final Set<PeaksMethod> protectedMethodsFound = new HashSet<>();
	private final Set<PeaksMethod> defaultVisibilityMethodsFound = new HashSet<>();
	private final Set<PeaksMethod> privateMethodsFound = new HashSet<>();
	private final Set<PeaksMethod> sideEffectFreeMethodsFound = new HashSet<>();
	/**
	 * Internal Set for found Calls
	 */
	private final Set<PeaksEdge> foundCalls = new HashSet<>();
	/**
	 * Internal Sets for found Fields
	 */
	private final Set<PeaksField> publicFieldsFound = new HashSet<>();
	private final Set<PeaksField> protectedFieldsFound = new HashSet<>();
	private final Set<PeaksField> defaultVisibilityFieldsFound = new HashSet<>();
	private final Set<PeaksField> immutableFieldsFound = new HashSet<>();
	private final Set<PeaksField> privateFieldsFound = new HashSet<>();

	private Set<Path> paths = new HashSet<>();

	public AnalysisResult(String version, AnalysisType type) {
		analysisVersion = version;
		this.type = type;
	}

	public String getAnalysisVersion() {
		return analysisVersion;
	}

	public boolean addMethod(PeaksMethod method) {
		switch (method.getVisibility()) {
		case Public:
			return publicMethodsFound.add(method);
		case Private:
			return privateMethodsFound.add(method);
		case Default:
			return defaultVisibilityMethodsFound.add(method);
		case Protected:
			return protectedMethodsFound.add(method);
		}
		return false;
	}

	/**
	 * Add an found immutable Field to the internal Set.
	 * 
	 * @param field
	 *            The field to add.
	 * @return Returns whether the adding succeeded
	 */
	public boolean addImmutableField(PeaksField field) {
		return immutableFieldsFound.add(field);
	}

	public boolean addField(PeaksField field) {
		switch (field.getVisibility()) {
		case Public:
			return publicFieldsFound.add(field);
		case Default:
			return defaultVisibilityFieldsFound.add(field);
		case Protected:
			return protectedFieldsFound.add(field);
		case Private:
			return privateFieldsFound.add(field);
		default:
			return false;
		}
	}

	public void setPaths(Set<Path> paths) {
		this.paths = paths;
	}

	public Set<Path> getPaths() {
		return paths;
	}

	public boolean addCall(PeaksEdge edge) {
		return this.foundCalls.add(edge);
	}

	public Collection<PeaksMethod> getPublicMethods() {
		return new HashSet<>(publicMethodsFound);
	}

	public Collection<PeaksMethod> getProtectedMethods() {
		return new HashSet<>(protectedMethodsFound);
	}

	public Collection<PeaksMethod> getDefaultMethods() {
		return new HashSet<>(defaultVisibilityMethodsFound);
	}

	public Collection<PeaksMethod> getPrivateMethods() {
		return new HashSet<>(privateMethodsFound);
	}

	private Collection<PeaksMethod> getRelevantMethods(float rating, Set<PeaksMethod> set) {
		Set<PeaksMethod> relevant = new HashSet<PeaksMethod>();
		for (PeaksMethod pm : set) {
			if (pm.getRating() >= rating)
				relevant.add(pm);
		}
		return relevant;
	}

	public Collection<PeaksMethod> getRelevantPrivateMethods(float rating) {
		return getRelevantMethods(rating, privateMethodsFound);
	}

	public Collection<PeaksMethod> getRelevantProtectedMethods(float rating) {
		return getRelevantMethods(rating, protectedMethodsFound);
	}

	public Collection<PeaksMethod> getRelevantPublicMethods(float rating) {
		return getRelevantMethods(rating, publicMethodsFound);
	}

	public Collection<PeaksMethod> getRelevantMethods(float rating) {
		Set<PeaksMethod> relevant = new HashSet<PeaksMethod>();
		relevant.addAll(getRelevantPublicMethods(rating));
		relevant.addAll(getRelevantProtectedMethods(rating));
		relevant.addAll(getRelevantPrivateMethods(rating));
		return relevant;
	}

	public double getSumOfRelevantMethods(float rating) {
		double sum = 0.0d;
		for (PeaksMethod pm : getRelevantMethods(rating)) {
			sum += pm.getRating();
		}
		return sum;
	}

	public Collection<PeaksEdge> getCalls() {
		return new HashSet<>(foundCalls);
	}

	public AnalysisType getType() {
		return type;
	}

	public Collection<PeaksMethod> getAllMethods() {
		HashSet<PeaksMethod> result = new HashSet<>(publicMethodsFound);
		result.addAll(protectedMethodsFound);
		result.addAll(privateMethodsFound);
		result.addAll(defaultVisibilityMethodsFound);
		result.addAll(sideEffectFreeMethodsFound);
		return result;
	}

	public Collection<PeaksField> getPublicFields() {
		return new HashSet<>(publicFieldsFound);
	}

	public Collection<PeaksField> getRelevantFields(float rating) {
		Set<PeaksField> relevant = new HashSet<PeaksField>();
		relevant.addAll(getRelevantPublicFields(rating));
		relevant.addAll(getRelevantProtectedFields(rating));
		relevant.addAll(getRelevantPrivateFields(rating));
		return relevant;
	}

	public double getSumOfRelevantFields(float rating) {
		double sum = 0.0d;
		for (PeaksField pf : getRelevantFields(rating)) {
			sum += pf.getRating();
		}
		return sum;
	}

	public Collection<PeaksField> getRelevantPublicFields(float rating) {
		return getRelevantFields(rating, publicFieldsFound);
	}

	public Collection<PeaksField> getProtectedFields() {
		return new HashSet<>(protectedFieldsFound);
	}

	public Collection<PeaksField> getRelevantProtectedFields(float rating) {
		return getRelevantFields(rating, protectedFieldsFound);
	}

	public Collection<PeaksField> getRelevantPrivateFields(float rating) {
		return getRelevantFields(rating, privateFieldsFound);
	}

	private Collection<PeaksField> getRelevantFields(float rating, Set<PeaksField> set) {
		Set<PeaksField> relevantSet = new HashSet<PeaksField>();
		for (PeaksField pf : set) {
			if (pf.getRating() >= rating)
				relevantSet.add(pf);
		}
		return relevantSet;
	}

	public Collection<PeaksField> getDefaultFields() {
		return new HashSet<>(defaultVisibilityFieldsFound);
	}

	public boolean addSideEffectFreeMethod(PeaksMethod method) {
		return sideEffectFreeMethodsFound.add(method);
	}

	public Collection<PeaksMethod> getSideEffectFreeMethods() {
		return new HashSet<>(sideEffectFreeMethodsFound);
	}

	public Collection<PeaksField> getImmutableFieldsFound() {
		return immutableFieldsFound;
	}

	public Collection<PeaksField> getAllFields() {
		HashSet<PeaksField> result = new HashSet<>(publicFieldsFound);
		result.addAll(protectedFieldsFound);
		result.addAll(defaultVisibilityFieldsFound);
		result.addAll(immutableFieldsFound);
		result.addAll(privateFieldsFound);
		return result;
	}

	public Collection<? extends PeaksHost> getAllHosts() {
		HashSet<PeaksHost> result = new HashSet<>();
		result.addAll(getAllMethods());
		result.addAll(getAllFields());
		return result;
	}

	public int numberOfMethods() {
		return privateMethodsFound.size() + protectedMethodsFound.size() + defaultVisibilityMethodsFound.size() + publicMethodsFound.size()
				+ sideEffectFreeMethodsFound.size();
	}

	public int numberOfFields() {
		return protectedFieldsFound.size() + defaultVisibilityFieldsFound.size() + immutableFieldsFound.size() + publicFieldsFound.size()
				+ privateFieldsFound.size();
	}

	public int numberOfPaths() {
		return paths.size();
	}

	@Override
	public String toString() {
		return type.getDescription();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AnalysisResult that = (AnalysisResult) o;

		if (type != that.type)
			return false;
		if (!analysisVersion.equals(that.analysisVersion))
			return false;
		if (!defaultVisibilityFieldsFound.equals(that.defaultVisibilityFieldsFound))
			return false;
		if (!defaultVisibilityMethodsFound.equals(that.defaultVisibilityMethodsFound))
			return false;
		if (!foundCalls.equals(that.foundCalls))
			return false;
		if (!immutableFieldsFound.equals(that.immutableFieldsFound))
			return false;
		if (!privateFieldsFound.equals(that.privateFieldsFound))
			return false;
		if (!paths.equals(that.paths))
			return false;
		if (!privateMethodsFound.equals(that.privateMethodsFound))
			return false;
		if (!protectedFieldsFound.equals(that.protectedFieldsFound))
			return false;
		if (!protectedMethodsFound.equals(that.protectedMethodsFound))
			return false;
		if (!publicFieldsFound.equals(that.publicFieldsFound))
			return false;
		if (!publicMethodsFound.equals(that.publicMethodsFound))
			return false;
		if (!sideEffectFreeMethodsFound.equals(that.sideEffectFreeMethodsFound))
			return false;

		return true;
	}

	private void checkSet(Set<?> set, Set<?> OtherSet) {
		boolean result = set.equals(OtherSet);
		System.out.println();
	}

	@Override
	public int hashCode() {
		int result = analysisVersion.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + publicMethodsFound.hashCode();
		result = 31 * result + protectedMethodsFound.hashCode();
		result = 31 * result + defaultVisibilityMethodsFound.hashCode();
		result = 31 * result + privateMethodsFound.hashCode();
		result = 31 * result + sideEffectFreeMethodsFound.hashCode();
		result = 31 * result + foundCalls.hashCode();
		result = 31 * result + publicFieldsFound.hashCode();
		result = 31 * result + protectedFieldsFound.hashCode();
		result = 31 * result + defaultVisibilityFieldsFound.hashCode();
		result = 31 * result + immutableFieldsFound.hashCode();
		result = 31 * result + privateFieldsFound.hashCode();
		result = 31 * result + paths.hashCode();
		return result;
	}
}
