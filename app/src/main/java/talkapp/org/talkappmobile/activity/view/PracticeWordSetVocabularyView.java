package talkapp.org.talkappmobile.activity.view;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface PracticeWordSetVocabularyView {
    void setWordSetVocabularyList(List<WordTranslation> wordTranslations);

    void onInitializeBeginning();

    void onInitializeEnd();

    void onLocalCacheIsEmptyException(LocalCacheIsEmptyException e);
}
