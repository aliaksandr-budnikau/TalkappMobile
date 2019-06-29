package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Task;

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