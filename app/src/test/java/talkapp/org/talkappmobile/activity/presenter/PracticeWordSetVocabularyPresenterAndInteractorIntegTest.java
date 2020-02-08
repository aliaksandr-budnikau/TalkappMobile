package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.RepositoryFactory;
import talkapp.org.talkappmobile.dao.impl.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetVocabularyPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private PracticeWordSetVocabularyInteractor interactor;
    private PracticeWordSetVocabularyView view;
    private RepositoryFactory repositoryFactory;
    private ServiceFactory serviceFactory;

    @Before
    public void setup() {
        view = mock(PracticeWordSetVocabularyView.class);

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

        interactor = new PracticeWordSetVocabularyInteractor(serviceFactory.getWordSetExperienceRepository(), serviceFactory.getWordTranslationService(), serviceFactory.getWordRepetitionProgressService(), serviceFactory.getCurrentPracticeStateService());
    }

    @After
    public void tearDown() throws Exception {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
    }

    @Test
    public void test() {
        int id = -1;
        List<Word2Tokens> words = asList(new Word2Tokens("age", "age", id), new Word2Tokens("anniversary", "anniversary", id), new Word2Tokens("birth", "birth", id));

        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setWords(words);
        wordSet.setTopicId("topicId");

        PracticeWordSetVocabularyPresenter presenter = new PracticeWordSetVocabularyPresenter(view, interactor);

        presenter.initialise(wordSet);
        ArgumentCaptor<List<WordTranslation>> wordTranslationsCaptor = forClass(List.class);
        verify(view).setWordSetVocabularyList(wordTranslationsCaptor.capture());
        assertFalse(wordTranslationsCaptor.getValue().isEmpty());
        assertEquals(words.size(), wordTranslationsCaptor.getValue().size());
        List<WordTranslation> translations = wordTranslationsCaptor.getValue();
        for (WordTranslation translation : translations) {
            HashSet<String> wordsForCheck = new HashSet<>();
            for (Word2Tokens word2Tokens : words) {
                wordsForCheck.add(word2Tokens.getWord());
            }
            assertTrue(wordsForCheck.contains(translation.getWord()));
        }
    }
}