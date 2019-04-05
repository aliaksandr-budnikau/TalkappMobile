package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.model.WordTranslation;

@EViewGroup(R.layout.word_set_vocabulary_item)
public class WordSetVocabularyItemView extends RelativeLayout {
    @Bean(SpeakerBean.class)
    Speaker speaker;
    @ViewById(R.id.word)
    TextView word;
    @ViewById(R.id.translation)
    TextView translation;
    private WordTranslation wordTranslation;

    public WordSetVocabularyItemView(Context context) {
        super(context);
    }

    public WordSetVocabularyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordSetVocabularyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void pronounceTranslation(WordTranslation item) {
        if (item == null) {
            return;
        }
        try {
            speaker.speak(item.getWord());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void setModel(WordTranslation wordTranslation) {
        this.wordTranslation = wordTranslation;
    }

    public void refreshModel() {
        word.setText(wordTranslation.getWord());
        translation.setText(wordTranslation.getTranslation());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pronounceTranslation(wordTranslation);
            }
        });
    }
}