package talkapp.org.talkappmobile.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyItemAlertDialog;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.component.PresenterFactoryProvider;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;
import talkapp.org.talkappmobile.service.WordSetService;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.text.TextUtils.isEmpty;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.service.WordTranslationServiceImpl.RUSSIAN_LANGUAGE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class CapitalLetterInNewWordTest {
    private WordSet wordSet;
    private PracticeWordSetFragment practiceWordSetFragment;
    private AddingNewWordSetFragment addingNewWordSetFragment;
    private PracticeWordSetVocabularyFragment practiceWordSetVocabularyFragment;
    private TextView answerTextMock;
    private EventBus eventBusMock = mock(EventBus.class);
    private WordSetVocabularyView wordSetVocabularyView;
    private ServiceFactory serviceFactory;
    private PresenterFactory presenterFactory;

    @Before
    public void setup() {
        RepositoryFactory repositoryFactory = RepositoryFactoryProvider.get(RuntimeEnvironment.application);
        serviceFactory = new ServiceFactoryImpl(repositoryFactory);
        presenterFactory = new talkapp.org.talkappmobile.presenter.PresenterFactoryImpl(serviceFactory);

        PresenterFactoryProvider presenterFactoryProvider = new PresenterFactoryProvider();
        presenterFactoryProvider.setPresenterFactory(presenterFactory);
        serviceFactory.getWordSetService().getWordSets(null);
        practiceWordSetFragment = new PracticeWordSetFragment();
        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(View.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        Whitebox.setInternalState(practiceWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetFragment, "originalText", mock(TextView.class));
        Whitebox.setInternalState(practiceWordSetFragment, "rightAnswer", mock(TextView.class));
        Whitebox.setInternalState(practiceWordSetFragment, "presenterFactoryProvider", presenterFactoryProvider);
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
        Whitebox.setInternalState(practiceWordSetFragment, "eventBus", eventBusMock);

        addingNewWordSetFragment = new AddingNewWordSetFragment();
        Whitebox.setInternalState(addingNewWordSetFragment, "presenterFactoryProvider", presenterFactoryProvider);
        Whitebox.setInternalState(addingNewWordSetFragment, "eventBus", eventBusMock);
        Whitebox.setInternalState(addingNewWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(addingNewWordSetFragment, "pleaseWaitProgressBar", mock(View.class));
        Whitebox.setInternalState(addingNewWordSetFragment, "editVocabularyItemAlertDialog", mock(WordSetVocabularyItemAlertDialog.class));
        Whitebox.setInternalState(addingNewWordSetFragment, "mainForm", mock(View.class));
        wordSetVocabularyView = mock(WordSetVocabularyView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "wordSetVocabularyView", wordSetVocabularyView);


        practiceWordSetVocabularyFragment = new PracticeWordSetVocabularyFragment();
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "presenterFactoryProvider", presenterFactoryProvider);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "eventBus", mock(EventBus.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "editVocabularyItemAlertDialog", mock(WordSetVocabularyItemAlertDialog.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSetVocabularyView", mock(WordSetVocabularyView.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
    }

    private WordSet createWordSet(int id, String... words) {
        int trainingExperience = 0;
        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (String word : words) {
            Word2Tokens age = new Word2Tokens(word, word, wordSet.getId());
            word2Tokens.add(age);
        }

        wordSet.setWords(word2Tokens);
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        return wordSet;
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void testCapitalLetterInNewWord() throws SQLException {
        addingNewWordSetFragment.init();
        String phrasalVerb = "look for";
        wordSet = createWordSet(1000000, "solemn", "grip", "wink", "adoption", "voluntary", phrasalVerb + "|искать", "preamble",
                "conquer", "adore", "deplete", "cease", "ratification");
        List<Word2Tokens> words = wordSet.getWords();
        List<WordTranslation> translations = new LinkedList<>();
        for (Word2Tokens word : words) {
            WordTranslation translation = new WordTranslation();
            String[] split = word.getWord().split("\\|");
            if (split.length == 1) {
                translation.setWord(split[0]);
                translation.setTranslation("");
            } else {
                translation.setWord(split[0]);
                translation.setTranslation(split[1]);
            }
            translations.add(translation);
        }
        when(wordSetVocabularyView.getVocabulary()).thenReturn(translations);

        for (Word2Tokens word : words) {
            String[] split = word.getWord().split("\\|");
            if (split.length != 2) {
                addingNewWordSetFragment.savePhraseTranslation(split[0].trim(), null);
                continue;
            }
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setId(word.getWord());
            wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
            wordTranslation.setTranslation(split[1].trim());
            wordTranslation.setWord(split[0].trim());
            wordTranslation.setTokens(split[0].trim());
            serviceFactory.getWordTranslationService().saveWordTranslations(asList(wordTranslation));
            addingNewWordSetFragment.savePhraseTranslation(split[0].trim(), split[1].trim());
        }

        reset(eventBusMock);
        try {
            addingNewWordSetFragment.onButtonSubmitClick();
        } catch (NullPointerException e) {
        }

        WordSetService wordSetService = serviceFactory.getWordSetService();
        wordSet = wordSetService.findById(wordSetService.getCustomWordSetsStartsSince());

        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSet", wordSet);
        practiceWordSetVocabularyFragment.init();

        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        practiceWordSetFragment.init();

        WordSet wordSet = Whitebox.getInternalState(practiceWordSetFragment, "wordSet");

        assertEquals(serviceFactory.getWordSetService().getCustomWordSetsStartsSince(), wordSet.getId());
        for (Word2Tokens word : wordSet.getWords()) {
            assertEquals(wordSet.getId(), word.getSourceWordSetId().intValue());
        }

        IPracticeWordSetPresenter presenter = Whitebox.getInternalState(practiceWordSetFragment, "presenter");
        Sentence currentSentence = presenter.getCurrentSentence();

        assertNotNull(currentSentence);
        assertFalse(isEmpty(currentSentence.getText()));
        assertFalse(isEmpty(currentSentence.getId()));

        while (true) {
            String text = presenter.getCurrentSentence().getText();
            if (text.equals(phrasalVerb)) {
                when(answerTextMock.getText()).thenReturn(phrasalVerb.substring(0, 5));
                practiceWordSetFragment.onCheckAnswerButtonClick();
                break;
            }
            practiceWordSetFragment.onNextButtonClick();
        }
    }
}