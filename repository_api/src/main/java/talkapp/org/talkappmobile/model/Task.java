package talkapp.org.talkappmobile.model;

public abstract class Task {
    private final String title;
    private final String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void start() {
        // TODO remove the action out of here
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}