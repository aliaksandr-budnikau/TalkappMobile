package org.talkappmobile.model;

import android.support.annotation.NonNull;

public abstract class Task {
    private final String title;
    private final String description;

    public Task(@NonNull String title, @NonNull String description) {
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