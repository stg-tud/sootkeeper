package de.tud.cs.peaks.misc;

public class PeaksField extends PeaksHost {

    private final String type;
    public PeaksField(String name, Visibility visibility, String path,String type, float rating) {
    	super(name, visibility, path, rating);
        this.type = type;
    }
    
    public String getQualifiedName(){
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(": ").append(name).append(" in ").append(path);
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeaksField that = (PeaksField) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        //if (rating != null ? !rating.equals(that.rating) : that.rating != null) return false;
        if (visibility != null ? !visibility.equals(that.visibility) : that.visibility != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        //result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PeaksField{" +
                "type='" + type + '\'' +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
