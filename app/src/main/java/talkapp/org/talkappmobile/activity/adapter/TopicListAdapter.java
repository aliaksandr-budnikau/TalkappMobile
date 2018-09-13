package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class TopicListAdapter extends ArrayAdapter<Topic> {
    @Inject
    WordSetExperienceUtils experienceUtils;

    public TopicListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
        DIContext.get().inject(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Topic topic = this.getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_topics_list, parent, false);
        TextView topicRow = convertView.findViewById(R.id.topicRow);
        topicRow.setText(topic.getName());

        return convertView;
    }
}
