package io.snw.tutorial;

public class MapPlayerTutorial {

    private String tutorial;
    private boolean seen;

    public MapPlayerTutorial(String tutorial, boolean seen) {
        this.tutorial = tutorial;
        this.seen = seen;
    }

    public String getTutorial() {
        return this.tutorial;
    }

    public boolean getSeen() {
        return this.seen;
    }
}
