package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.module.GameplayModule.PLACEHOLDER;

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
    private PracticeWordSetInteractor interactor;
    private ViewStrategyFactory viewStrategyFactory;
    private Speaker speaker;

    @Before
    public void setup() {
        viewStrategyFactory = getClassForInjection().getViewStrategyFactory();
        interactor = getClassForInjection().getPracticeWordSetInteractor();
        exerciseService = getClassForInjection().getExerciseService();
        speaker = getClassForInjection().getSpeaker();
    }

    private void createPresenter(PracticeWordSetInteractor interactor, ViewStrategyFactory viewStrategyFactory) {
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
        PracticeWordSetViewHideNewWordOnlyStrategy newWordOnlyStrategy = viewStrategyFactory.createPracticeWordSetViewHideNewWordOnlyStrategy(view);
        PracticeWordSetViewHideAllStrategy hideAllStrategy = viewStrategyFactory.createPracticeWordSetViewHideAllStrategy(view);
        presenter = new PracticeWordSetPresenter(wordSet, interactor, newWordOnlyStrategy, hideAllStrategy);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        verify(view).setRightAnswer(matches(ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length <= 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
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
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(matches(NOT_ENGLISH));
        arg = ArgumentCaptor.forClass(String.class);
        verify(view).setRightAnswer(arg.capture());
        assertTrue(arg.getValue().split(PLACEHOLDER_REGEX).length > 2);
        verify(view).setAnswerText("");
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
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
        // and here
        presenter.rightAnswerTouched();
        verify(speaker).speak(sentence.getText());
    }

    @Test
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() {
        createPresenter(interactor, viewStrategyFactory);
        login();

        presenter.initialise();
        presenter.nextButtonClick();
        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        presenter.rightAnswerTouched();
        presenter.rightAnswerUntouched();

        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).setRightAnswer(arg.capture());
        List<String> allValues = arg.getAllValues();
        assertFalse(allValues.get(allValues.size() - 1).contains(PLACEHOLDER));
    }
}