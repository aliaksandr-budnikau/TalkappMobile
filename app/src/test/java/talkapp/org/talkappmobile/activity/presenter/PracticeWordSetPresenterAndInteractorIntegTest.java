package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.impl.PracticeWordSetExerciseServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordSetExperienceServiceImpl;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.component.impl.BackendSentenceProviderStrategy;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBean;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorBean;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private PracticeWordSetView view;
    private PracticeWordSetPresenter presenter;
    private PracticeWordSetExerciseService exerciseService;
    @Mock
    private UserExpService userExpService;
    private WordSet wordSet;
    private StudyingPracticeWordSetInteractor interactor;
    @Mock
    private Context context;
    private WordSetExperienceDao wordSetExperienceDao;
    private WordSetDao wordSetDao;
    private WordSetExperienceService experienceService;

    @Before
    public void setup() {
        PracticeWordSetExerciseDao exerciseDao = providePracticeWordSetExerciseDao();
        wordSetExperienceDao = provideWordSetExperienceDao();
        wordSetDao = provideWordSetDao();
        exerciseService = new PracticeWordSetExerciseServiceImpl(exerciseDao, wordSetExperienceDao, new ObjectMapper());
        LoggerBean logger = new LoggerBean();
        experienceService = new WordSetExperienceServiceImpl(wordSetExperienceDao, wordSetDao, logger);
        interactor = new StudyingPracticeWordSetInteractor(new RandomWordsCombinatorBean(),
                new SentenceProviderImpl(new BackendSentenceProviderStrategy(getServer()), new SentenceProviderRepetitionStrategy(getServer(), exerciseService)),
                new RandomSentenceSelectorBean(), new RefereeServiceImpl(new GrammarCheckServiceImpl(getServer()), new EqualityScorerBean()),
                logger, experienceService, exerciseService, userExpService, context, new AudioStuffFactoryBean());
        getServer().findAllWordSets();
    }

    private void createPresenter(StudyingPracticeWordSetInteractor interactor) {
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
        PracticeWordSetViewStrategy firstCycleViewStrategy = new PracticeWordSetViewStrategy(view, new TextUtilsImpl(), new WordSetExperienceUtilsImpl());
        presenter = new PracticeWordSetPresenter(wordSet, interactor, firstCycleViewStrategy);
    }

    @Test
    public void testPracticeWordSet_completeOneSet() {
        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(16);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(33);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(50);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(66);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(83);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
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
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() {
        createPresenter(interactor);

        presenter.initialise();
        presenter.nextButtonClick();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        Sentence sentence = exerciseService.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());

        verify(view).onExerciseGotAnswered();
    }
}