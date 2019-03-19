package talkapp.org.talkappmobile.activity.listener;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface OnPracticeWordSetListener {
    void onInitialiseExperience(WordSet wordSet);

    void onSentencesFound(Sentence sentence, Word2Tokens word);

    void onAnswerEmpty();

    void onAccuracyTooLowError();

    void onUpdateProgress(WordSet wordSet);

    void onUpdateProgress(WordSet wordSet, int maxTrainingProgress);

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
}
