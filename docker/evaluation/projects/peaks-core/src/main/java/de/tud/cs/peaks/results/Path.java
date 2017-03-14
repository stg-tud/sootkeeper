package de.tud.cs.peaks.results;

import de.tud.cs.peaks.misc.PeaksMethod;

import java.util.List;

public class Path {

	private List<PeaksMethod> nodes;

	public Path(List<PeaksMethod> nodes) {
		this.nodes = nodes;
	}

	public List<PeaksMethod> getNodes() {
		return nodes;
	}

	public void setNodes(List<PeaksMethod> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (PeaksMethod pn : nodes) {
			result.append(pn).append(System.getProperty("line.separator"));
		}
		return result.toString();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (!nodes.equals(path.nodes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }
}
