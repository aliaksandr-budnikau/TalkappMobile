package talkapp.org.talkappmobile.view;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.LocalCacheIsEmptyException;

public interface PracticeWordSetVocabularyView {
    void setWordSetVocabularyList(List<WordTranslation> wordTranslations);

    void onInitializeBeginning();

    void onInitializeEnd();

    void onLocalCacheIsEmptyException(LocalCacheIsEmptyException e);

    void onUpdateNotCustomWordSet();

    void onUpdateCustomWordSetFinished();
}
