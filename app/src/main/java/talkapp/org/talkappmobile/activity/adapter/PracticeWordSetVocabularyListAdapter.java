package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyListAdapter extends ArrayAdapter<WordTranslation> {

    public PracticeWordSetVocabularyListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WordTranslation translation = this.getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_word_translations_list, parent, false);

        TextView wordTextView = convertView.findViewById(R.id.word);
        wordTextView.setText(translation.getWord());

        TextView translationTextView = convertView.findViewById(R.id.translation);
        translationTextView.setText(translation.getTranslation());

        return convertView;
    }
}
