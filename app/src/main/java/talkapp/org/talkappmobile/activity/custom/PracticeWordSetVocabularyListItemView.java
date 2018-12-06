package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.model.WordTranslation;

@EViewGroup(R.layout.row_word_translations_list)
public class PracticeWordSetVocabularyListItemView extends RelativeLayout {

    @ViewById(R.id.word)
    TextView word;
    @ViewById(R.id.translation)
    TextView translation;
    private WordTranslation wordTranslation;

    public PracticeWordSetVocabularyListItemView(Context context) {
        super(context);
    }

    public PracticeWordSetVocabularyListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PracticeWordSetVocabularyListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setModel(WordTranslation wordTranslation) {
        this.wordTranslation = wordTranslation;
    }

    public void refreshModel() {
        word.setText(wordTranslation.getWord());
        translation.setText(wordTranslation.getTranslation());
    }
}