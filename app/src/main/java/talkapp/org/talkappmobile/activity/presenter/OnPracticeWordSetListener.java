package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;

public interface OnPracticeWordSetListener {
    void onInitialiseExperience();

    void onSentencesFound(Sentence sentence);

    void onAnswerEmpty();

    void onSpellingOrGrammarError(List<GrammarError> errors);

    void onAccuracyTooLowError();

    void onUpdateProgress(int currentTrainingExperience);

    void onTrainingFinished();

    void onRightAnswer();

    void onStartPlaying();

    void onStopPlaying();

    void onSnippetRecorded(long speechLength, int maxSpeechLengthMillis);

    void onStartRecording();

    void onStopRecording();

    void onStopRecognition();

    void onGotRecognitionResult(VoiceRecognitionResult result);
}
