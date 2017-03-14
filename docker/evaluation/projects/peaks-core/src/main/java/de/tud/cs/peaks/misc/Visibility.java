package de.tud.cs.peaks.misc;


public enum Visibility {
    Public("public"),
    Private("private"),
    Default("default"),
    Protected("protected");
    private final String description;

    private Visibility(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
