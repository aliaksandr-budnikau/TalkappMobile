package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface OriginalTextTextViewPresenter {
    void setModel(Sentence sentence);

    void unlock();

    void refresh();

    void prepareSentencesForPicking(List<Sentence> sentences, List<Sentence> alreadyPickedSentences, Word2Tokens word);

    void prepareDialog(Word2Tokens word, String anotherSentenceOption, String poorSentenceOption, String corruptedSentenceOption, String insultSentenceOption);

    void lock();

    void changeSentence(Word2Tokens word);

    Sentence getSentence();
}
