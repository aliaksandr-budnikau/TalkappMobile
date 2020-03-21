package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashSet;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.PresenterFactory;
import talkapp.org.talkappmobile.activity.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private PracticeWordSetView view;
    private IPracticeWordSetPresenter presenter;
    private WordSet wordSet;
    private Context context;
    private CurrentPracticeStateService currentPracticeStateService;
    private PresenterFactory presenterFactory;
    private ServiceFactory serviceFactory;
    private RepositoryFactory repositoryFactory;

    @Before
    public void setup() {
        view = mock(PracticeWordSetView.class);
        context = mock(Context.class);
        LoggerBean logger = new LoggerBean();

        repositoryFactory = new RepositoryFactoryImpl(mock(Context.class)) {
            private DatabaseHelper helper;

            @Override
            protected DatabaseHelper databaseHelper() {
                if (helper != null) {
                    return helper;
                }
                helper = getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
                return helper;
            }
        };
        serviceFactory = ServiceFactoryBean.getInstance(repositoryFactory);
        currentPracticeStateService = serviceFactory.getCurrentPracticeStateService();

        presenterFactory = new PresenterFactory();
        Whitebox.setInternalState(presenterFactory, "equalityScorer", new EqualityScorerBean());
        Whitebox.setInternalState(presenterFactory, "textUtils", new TextUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "logger", logger);
        Whitebox.setInternalState(presenterFactory, "audioStuffFactory", new AudioStuffFactoryBean());

        serviceFactory.getWordSetExperienceRepository().getWordSets(null);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
    }

    private void createPresenter() {
        int id = -1;
        int trainingExperience = 0;
        WordSetProgressStatus status = FIRST_CYCLE;
        if (wordSet != null) {
            WordSet wordSet = currentPracticeStateService.getWordSet();
            trainingExperience = wordSet.getTrainingExperience();
            status = wordSet.getStatus();
        }
        wordSet = new WordSet();
        wordSet.setId(id);

        Word2Tokens age = new Word2Tokens("age", "age", wordSet.getId());
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", wordSet.getId());
        Word2Tokens birth = new Word2Tokens("birth", "birth", wordSet.getId());

        wordSet.setWords(asList(age, anniversary, birth));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);

        serviceFactory.getWordSetExperienceRepository().save(wordSet);
        presenter = presenterFactory.create(view, context, false);
    }

    @Test
    public void testPracticeWordSet_completeOneSet() {
        createPresenter();
        HashSet<Word2Tokens> historyOfWords = new HashSet<>();

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        ArgumentCaptor<Word2Tokens> captor = ArgumentCaptor.forClass(Word2Tokens.class);
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        Word2Tokens currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        historyOfWords.clear();

        // sentence 4
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(100);
        verify(view).showCongratulationMessage();
        verify(view).hideNextButton();
        verify(view).showCloseButton();
        verify(view).onUpdateUserExp(1);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_completeOneSetAndRestartAfterEacheStep() {
        createPresenter();
        HashSet<Word2Tokens> historyOfWords = new HashSet<>();

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        ArgumentCaptor<Word2Tokens> captor = ArgumentCaptor.forClass(Word2Tokens.class);
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        Word2Tokens currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        historyOfWords.add(currentWord);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(16);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        historyOfWords.add(currentWord);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }


        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(33);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        historyOfWords.add(currentWord);
        historyOfWords.clear();

        // sentence 4
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(50);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        historyOfWords.add(currentWord);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(66);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        historyOfWords.add(currentWord);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(83);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(100);
        verify(view).showCongratulationMessage();
        verify(view).hideNextButton();
        verify(view).showCloseButton();
        verify(view).onUpdateUserExp(1);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() {
        createPresenter();

        presenter.initialise(wordSet);
        presenter.nextButtonClick();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());

        verify(view).onExerciseGotAnswered();
    }

    @Test
    public void testPracticeWordSet_notSavingToDBBug() {
        createPresenter();
        presenter.initialise(wordSet);
        presenter.nextButtonClick();
        presenter.changeSentence();
        for (int i = 0; i < 6; i++) {
            Sentence sentence = presenter.getCurrentSentence();
            presenter.checkAnswerButtonClick(sentence.getText());
            presenter.nextButtonClick();
            presenter.changeSentence();
            WordSet wordSetFromDB = repositoryFactory.getWordSetRepository().findById(this.wordSet.getId());
            assertEquals(wordSet.getRepetitionClass(), wordSetFromDB.getRepetitionClass());
            assertEquals(wordSet.getStatus(), wordSetFromDB.getStatus());
            assertEquals(wordSet.getId(), wordSetFromDB.getId());
            assertEquals(wordSet.getTrainingExperience(), wordSetFromDB.getTrainingExperience());
        }
    }

    @Test
    public void testPracticeWordSet_completeOneSetWithPeepingBug() {
        createPresenter();
        presenter.initialise(wordSet);
        presenter.nextButtonClick();
        presenter.changeSentence();
        presenter.markAnswerHasBeenSeen();
        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        presenter.nextButtonClick();
        presenter.changeSentence();
    }
}