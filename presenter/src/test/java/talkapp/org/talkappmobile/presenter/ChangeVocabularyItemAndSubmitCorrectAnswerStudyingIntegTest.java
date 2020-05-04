package talkapp.org.talkappmobile.presenter;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashSet;

import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class ChangeVocabularyItemAndSubmitCorrectAnswerStudyingIntegTest {
    public static final String RUSSIAN = "russian";
    public static final String AGE = "age";
    public static final String ANNIVERSARY = "anniversary";
    public static final String BIRTH = "birth";
    public static final String ACE = "ace";
    private IPracticeWordSetPresenter practiceWordSetPresenter;
    private PracticeWordSetVocabularyPresenter practiceWordSetVocabularyPresenter;
    private WordSet wordSet;
    private ServiceFactory serviceFactory;

    @Before
    public void setUp() {

        RepositoryFactory repositoryFactory = RepositoryFactoryProvider.get(RuntimeEnvironment.application);
        serviceFactory = new ServiceFactoryImpl(repositoryFactory);
        PresenterFactory presenterFactory = new PresenterFactoryImpl(serviceFactory);

        practiceWordSetPresenter = presenterFactory.create(mock(PracticeWordSetView.class), false);
        practiceWordSetVocabularyPresenter = presenterFactory.create(mock(PracticeWordSetVocabularyView.class));

        wordSet = new WordSet();
        wordSet.setId(-1);

        Word2Tokens age = new Word2Tokens(AGE, AGE, wordSet.getId());
        Word2Tokens anniversary = new Word2Tokens(ANNIVERSARY, ANNIVERSARY, wordSet.getId());
        Word2Tokens birth = new Word2Tokens(BIRTH, BIRTH, wordSet.getId());

        wordSet.setWords(asList(age, anniversary, birth));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(0);
        wordSet.setStatus(FIRST_CYCLE);

        serviceFactory.getWordSetService().save(wordSet);
    }

    @Test
    public void test() {
        practiceWordSetPresenter.initialise(wordSet);
        practiceWordSetPresenter.nextButtonClick();
        practiceWordSetVocabularyPresenter.initialise(wordSet);

        WordTranslation wordTranslation = getWordTranslation(AGE, RUSSIAN, ACE, "Туз");
        practiceWordSetVocabularyPresenter.updateCustomWordSet(0, wordTranslation);
        practiceWordSetPresenter.checkRightAnswerCommandRecognized();
        practiceWordSetPresenter.nextButtonClick();

        wordTranslation = getWordTranslation(ANNIVERSARY, RUSSIAN, ACE, "Туз");
        practiceWordSetVocabularyPresenter.updateCustomWordSet(1, wordTranslation);
        practiceWordSetPresenter.checkRightAnswerCommandRecognized();
        practiceWordSetPresenter.nextButtonClick();

        wordTranslation = getWordTranslation(BIRTH, RUSSIAN, ACE, "Туз");
        practiceWordSetVocabularyPresenter.updateCustomWordSet(2, wordTranslation);
        practiceWordSetPresenter.checkRightAnswerCommandRecognized();
        practiceWordSetPresenter.nextButtonClick();

        HashSet<String> words = new HashSet<>();
        for (Word2Tokens word : wordSet.getWords()) {
            words.add(word.getWord());
        }
        assertTrue(words.contains(ACE));
        assertEquals(1, words.size());
        WordTranslationService wordTranslationService = serviceFactory.getWordTranslationService();
        assertNull(wordTranslationService.findByWordAndByLanguage(AGE, RUSSIAN));
        assertNull(wordTranslationService.findByWordAndByLanguage(ANNIVERSARY, RUSSIAN));
        assertNull(wordTranslationService.findByWordAndByLanguage(BIRTH, RUSSIAN));
        assertNotNull(wordTranslationService.findByWordAndByLanguage(ACE, RUSSIAN));
    }

    private WordTranslation getWordTranslation(String word, String language, String newWord, String translation) {
        WordTranslationService wordTranslationService = serviceFactory.getWordTranslationService();
        WordTranslation wordTranslation = wordTranslationService.findByWordAndByLanguage(word, language);
        wordTranslation.setWord(newWord);
        wordTranslation.setTokens(newWord);
        wordTranslation.setLanguage(RUSSIAN);
        wordTranslation.setTranslation("возраст");
        return wordTranslation;
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }
}
