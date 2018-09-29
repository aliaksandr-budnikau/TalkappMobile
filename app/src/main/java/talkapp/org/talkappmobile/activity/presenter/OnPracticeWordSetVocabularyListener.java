package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface OnPracticeWordSetVocabularyListener {
    void onWordSetVocabularyFound(List<WordTranslation> wordTranslations);
}