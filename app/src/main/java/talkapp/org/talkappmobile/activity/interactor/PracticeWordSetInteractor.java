package talkapp.org.talkappmobile.activity.interactor;

import android.net.Uri;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface PracticeWordSetInteractor {
    Sentence getCurrentSentence(int wordSetId);

    void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener);

    void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener);

    Word2Tokens peekAnyNewWordByWordSetId(int wordSetId);

    void initialiseSentence(Word2Tokens word, int wordSetId, OnPracticeWordSetListener listener);

    void checkAnswer(String answer, WordSet wordSet, Sentence currentSentence, OnPracticeWordSetListener listener);

    void playVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener);

    void rightAnswerUntouched(int wordSetId, OnPracticeWordSetListener listener);

    void pronounceRightAnswer(int wordSetId, OnPracticeWordSetListener listener);
}