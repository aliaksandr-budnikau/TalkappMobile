package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSetListAdapter extends ArrayAdapter<WordSet> {
    @Inject
    WordSetExperienceUtils experienceUtils;

    public WordSetListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
        DIContext.get().inject(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WordSet wordSet = this.getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_word_sets_list, parent, false);
        TextView wordSetRow = convertView.findViewById(R.id.wordSetRow);
        String label = StringUtils.joinWith(", ", wordSet.getWords().toArray());
        wordSetRow.setText(label);

        ProgressBar wordSetProgress = convertView.findViewById(R.id.wordSetProgress);
        if (wordSet.getExperience() == null) {
            wordSetProgress.setProgress(0);
        } else {
            wordSetProgress.setProgress(experienceUtils.getProgress(wordSet.getExperience()));
        }

        return convertView;
    }
}
