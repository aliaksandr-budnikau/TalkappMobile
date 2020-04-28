package talkapp.org.talkappmobile.activity;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.PresenterFactory;
import talkapp.org.talkappmobile.TestHelper;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.presenter.OriginalTextTextViewPresenter;
import talkapp.org.talkappmobile.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.events.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.events.NewSentenceEM;
import talkapp.org.talkappmobile.events.SentenceWasPickedForChangeEM;
import talkapp.org.talkappmobile.events.SentencesWereFoundForChangeEM;
import talkapp.org.talkappmobile.events.UserExpUpdatedEM;
import talkapp.org.talkappmobile.events.WordSetPracticeFinishedEM;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.LoggerImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.google.common.collect.Lists.newArrayList;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class ChangeSentenceTest {
    private WordSet wordSet;
    private PracticeWordSetFragment practiceWordSetFragment;
    private OriginalTextTextViewPresenter originalTextTextViewPresenter;
    private TextView answerTextMock;
    private TestHelper testHelper;
    private ServiceFactory serviceFactory;
    private RepositoryFactory repositoryFactory;

    @Before
    public void setup() {
        LoggerImpl logger = new LoggerImpl();
        ObjectMapper mapper = new ObjectMapper();
        testHelper = new TestHelper();
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

        PresenterFactory presenterFactory = new PresenterFactory();

        serviceFactory.getWordSetExperienceRepository().getWordSets(null);
        wordSet = createWordSet(-1, "age");
        practiceWordSetFragment = new PracticeWordSetFragment();
        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(View.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        Whitebox.setInternalState(practiceWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        Whitebox.setInternalState(practiceWordSetFragment, "presenterFactory", presenterFactory);
        Whitebox.setInternalState(practiceWordSetFragment, "originalText", mock(TextView.class));
        Whitebox.setInternalState(practiceWordSetFragment, "rightAnswer", mock(TextView.class));
        answerTextMock = mock(TextView.class);
        Whitebox.setInternalState(practiceWordSetFragment, "answerText", answerTextMock);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSetProgress", mock(ProgressBar.class));
        Whitebox.setInternalState(practiceWordSetFragment, "nextButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "checkButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "closeButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "speakButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "playButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "pronounceRightAnswerButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "pleaseWaitProgressBar", mock(View.class));
        Whitebox.setInternalState(practiceWordSetFragment, "wordSetPractiseForm", mock(View.class));
        Whitebox.setInternalState(practiceWordSetFragment, "spellingGrammarErrorsListView", mock(LinearLayout.class));
        EventBus eventBus = testHelper.getEventBusMock();
        Whitebox.setInternalState(practiceWordSetFragment, "eventBus", eventBus);

        originalTextTextViewPresenter = new OriginalTextTextViewPresenter(mock(OriginalTextTextViewView.class));
    }

    private WordSet createWordSet(int id, WordSetProgressStatus status, String... words) {
        int trainingExperience = 0;
        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (String word : words) {
            Word2Tokens age = new Word2Tokens(word, word, id);
            word2Tokens.add(age);
        }

        wordSet.setWords(word2Tokens);
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);
        serviceFactory.getWordSetExperienceRepository().save(wordSet);
        return wordSet;
    }

    private WordSet createWordSet(int id, String... words) {
        return createWordSet(id, FIRST_CYCLE, words);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
    }

    @Test
    public void testChangeOtherSentences() throws SQLException {
        EventBus eventBus = practiceWordSetFragment.eventBus;

        practiceWordSetFragment.init();

        // Test of showing a dialog where all sentences are checked in the beginning
        NewSentenceEM newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        Sentence displayedSentence = newSentenceEM.getSentence();
        Word2Tokens displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        SentencesWereFoundForChangeEM sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        reset(eventBus);

        // Test of changing of sentence the same sentence should not be picked twice sequentially
        List<Sentence> pickedSentences = getPickedSentences(displayedSentence, sentencesWereFoundForChangeEM);
        SentenceWasPickedForChangeEM wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, displayedWord);
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);
            newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
            displayedWord = newSentenceEM.getWord();
        }
        displayNewSentence(displayedSentence);

        // Test of showing the dialog where only previously changed sentences are checked
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());


        //
        // START OF MOVING TO THE NEXT CYCLE
        //
        when(answerTextMock.getText()).thenReturn(displayedSentence.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        UserExpUpdatedEM expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();
        //
        // SECOND CYCLE
        //

        // Test of showing a dialog on the second cycle. Only previously changed sentences are checked
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        // Test of picking all sentences on the second cycle
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(sentencesWereFoundForChangeEM.getSentences(), displayedWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);

        // Test of changing of sentence the same sentence should not be picked twice sequentially
        pickedSentences = getPickedSentences(displayedSentence, sentencesWereFoundForChangeEM);
        wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, displayedWord);
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);
            newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
            displayedWord = newSentenceEM.getWord();
        }
        displayNewSentence(displayedSentence);

        // Test of showing the dialog where only previously changed sentences are checked
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        when(answerTextMock.getText()).thenReturn(displayedSentence.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();


        //
        // REPETITION MODE
        //

        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();

        // Test of showing a dialog in the repetition mode. Only previously changed sentences (2) are checked
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 2);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        // Test of picking all sentences in the repetition mode
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(sentencesWereFoundForChangeEM.getSentences(), displayedWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);


        //
        // TEST OF SENTENCES CYCLICAL MOVEMENT FIRST STAGE
        //
        repositoryFactory.getWordRepetitionProgressRepository().cleanByWordSetId(-1);
        reset(eventBus);

        wordSet = createWordSet(-1, "birth", "anniversary");
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", false);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        practiceWordSetFragment.init();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        Sentence displayedSentenceForFirstWord = newSentenceEM.getSentence();
        Word2Tokens displayedWordForFirstWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        List<Sentence> allSentencesForFirstWord = sentencesWereFoundForChangeEM.getSentences();
        List<Sentence> alreadyPickedSentencesForFirstWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();
        assertEquals(allSentencesForFirstWord.size(), alreadyPickedSentencesForFirstWord.size());

        alreadyPickedSentencesForFirstWord = newArrayList(allSentencesForFirstWord.get(0), allSentencesForFirstWord.get(2), allSentencesForFirstWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForFirstWord, displayedWordForFirstWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentenceForFirstWord = newSentenceEM.getSentence();
        displayedWordForFirstWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        alreadyPickedSentencesForFirstWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the first word for the first cycle

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        Sentence displayedSentenceForSecondWord = newSentenceEM.getSentence();
        Word2Tokens displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        List<Sentence> allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        List<Sentence> alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();
        assertEquals(allSentencesForSecondWord.size(), alreadyPickedSentencesForSecondWord.size());

        alreadyPickedSentencesForSecondWord = newArrayList(allSentencesForSecondWord.get(0), allSentencesForSecondWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForSecondWord, displayedWordForSecondWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the second word for the first cycle

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        assertEquals(displayedSentenceForFirstWord, newSentenceEM.getSentence());
        assertEquals(displayedWordForFirstWord, newSentenceEM.getWord());

        //
        // TEST OF SENTENCES CYCLICAL MOVEMENT SECOND STAGE
        //

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        assertEquals(allSentencesForFirstWord, sentencesWereFoundForChangeEM.getSentences());

        assertEquals(alreadyPickedSentencesForFirstWord, sentencesWereFoundForChangeEM.getAlreadyPickedSentences());
        assertEquals(3, alreadyPickedSentencesForFirstWord.size());
        assertEquals(3, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the first word for the second cycle

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        assertEquals(displayedSentenceForSecondWord, newSentenceEM.getSentence());
        assertEquals(displayedWordForSecondWord, newSentenceEM.getWord());

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        assertEquals(allSentencesForSecondWord, sentencesWereFoundForChangeEM.getSentences());

        assertEquals(alreadyPickedSentencesForSecondWord, sentencesWereFoundForChangeEM.getAlreadyPickedSentences());
        assertEquals(2, alreadyPickedSentencesForSecondWord.size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        alreadyPickedSentencesForSecondWord = newArrayList(allSentencesForSecondWord.get(0), allSentencesForSecondWord.get(2), allSentencesForSecondWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForSecondWord, displayedWordForSecondWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the second word for the second cycle

        //
        // REPETITION MODE
        //
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();


        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 2);
        while (!displayedWordForFirstWord.equals(newSentenceEM.getWord())) {
            practiceWordSetFragment.onNextButtonClick();
            newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        }
        assertNotEquals(displayedSentenceForFirstWord, newSentenceEM.getSentence());
        assertEquals(alreadyPickedSentencesForFirstWord.get(1), newSentenceEM.getSentence());

        displayedSentenceForFirstWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        assertNotEquals(displayedSentenceForSecondWord, newSentenceEM.getSentence());
        assertEquals(alreadyPickedSentencesForSecondWord.get(1), newSentenceEM.getSentence());

        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();
        reset(eventBus);

        practiceWordSetFragment.onCloseButtonClick();

        WordSetPracticeFinishedEM wordSetPracticeFinishedEM = testHelper.getEM(WordSetPracticeFinishedEM.class, 1);
        assertNotNull(wordSetPracticeFinishedEM);

        //
        // REPETITION MODE SECOND TIME
        //
        wordSet = createWordSet(-1, WordSetProgressStatus.FINISHED, "birth", "anniversary");
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(2, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(2, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        practiceWordSetFragment.onCloseButtonClick();
    }

    @NonNull
    private List<Sentence> getPickedSentences(Sentence displayedSentence, SentencesWereFoundForChangeEM sentencesWereFoundForChangeEM) {
        List<Sentence> sentences = sentencesWereFoundForChangeEM.getSentences();
        return asList(sentences.indexOf(displayedSentence) == 5 ? sentences.get(6) : sentences.get(5), displayedSentence);
    }

    private void displayNewSentence(Sentence displayedSentence) {
        originalTextTextViewPresenter.setModel(displayedSentence);
        originalTextTextViewPresenter.unlock();
        originalTextTextViewPresenter.refresh();
    }
}