package de.tud.cs.peaks.misc;

import java.util.LinkedList;
import java.util.List;

public class PeaksMethod extends PeaksHost{
    private final List<String> parameters;
    private final String returnValue;
    
    public PeaksMethod(String name, String returnValue, Visibility visibility, String path, float rating, List<String> parameters) {
    	super(name, visibility, path, rating);
        this.returnValue = returnValue;
        this.parameters = new LinkedList<>();
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
    }

    public List<String> getParameters() {
        return parameters;
    }
    
    public String getReturnValue() {
        return returnValue;
    }

    public String getQualifiedName(){
        StringBuilder sb = new StringBuilder();
        sb.append(path).append(": ");
        sb.append(returnValue).append(" ").append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i));
            if (i != parameters.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeaksMethod that = (PeaksMethod) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!parameters.equals(that.parameters)) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        //if (rating != null ? !rating.equals(that.rating) : that.rating != null) return false;
        if (returnValue != null ? !returnValue.equals(that.returnValue) : that.returnValue != null) return false;
        if (visibility != null ? !visibility.equals(that.visibility) : that.visibility != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parameters.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (returnValue != null ? returnValue.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        //result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(getQualifiedName());
        sb.append("> ");
        sb.append(" Rated as: ").append(rating);
        return sb.toString();
    }
}
