package talkapp.org.talkappmobile.activity.view;

import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import java.util.List;

public interface PracticeWordSetVocabularyView {
    void setWordSetVocabularyList(List<WordTranslation> wordTranslations);

    void onInitializeBeginning();

    void onInitializeEnd();

    void onLocalCacheIsEmptyException(LocalCacheIsEmptyException e);
}
