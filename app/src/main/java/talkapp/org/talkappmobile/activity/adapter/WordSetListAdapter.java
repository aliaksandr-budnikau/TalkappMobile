package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.Collection;

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
}
