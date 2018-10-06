package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class PracticeWordSetViewStrategy {
    private final PracticeWordSetView view;
    @Inject
    WordSetExperienceUtils experienceUtils;
    @Inject
    TextUtils textUtils;

    public PracticeWordSetViewStrategy(PracticeWordSetView view) {
        this.view = view;
        DIContext.get().inject(this);
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

    public void onUpdateProgress(WordSetExperience exp, int currentTrainingExperience) {
        int progress = experienceUtils.getProgress(currentTrainingExperience, exp.getMaxTrainingExperience());
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

    public void onStopPlaying() {
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onSnippetRecorded(long speechLength, int maxSpeechLengthMillis) {
        view.setRecProgress((int) (((double) speechLength / maxSpeechLengthMillis) * 100));
    }

    public void onStartRecording(Sentence sentence, String word) {
        view.setRecProgress(0);
        view.showRecProgress();
        view.setEnablePlayButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
        String hiddenRightAnswer = hideRightAnswer(sentence, word);
        view.setRightAnswer(hiddenRightAnswer);
        view.setEnableRightAnswer(false);
    }

    public void onStopRecording() {
        view.setEnablePlayButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
        view.setEnableRightAnswer(true);
    }

    public void onStopRecognition() {
        view.hideRecProgress();
        view.setRecProgress(0);
    }

    public void onGotRecognitionResult(Sentence sentence, VoiceRecognitionResult result) {
        String textWithUpper = textUtils.toUpperCaseFirstLetter(result.getVariant().get(0));
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
}