package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import talkapp.org.talkappmobile.model.WordTranslation;

@EBean
public class PracticeWordSetVocabularyListAdapter extends ArrayAdapter<WordTranslation> {

    @RootContext
    Context context;

    public PracticeWordSetVocabularyListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PracticeWordSetVocabularyListItemView itemView;
        if (convertView == null) {
            itemView = PracticeWordSetVocabularyListItemView_.build(context);
        } else {
            itemView = (PracticeWordSetVocabularyListItemView) convertView;
        }
        itemView.setModel(this.getItem(position));
        itemView.refreshModel();
        return itemView;
    }
}
