package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class PracticeWordSetViewStrategy {
    private final PracticeWordSetView view;
    private final WordSetExperienceUtils experienceUtils;
    private final TextUtils textUtils;

    public PracticeWordSetViewStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        this.view = view;
        this.textUtils = textUtils;
        this.experienceUtils = experienceUtils;
    }

    public void onInitialiseExperience(WordSetExperience exp) {
        int progress = experienceUtils.getProgress(exp.getTrainingExperience(), exp.getMaxTrainingExperience());
        view.setProgress(progress);
    }

    public void onSentencesFound(Sentence sentence, String word) {
        view.hideNextButton();
        view.showCheckButton();
        view.setOriginalText(sentence.getTranslations().get("russian"));
        String hiddenRightAnswer = hideRightAnswer(sentence, word);
        view.setRightAnswer(hiddenRightAnswer);
        view.setAnswerText("");
    }

    public void onAnswerEmpty() {
        view.showMessageAnswerEmpty();
    }

    public void onSpellingOrGrammarError(List<GrammarError> errors) {
        view.showMessageSpellingOrGrammarError();
        view.hideSpellingOrGrammarErrorPanel();
        for (GrammarError error : errors) {
            String errorMessage = textUtils.buildSpellingGrammarErrorMessage(error);
            view.showSpellingOrGrammarErrorPanel(errorMessage);
        }
    }

    public void onAccuracyTooLowError() {
        view.showMessageAccuracyTooLow();
        view.hideSpellingOrGrammarErrorPanel();
    }

    public void onUpdateProgress(WordSetExperience exp) {
        int progress = experienceUtils.getProgress(exp.getTrainingExperience(), exp.getMaxTrainingExperience());
        view.setProgress(progress);
    }

    public void onTrainingFinished() {
        view.showCongratulationMessage();
        view.closeActivity();
        view.openAnotherActivity();
    }

    public void onRightAnswer(Sentence sentence) {
        view.setRightAnswer(sentence.getText());
        view.showNextButton();
        view.hideCheckButton();
        view.hideSpellingOrGrammarErrorPanel();
    }

    public void onStartPlaying() {
        view.setEnableVoiceRecButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onStartSpeaking() {
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableVoiceRecButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onStopSpeaking() {
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onStopPlaying() {
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onGotRecognitionResult(Sentence sentence, List<String> result) {
        String textWithUpper = textUtils.toUpperCaseFirstLetter(result.get(0));
        String textWithLastSymbol = textUtils.appendLastSymbol(textWithUpper, sentence.getText());
        view.setAnswerText(textWithLastSymbol);
    }

    public void rightAnswerTouched(Sentence sentence) {
        view.setRightAnswer(sentence.getText());
    }

    public void rightAnswerUntouched(Sentence sentence, String word) {
        String hiddenRightAnswer = hideRightAnswer(sentence, word);
        view.setRightAnswer(hiddenRightAnswer);
    }

    public PracticeWordSetView getView() {
        return view;
    }

    protected String hideRightAnswer(Sentence sentence, String word) {
        return textUtils.screenTextWith(sentence.getText());
    }

    public void onTrainingHalfFinished() {

    }

    public void onNextButtonStart() {
        view.setEnableRightAnswerTextView(false);
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableNextButton(false);
    }

    public void onNextButtonFinish() {
        view.setEnableRightAnswerTextView(true);
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableNextButton(true);
    }

    public void onCheckAnswerStart() {
        view.setEnableRightAnswerTextView(false);
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableCheckButton(false);
    }

    public void onCheckAnswerFinish() {
        view.setEnableRightAnswerTextView(true);
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableCheckButton(true);
    }
}