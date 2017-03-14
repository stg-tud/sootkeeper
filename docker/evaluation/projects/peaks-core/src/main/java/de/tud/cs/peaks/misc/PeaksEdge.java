package de.tud.cs.peaks.misc;

public class PeaksEdge {


    private final PeaksMethod source;
    private final PeaksMethod target;
    private final boolean isStatic;

    public PeaksEdge(PeaksMethod source, PeaksMethod target) {
        this.source = source;
        this.target = target;
        isStatic = false;
    }

    public PeaksEdge(PeaksMethod source, PeaksMethod target, boolean isStatic) {
        this.source = source;
        this.target = target;
        this.isStatic = isStatic;
    }

    public PeaksMethod getSource() {
        return source;
    }

    public PeaksMethod getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeaksEdge peaksEdge = (PeaksEdge) o;

        if (isStatic != peaksEdge.isStatic) return false;
        if (source != null ? !source.equals(peaksEdge.source) : peaksEdge.source != null) return false;
        if (target != null ? !target.equals(peaksEdge.target) : peaksEdge.target != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (isStatic ? 1 : 0);
        return result;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isStatic) {
            sb.append("STATIC ");
        }
        sb.append("Edge: ");
        sb.append("From ").append(source).append(" ==> ").append(target);
        return sb.toString();
    }
}
