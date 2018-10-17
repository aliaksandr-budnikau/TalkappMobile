package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface OnPracticeWordSetListener {
    void onInitialiseExperience(WordSetExperience exp);

    void onSentencesFound(Sentence sentence, String word);

    void onAnswerEmpty();

    void onSpellingOrGrammarError(List<GrammarError> errors);

    void onAccuracyTooLowError();

    void onUpdateProgress(WordSetExperience exp);

    void onTrainingHalfFinished(Sentence sentence);

    void onTrainingFinished();

    void onRightAnswer(Sentence sentence);

    void onStartPlaying();

    void onStopPlaying();
}
