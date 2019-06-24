package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import org.talkappmobile.model.WordTranslation;

public interface OnPracticeWordSetVocabularyListener {
    void onWordSetVocabularyFound(List<WordTranslation> wordTranslations);
}