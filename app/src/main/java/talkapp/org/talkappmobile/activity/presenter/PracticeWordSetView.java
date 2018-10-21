package talkapp.org.talkappmobile.activity.presenter;

public interface PracticeWordSetView {

    void showNextButton();

    void hideNextButton();

    void showCheckButton();

    void hideCheckButton();

    void setRightAnswer(String text);

    void setProgress(int progress);

    void setOriginalText(String text);

    void showMessageAnswerEmpty();

    void showMessageSpellingOrGrammarError();

    void showMessageAccuracyTooLow();

    void showCongratulationMessage();

    void closeActivity();

    void openAnotherActivity();

    void setEnablePronounceRightAnswerButton(boolean value);

    void setEnableVoiceRecButton(boolean value);

    void setEnableCheckButton(boolean value);

    void setEnableNextButton(boolean value);

    void setAnswerText(String text);

    void showSpellingOrGrammarErrorPanel(String errorMessage);

    void hideSpellingOrGrammarErrorPanel();
}