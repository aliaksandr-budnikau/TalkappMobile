package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.activity.event.wordset.TasksListLoadedEM;
import org.talkappmobile.model.Task;

@EView
public class TasksListView extends RecyclerView {
    @EventBusGreenRobot
    EventBus eventBus;

    public TasksListView(Context context) {
        super(context);
    }

    public TasksListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        addItemDecoration(itemDecor);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TasksListLoadedEM event) {
        this.setAdapter(new TasksAdapter(event.getTasks()));
    }

    private static class TasksAdapter extends Adapter<TasksAdapter.ViewHolder> {
        private final TaskExpandable[] tasks;

        private TasksAdapter(Task[] tasks) {
            this.tasks = new TaskExpandable[tasks.length];
            for (int i = 0; i < tasks.length; i++) {
                this.tasks[i] = new TaskExpandable(tasks[i]);
            }
        }

        @Override
        public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = TaskListItemView_.build(parent.getContext());
            return new ViewHolder((TaskListItemView) v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(this.tasks[position], position);
        }

        @Override
        public int getItemCount() {
            return tasks.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TaskListItemView view;

            private ViewHolder(TaskListItemView view) {
                super(view);
                this.view = view;
            }

            void bind(final TaskExpandable task, final int position) {
                view.setModel(task.getTask());
                view.refreshModel(task.isExpanded());
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean expanded = task.isExpanded();
                        task.setExpanded(!expanded);
                        notifyItemChanged(position);
                    }
                });
            }
        }
    }

    private static class TaskExpandable {
        private Task task;
        private boolean expanded;

        TaskExpandable(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }
    }
}