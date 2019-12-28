package talkapp.org.talkappmobile.activity.presenter.decorator;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface IPracticeWordSetPresenter {

    void nextButtonClick();

    void initialise(WordSet wordSet);

    void prepareOriginalTextClickEM();

    void playVoiceButtonClick();

    void disableButtonsDuringPronunciation();

    void refreshCurrentWord();

    void enableButtonsAfterPronunciation();

    void checkRightAnswerCommandRecognized(WordSet wordSet);

    void checkAnswerButtonClick(String answer, WordSet wordSet);

    void gotRecognitionResult(List<String> suggestedWords);

    void voiceRecorded(Uri data);

    void scoreSentence(SentenceContentScore score, Sentence sentence);

    void findSentencesForChange(Word2Tokens word);

    void refreshSentence();

    void changeSentence(List<Sentence> sentences, Word2Tokens word);

    void markAnswerHasBeenSeen();

    Sentence getCurrentSentence();

    void changeSentence(int wordSetId);
}