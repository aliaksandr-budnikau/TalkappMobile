package talkapp.org.talkappmobile.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface OnPracticeWordSetListener {
    void onInitialiseExperience(WordSet wordSet);

    void onSentencesFound(Sentence sentence, Word2Tokens word);

    void onAnswerEmpty();

    void onAccuracyTooLowError();

    void onUpdateProgress(WordSet wordSet);

    void onTrainingHalfFinished(Sentence sentence);

    void onTrainingFinished();

    void onRightAnswer(Sentence sentence);

    void onStartPlaying();

    void onStopPlaying();

    void onEnableRepetitionMode();

    void onScoringUnsuccessful();

    void onScoringSuccessful();

    void onSentenceChanged();

    void onUpdateUserExp(double expScore);

    void onNoSentencesToChange();

    void onGotSentencesToChange(List<Sentence> sentences, List<Sentence> alreadyPickedSentences, Word2Tokens word);

    void onForgottenAgain(int counter);

    void onOriginalTextClickEMPrepared(Word2Tokens word);

    void onSentencesFound();
}
