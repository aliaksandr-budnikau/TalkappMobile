package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.custom.WordSetsListItemView;
import talkapp.org.talkappmobile.activity.custom.WordSetsListItemView_;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSetListAdapter extends ArrayAdapter<WordSet> {
    @Inject
    WordSetExperienceService experienceService;

    public WordSetListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
        DIContextUtils.get().inject(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WordSetsListItemView itemView;
        if (convertView == null) {
            itemView = WordSetsListItemView_.build(getContext());
        } else {
            itemView = (WordSetsListItemView) convertView;
        }
        WordSet wordSet = this.getItem(position);
        itemView.setModel(wordSet, experienceService.findById(wordSet.getId()));
        if (wordSet.getId() == 0) {
            itemView.hideProgress();
        }
        itemView.refreshModel();
        return itemView;
    }
}
