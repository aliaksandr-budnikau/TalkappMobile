package talkapp.org.talkappmobile.activity.view;

import talkapp.org.talkappmobile.model.WordSet;

public interface AddingNewWordSetFragmentView {

    void markSentencesWereNotFound(int wordIndex);

    void markSentencesWereFound(int wordIndex);

    void submitSuccessfully(WordSet wordSet);

    void markWordIsEmpty(int wordIndex);

    void markWordIsDuplicate(int wordIndex);

    void markTranslationWasNotFound(int wordIndex);
}