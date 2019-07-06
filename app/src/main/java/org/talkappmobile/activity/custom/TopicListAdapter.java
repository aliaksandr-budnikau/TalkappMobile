package org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import org.talkappmobile.model.Topic;

/**
 * @author Budnikau Aliaksandr
 */
@EBean
public class TopicListAdapter extends ArrayAdapter<Topic> {

    @RootContext
    Context context;

    public TopicListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TopicListItemView itemView;
        if (convertView == null) {
            itemView = TopicListItemView_.build(context);
        } else {
            itemView = (TopicListItemView) convertView;
        }
        itemView.setModel(this.getItem(position));
        itemView.refreshModel();
        return itemView;
    }
}