package talkapp.org.talkappmobile.activity;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSetExperience;

interface PracticeWordSetView {

    void showNextButton();

    void hideNextButton();

    void showCheckButton();

    void hideCheckButton();

    void setRightAnswer(Sentence sentence);

    void setProgress(WordSetExperience exp);

    void setOriginalText(Sentence sentence);

    void setHiddenRightAnswer(Sentence sentence);

    void setAnswerText(Sentence sentence) ;

    void showMessageAnswerEmpty();

    void showMessageSpellingOrGrammarError();

    void showMessageAccuracyTooLow();

    void updateProgress(WordSetExperience experience, int currentTrainingExperience);

    void showCongratulationMessage();

    void closeActivity();

    void openAnotherActivity();
}