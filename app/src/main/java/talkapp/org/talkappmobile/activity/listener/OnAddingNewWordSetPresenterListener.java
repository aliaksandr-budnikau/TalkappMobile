package talkapp.org.talkappmobile.activity.listener;

import org.talkappmobile.model.WordSet;

public interface OnAddingNewWordSetPresenterListener {
    void onSentencesWereNotFound(int wordIndex);

    void onSentencesWereFound(int wordIndex);

    void onSubmitSuccessfully(WordSet wordSet);

    void onWordIsEmpty(int wordIndex);

    void onWordIsDuplicate(int wordIndex);

    void onTranslationWasNotFound(int wordIndex);
}