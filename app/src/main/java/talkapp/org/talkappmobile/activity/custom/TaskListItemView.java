package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.talkappmobile.model.Task;

import talkapp.org.talkappmobile.R;

@EViewGroup(R.layout.task_list_item)
public class TaskListItemView extends RelativeLayout {
    public static final int MIN_LINES = 2;
    @ViewById(R.id.title)
    TextView title;
    @ViewById(R.id.description)
    TextView description;
    @NonNull
    private Task task;

    public TaskListItemView(Context context) {
        super(context);
    }

    public TaskListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setModel(Task task) {
        this.task = task;
    }

    public void refreshModel(boolean expanded) {
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        if (expanded) {
            if (description.getLineCount() != 0) {
                description.setLines(description.getLineCount());
            }
            description.setMinLines(MIN_LINES);
        } else {
            description.setLines(MIN_LINES);
        }
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                task.start();
                return true;
            }
        });
    }
}