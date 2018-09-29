package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.model.Sentence;

interface PracticeWordSetView {

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

    void setEnableVoiceRecButton(boolean value);

    void setEnablePlayButton(boolean value);

    void setEnableCheckButton(boolean value);

    void setEnableNextButton(boolean value);

    void showRecProgress();

    void setRecProgress(int value);

    void hideRecProgress();

    void setAnswerText(String text);

    void setEnableRightAnswer(boolean value);

    void showSpellingOrGrammarErrorPanel(String errorMessage);

    void hideSpellingOrGrammarErrorPanel();
}