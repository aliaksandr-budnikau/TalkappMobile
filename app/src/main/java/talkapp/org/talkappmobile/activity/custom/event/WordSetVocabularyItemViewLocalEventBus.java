package talkapp.org.talkappmobile.activity.custom.event;

import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;

public interface WordSetVocabularyItemViewLocalEventBus {
    void onMessageEvent(PhraseTranslationInputPopupOkClickedEM event);

    void onMessageEvent(NewWordIsEmptyEM event);

    void onMessageEvent(NewWordTranslationWasNotFoundEM event);

    void onMessageEvent(NewWordSentencesWereNotFoundEM event);

    void onMessageEvent(NewWordSentencesWereFoundEM event);

    void onMessageEvent(PhraseTranslationInputWasValidatedSuccessfullyEM event);
}