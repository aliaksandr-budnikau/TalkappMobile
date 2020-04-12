package talkapp.org.talkappmobile.events;

import androidx.annotation.NonNull;

import talkapp.org.talkappmobile.model.Task;

public class TasksListLoadedEM {
    @NonNull
    private Task[] tasks;

    public TasksListLoadedEM(@NonNull Task[] tasks) {
        this.tasks = tasks;
    }

    @NonNull
    public Task[] getTasks() {
        return tasks;
    }
}