package talkapp.org.talkappmobile.activity.custom.controller;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.service.AddingEditingNewWordSetsService;
import talkapp.org.talkappmobile.service.impl.AddingEditingNewWordSetsServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordSetVocabularyViewControllerTest {
    private EventBus eventBus;
    private AddingEditingNewWordSetsService service;
    private ServiceFactoryBean serviceFactory;

    @Before
    public void setUp() throws Exception {
        serviceFactory = new ServiceFactoryBean() {
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
        serviceFactory.setContext(mock(Context.class));
        eventBus = mock(EventBus.class);
        service = new AddingEditingNewWordSetsServiceImpl(eventBus, serviceFactory.getDataServer(), serviceFactory.getWordTranslationService());
    }

    @Test
    public void submit_noSentencesForAnyWord() {
        service.saveNewWordTranslation("  sdfds ", null);

        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(1)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_noSentencesForFewWord() {
        service.saveNewWordTranslation("house", null);

        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_allSentencesWereFound() throws SQLException {
        String word0 = "house";
        service.saveNewWordTranslation(word0, null);

        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
        verify(eventBus, times(1)).post(any(PhraseTranslationInputWasValidatedSuccessfullyEM.class));
    }


    @Test
    public void submit_allSentencesWereFoundButThereFewExpressions() throws SQLException {
        String word = "make out";
        String translation = "разглядеть, различить, разбирать";
        service.saveNewWordTranslation("  " + word + " ", "  " + translation + " ");

        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }
}