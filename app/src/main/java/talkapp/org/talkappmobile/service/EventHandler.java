package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface EventHandler {
    void onWordSetNotFound(String wordSetId);

    void onException(Exception e);

    void onDestroy();

    void onSentenceNotFound(String words, WordSet wordSet);

    void onNewSentenceGot(final Sentence sentence, String combination, WordSet wordSet);

    void onNextTask(AnswerCheckingResult result, WordSet wordSet);

    void onWin(AnswerCheckingResult result, WordSet wordSet);

    void onErrors(AnswerCheckingResult result, WordSet wordSet);
}
