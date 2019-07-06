package org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import org.talkappmobile.R;
import org.talkappmobile.model.Topic;

@EViewGroup(R.layout.row_topics_list)
public class TopicListItemView extends RelativeLayout {

    @ViewById(R.id.topicRow)
    TextView topicRow;
    private Topic topic;

    public TopicListItemView(Context context) {
        super(context);
    }

    public TopicListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopicListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setModel(Topic topic) {
        this.topic = topic;
    }

    public void refreshModel() {
        topicRow.setText(topic.getName());
    }
}