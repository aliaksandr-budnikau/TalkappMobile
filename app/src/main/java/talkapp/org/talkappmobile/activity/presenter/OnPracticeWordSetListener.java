package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;

public interface OnPracticeWordSetListener {
    void onInitialiseExperience();

    void onSentencesFound(Sentence sentence, String word);

    void onAnswerEmpty();

    void onSpellingOrGrammarError(List<GrammarError> errors);

    void onAccuracyTooLowError();

    void onUpdateProgress(int currentTrainingExperience);

    void onTrainingHalfFinished();

    void onTrainingFinished();

    void onRightAnswer();

    void onStartPlaying();

    void onStopPlaying();

    void onGotRecognitionResult(List<String> result);
}
