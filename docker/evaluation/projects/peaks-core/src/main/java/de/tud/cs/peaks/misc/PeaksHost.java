package de.tud.cs.peaks.misc;

public abstract class PeaksHost {
	protected final String path;
	protected final String name;
	protected final float rating;
	protected final Visibility visibility;
    
    public PeaksHost(String name, Visibility visibility, String path, float rating) {
		super();
		this.path = path;
		this.name = name;
		this.rating = rating;
		this.visibility = visibility;
	}
    
    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public Visibility getVisibility() {
        return visibility;
    }
    
    public abstract String getQualifiedName();
}
