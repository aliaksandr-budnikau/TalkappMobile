package talkapp.org.talkappmobile.events;

import org.talkappmobile.model.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class TasksListLoadedEM {
    @NonNull
    private Task[] tasks;
}