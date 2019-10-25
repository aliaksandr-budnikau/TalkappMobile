package talkapp.org.talkappmobile.activity.interactor;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface PracticeWordSetInteractor {
    Sentence getCurrentSentence();

    void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener);

    void initialiseWordsSequence(int wordSetId, OnPracticeWordSetListener listener);

    Word2Tokens peekAnyNewWordByWordSetId(int wordSetId);

    void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener);

    boolean checkAnswer(String answer, WordSet wordSet, Sentence currentSentence, boolean answerHasBeenSeen, OnPracticeWordSetListener listener);

    void playVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener);

    void scoreSentence(Sentence sentence, SentenceContentScore score, OnPracticeWordSetListener listener);

    void changeSentence(int wordSetId, OnPracticeWordSetListener listener);

    void changeSentence(Word2Tokens currentWord, List<Sentence> sentences, OnPracticeWordSetListener listener);

    void findSentencesForChange(Word2Tokens currentWord, OnPracticeWordSetListener listener);

    void prepareOriginalTextClickEM(OnPracticeWordSetListener listener);
}