package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.model.WordTranslation;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@EViewGroup(R.layout.word_set_vocabulary_item)
public class WordSetVocabularyItemView extends RelativeLayout {
    public static final int MIN_LINES = 2;

    @ViewById(R.id.word)
    TextView wordTextField;
    @ViewById(R.id.translation)
    TextView translationTextView;

    public WordSetVocabularyItemView(Context context) {
        super(context);
    }

    public WordSetVocabularyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordSetVocabularyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void refreshModel(WordTranslation wordTranslation, boolean expanded) {
        if (isNotEmpty(wordTranslation.getWord())) {
            wordTextField.setText(wordTranslation.getWord());
            translationTextView.setText(wordTranslation.getTranslation());
        }
        if (expanded) {
            if (translationTextView.getLineCount() != 0) {
                translationTextView.setLines(translationTextView.getLineCount());
            }
            translationTextView.setMinLines(MIN_LINES);
        } else {
            translationTextView.setLines(MIN_LINES);
        }
    }
}