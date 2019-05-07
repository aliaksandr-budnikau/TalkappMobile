package talkapp.org.talkappmobile.activity.view;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface PracticeWordSetView {

    void showNextButton();

    void hideNextButton();

    void showPleaseWaitProgressBar();

    void hidePleaseWaitProgressBar();

    void showCheckButton();

    void hideCheckButton();

    void setRightAnswer(String text);

    void setProgress(int progress);

    void showMessageAnswerEmpty();

    void showMessageAccuracyTooLow();

    void showCongratulationMessage();

    void closeActivity();

    void openAnotherActivity();

    void setEnablePronounceRightAnswerButton(boolean value);

    void setEnableVoiceRecButton(boolean value);

    void setEnableCheckButton(boolean value);

    void setEnableNextButton(boolean value);

    void setAnswerText(String text);

    void setEnableRightAnswerTextView(boolean value);

    void showScoringSuccessfulMessage();

    void showScoringUnsuccessfulMessage();

    void showSentenceChangedSuccessfullyMessage();

    void onSentencesFound(Sentence sentence, Word2Tokens word);

    void onEnableRepetitionMode();

    void onExerciseGotAnswered();

    void onUpdateUserExp(double expScore);

    void onNoSentencesToChange();

    void onGotSentencesToChange(List<Sentence> sentences);
}