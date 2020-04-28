package talkapp.org.talkappmobile.view;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface PracticeWordSetVocabularyView {
    void setWordSetVocabularyList(List<WordTranslation> wordTranslations);

    void onInitializeBeginning();

    void onInitializeEnd();

    void onLocalCacheIsEmptyException(RuntimeException  e);

    void onUpdateNotCustomWordSet();

    void onUpdateCustomWordSetFinished();
}
