package talkapp.org.talkappmobile.activity.view;

import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import java.util.List;

public interface PracticeWordSetVocabularyView {
    void setWordSetVocabularyList(List<WordTranslation> wordTranslations);

    void onInitializeBeginning();

    void onInitializeEnd();

    void onLocalCacheIsEmptyException(LocalCacheIsEmptyException e);
}
