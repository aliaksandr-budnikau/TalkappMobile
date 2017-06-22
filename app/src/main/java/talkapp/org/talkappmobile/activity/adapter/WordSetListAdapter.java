package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.ExerciseActivity;
import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSetListAdapter extends ArrayAdapter<WordSet> implements AdapterView.OnItemClickListener {
    private final Context context;

    public WordSetListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
    }

    @Override
    public void addAll(@NonNull Collection<? extends WordSet> data) {
        super.addAll(data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WordSet wordSet = this.getItem(position);

        Intent intent = new Intent(context, ExerciseActivity.class);
        intent.putExtra(ExerciseActivity.WORD_SET_MAPPING, wordSet);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WordSet wordSet = this.getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_word_sets_list, parent, false);
        TextView wordSetRow = (TextView) convertView.findViewById(R.id.wordSetRow);
        String label = StringUtils.joinWith(", ", wordSet.getWords().toArray());
        wordSetRow.setText(label);
        return convertView;
    }
}
