package talkapp.org.talkappmobile.interactor;

import java.util.List;

import talkapp.org.talkappmobile.interactor.impl.PracticeWordSetInteractorStrategy;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface PracticeWordSetInteractor {
    Sentence getCurrentSentence();

    void initialiseExperience(OnPracticeWordSetListener listener);

    void initialiseWordsSequence(OnPracticeWordSetListener listener);

    Word2Tokens peekAnyNewWordByWordSetId();

    void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener);

    boolean checkAnswer(String answer, OnPracticeWordSetListener listener);

    void scoreSentence(Sentence sentence, SentenceContentScore score, OnPracticeWordSetListener listener);

    void changeSentence(OnPracticeWordSetListener listener);

    void changeSentence(Word2Tokens currentWord, List<Sentence> sentences, OnPracticeWordSetListener listener);

    void findSentencesForChange(Word2Tokens currentWord, OnPracticeWordSetListener listener);

    void prepareOriginalTextClickEM(OnPracticeWordSetListener listener);

    void refreshSentence(OnPracticeWordSetListener listener);

    void saveCurrentWordSet(WordSet wordSet);

    void resetSentenceState(OnPracticeWordSetListener listener);

    void markAnswerHasBeenSeen();

    void changeStrategy(PracticeWordSetInteractorStrategy strategy);

    void finishWord(OnPracticeWordSetListener listener);
}