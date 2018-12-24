package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private static final String PLACEHOLDER_REGEX = "\\*\\*\\*";
    private static final String NOT_ENGLISH = "[^a-zA-Z]{5,}";
    private static final String ENGLISH = ".*[a-zA-Z]{3,}.*";
    @Mock
    private PracticeWordSetView view;
    private PracticeWordSetPresenter presenter;
    private PracticeWordSetExerciseService exerciseService;
    private WordSet wordSet;
    private StudyingPracticeWordSetInteractor interactor;
    private ViewStrategyFactory viewStrategyFactory;
    private Speaker speaker;

    @Before
    public void setup() {
        viewStrategyFactory = getClassForInjection().getViewStrategyFactory();
        interactor = getClassForInjection().getStudyingPracticeWordSetInteractor();
        exerciseService = getClassForInjection().getExerciseService();
        speaker = getClassForInjection().getSpeaker();
    }

    private void createPresenter(StudyingPracticeWordSetInteractor interactor, ViewStrategyFactory viewStrategyFactory) {
        int id = -1;
        wordSet = new WordSet();
        wordSet.setId(id);

        Word2Tokens age = new Word2Tokens("age");
        age.setTokens("age");

        Word2Tokens anniversary = new Word2Tokens("anniversary");
        anniversary.setTokens("anniversary");

        Word2Tokens birth = new Word2Tokens("birth");
        birth.setTokens("birth");

        wordSet.setWords(asList(age, anniversary, birth));
        wordSet.setTopicId("topicId");
        PracticeWordSetFirstCycleViewStrategy firstCycleViewStrategy = viewStrategyFactory.createPracticeWordSetFirstCycleViewStrategy(view);
        PracticeWordSetSecondCycleViewStrategy secondCycleViewStrategy = viewStrategyFactory.createPracticeWordSetSecondCycleViewStrategy(view);
        presenter = new PracticeWordSetPresenter(wordSet, interactor, firstCycleViewStrategy, secondCycleViewStrategy, view);
    }

    @Test
    public void testPracticeWordSet_completeOneSet() {
        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).showSentenceChangedSuccessfullyMessage();
        verify(view).setAnswerText("");

        verify(view).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).showSentenceChangedSuccessfullyMessage();
        verify(view).setAnswerText("");

        verify(view).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).showSentenceChangedSuccessfullyMessage();
        verify(view).setAnswerText("");

        verify(view).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
        verify(view).showSentenceChangeUnsupportedMessage();

        verify(view, times(0)).hideNextButton();
        verify(view, times(0)).showCheckButton();
        verify(view, times(0)).setOriginalText(matches(NOT_ENGLISH));
        verify(view, times(0)).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();
        verify(view, times(0)).setAnswerText("");

        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
        verify(view).showSentenceChangeUnsupportedMessage();

        verify(view, times(0)).hideNextButton();
        verify(view, times(0)).showCheckButton();
        verify(view, times(0)).setOriginalText(matches(NOT_ENGLISH));
        verify(view, times(0)).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();
        verify(view, times(0)).setAnswerText("");

        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
        verify(view).showSentenceChangeUnsupportedMessage();

        verify(view, times(0)).hideNextButton();
        verify(view, times(0)).showCheckButton();
        verify(view, times(0)).setOriginalText(matches(NOT_ENGLISH));
        verify(view, times(0)).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();
        verify(view, times(0)).setAnswerText("");

        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(100);
        verify(view).showCongratulationMessage();
        verify(view).closeActivity();
        verify(view).openAnotherActivity();
        verify(view).setEnableCheckButton(true);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_completeOneSetAndRestartAfterEacheStep() {
        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).showSentenceChangedSuccessfullyMessage();
        verify(view).setAnswerText("");

        verify(view).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(16);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).showSentenceChangedSuccessfullyMessage();
        verify(view).setAnswerText("");

        verify(view).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(33);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        verify(view).showSentenceChangedSuccessfullyMessage();
        verify(view).setAnswerText("");

        verify(view).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(50);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
        verify(view).showSentenceChangeUnsupportedMessage();

        verify(view, times(0)).hideNextButton();
        verify(view, times(0)).showCheckButton();
        verify(view, times(0)).setOriginalText(matches(NOT_ENGLISH));
        verify(view, times(0)).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();
        verify(view, times(0)).setAnswerText("");

        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(66);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
        verify(view).showSentenceChangeUnsupportedMessage();

        verify(view, times(0)).hideNextButton();
        verify(view, times(0)).showCheckButton();
        verify(view, times(0)).setOriginalText(matches(NOT_ENGLISH));
        verify(view, times(0)).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();
        verify(view, times(0)).setAnswerText("");

        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).lockRightAnswer();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        verify(view).setProgress(83);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.originalTextClick();
        verify(view).openDialogForSentenceScoring(any(Sentence.class));
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
        verify(view).showSentenceChangeUnsupportedMessage();

        verify(view, times(0)).hideNextButton();
        verify(view, times(0)).showCheckButton();
        verify(view, times(0)).setOriginalText(matches(NOT_ENGLISH));
        verify(view, times(0)).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(true));
        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();
        verify(view, times(0)).setAnswerText("");

        verify(view, times(0)).showSentenceChangedSuccessfullyMessage();

        verify(view).hidePleaseWaitProgressBar();
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).showSpellingOrGrammarErrorPanel(contains(wrongWord));
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(100);
        verify(view).showCongratulationMessage();
        verify(view).closeActivity();
        verify(view).openAnotherActivity();
        verify(view).setEnableCheckButton(true);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_indexOutOfBoundsExceptionAfterCheck() {
        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        presenter.nextButtonClick();
        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());

        // can fail here
        presenter.pronounceRightAnswerButtonClick();
        verify(speaker).speak(sentence.getText());
    }

    @Test
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() {
        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        presenter.nextButtonClick();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class), eq(false));
        reset(view);

        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());

        verify(view).lockRightAnswer();
    }
}