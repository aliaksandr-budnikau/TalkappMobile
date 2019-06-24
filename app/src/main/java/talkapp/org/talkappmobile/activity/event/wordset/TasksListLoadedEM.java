package talkapp.org.talkappmobile.activity.event.wordset;

import org.talkappmobile.model.Task;

public class TasksListLoadedEM {
    private Task[] tasks;

    public TasksListLoadedEM(Task[] tasks) {
        this.tasks = tasks;
    }

    public Task[] getTasks() {
        return tasks;
    }
}