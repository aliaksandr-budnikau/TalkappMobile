package talkapp.org.talkappmobile.activity.interactor;

import android.net.Uri;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface PracticeWordSetInteractor {
    Sentence getCurrentSentence(int wordSetId);

    void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener);

    void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener);

    Word2Tokens peekAnyNewWordByWordSetId(int wordSetId);

    void initialiseSentence(Word2Tokens word, int wordSetId, OnPracticeWordSetListener listener);

    boolean checkAnswer(String answer, WordSet wordSet, Sentence currentSentence, boolean answerHasBeenSeen, OnPracticeWordSetListener listener);

    void playVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener);

    void pronounceRightAnswer(Sentence sentence, OnPracticeWordSetListener listener);

    void scoreSentence(Sentence sentence, SentenceContentScore score, OnPracticeWordSetListener listener);

    void changeSentence(int wordSetId, OnPracticeWordSetListener listener);

    void initialiseSentence(WordSet wordSet);
}