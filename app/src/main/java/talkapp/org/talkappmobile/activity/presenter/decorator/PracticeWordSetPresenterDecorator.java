package talkapp.org.talkappmobile.activity.presenter.decorator;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

class PracticeWordSetPresenterDecorator implements IPracticeWordSetPresenter {
    private final IPracticeWordSetPresenter presenter;

    PracticeWordSetPresenterDecorator(IPracticeWordSetPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void nextButtonClick() {
        presenter.nextButtonClick();
    }

    @Override
    public void initialise(WordSet wordSet) {
        presenter.initialise(wordSet);
    }

    @Override
    public void prepareOriginalTextClickEM() {
        presenter.prepareOriginalTextClickEM();
    }

    @Override
    public void playVoiceButtonClick() {
        presenter.playVoiceButtonClick();
    }

    @Override
    public void disableButtonsDuringPronunciation() {
        presenter.disableButtonsDuringPronunciation();
    }

    @Override
    public void refreshCurrentWord() {
        presenter.refreshCurrentWord();
    }

    @Override
    public void enableButtonsAfterPronunciation() {
        presenter.enableButtonsAfterPronunciation();
    }

    @Override
    public void checkRightAnswerCommandRecognized(WordSet wordSet) {
        presenter.checkRightAnswerCommandRecognized(wordSet);
    }

    @Override
    public void checkAnswerButtonClick(String answer) {
        presenter.checkAnswerButtonClick(answer);
    }

    @Override
    public void gotRecognitionResult(List<String> suggestedWords) {
        presenter.gotRecognitionResult(suggestedWords);
    }

    @Override
    public void voiceRecorded(Uri data) {
        presenter.voiceRecorded(data);
    }

    @Override
    public void scoreSentence(SentenceContentScore score, Sentence sentence) {
        presenter.scoreSentence(score, sentence);
    }

    @Override
    public void findSentencesForChange(Word2Tokens word) {
        presenter.findSentencesForChange(word);
    }

    @Override
    public void refreshSentence() {
        presenter.refreshSentence();
    }

    @Override
    public void changeSentence(List<Sentence> sentences, Word2Tokens word) {
        presenter.changeSentence(sentences, word);
    }

    @Override
    public void markAnswerHasBeenSeen() {
        presenter.markAnswerHasBeenSeen();
    }

    @Override
    public Sentence getCurrentSentence() {
        return presenter.getCurrentSentence();
    }

    @Override
    public void changeSentence() {
        presenter.changeSentence();
    }
}