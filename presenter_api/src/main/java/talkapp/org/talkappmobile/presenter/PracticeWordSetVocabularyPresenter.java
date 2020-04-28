package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface PracticeWordSetVocabularyPresenter {
    void initialise(WordSet wordSet);

    void updateCustomWordSet(int editedItemPosition, WordTranslation wordTranslation);
}
