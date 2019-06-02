package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.model.Task;

@EViewGroup(R.layout.task_list_item)
public class TaskListItemView extends RelativeLayout {
    @ViewById(R.id.title)
    TextView title;
    @ViewById(R.id.description)
    TextView description;
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

    public void refreshModel() {
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Tasked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}