package talkapp.org.talkappmobile.activity.interactor.impl;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

class PracticeWordSetInteractorDecorator implements PracticeWordSetInteractor {
    private final PracticeWordSetInteractor interactor;

    public PracticeWordSetInteractorDecorator(PracticeWordSetInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public Sentence getCurrentSentence() {
        return interactor.getCurrentSentence();
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        interactor.initialiseExperience(listener);
    }

    @Override
    public void initialiseWordsSequence(OnPracticeWordSetListener listener) {
        interactor.initialiseWordsSequence(listener);
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        return interactor.peekAnyNewWordByWordSetId();
    }

    @Override
    public void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener) {
        interactor.initialiseSentence(word, listener);
    }

    @Override
    public boolean checkAnswer(String answer, OnPracticeWordSetListener listener) {
        return interactor.checkAnswer(answer, listener);
    }

    @Override
    public void playVoice(OnPracticeWordSetListener listener) {
        interactor.playVoice(listener);
    }

    @Override
    public void scoreSentence(Sentence sentence, SentenceContentScore score, OnPracticeWordSetListener listener) {
        interactor.scoreSentence(sentence, score, listener);
    }

    @Override
    public void changeSentence(OnPracticeWordSetListener listener) {
        interactor.changeSentence(listener);
    }

    @Override
    public void changeSentence(Word2Tokens currentWord, List<Sentence> sentences, OnPracticeWordSetListener listener) {
        interactor.changeSentence(currentWord, sentences, listener);
    }

    @Override
    public void findSentencesForChange(Word2Tokens currentWord, OnPracticeWordSetListener listener) {
        interactor.findSentencesForChange(currentWord, listener);
    }

    @Override
    public void prepareOriginalTextClickEM(OnPracticeWordSetListener listener) {
        interactor.prepareOriginalTextClickEM(listener);
    }

    @Override
    public void refreshSentence(OnPracticeWordSetListener listener) {
        interactor.refreshSentence(listener);
    }

    @Override
    public void saveCurrentWordSet(WordSet wordSet) {
        interactor.saveCurrentWordSet(wordSet);
    }

    @Override
    public void resetSentenceState(OnPracticeWordSetListener listener) {
        interactor.resetSentenceState(listener);
    }

    @Override
    public void markAnswerHasBeenSeen() {
        interactor.markAnswerHasBeenSeen();
    }

    @Override
    public void saveVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener) {
        interactor.saveVoice(voiceRecordUri, listener);
    }

    @Override
    public void changeStrategy(PracticeWordSetInteractorStrategy state) {
        interactor.changeStrategy(state);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        interactor.finishWord(listener);
    }
}