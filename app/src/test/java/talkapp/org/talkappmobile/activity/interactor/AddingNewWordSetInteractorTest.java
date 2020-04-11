package talkapp.org.talkappmobile.activity.interactor;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.listener.OnAddingNewWordSetListener;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetInteractorTest {
    private AddingNewWordSetInteractor interactor;
    private ServiceFactory serviceFactory;
    private RepositoryFactory repositoryFactory;

    @Before
    public void setUp() throws Exception {
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
        interactor = new AddingNewWordSetInteractor(null, serviceFactory.getWordTranslationService(), serviceFactory.getDataServer());
    }

    @Test
    public void submit_noSentencesForAnyWord() {
        OnAddingNewWordSetListener mock = mock(OnAddingNewWordSetListener.class);
        interactor.savePhraseTranslationInputOnPopup("  sdfds ", null, mock);

        verify(mock, times(0)).onNewWordSuccessfullySubmitted(any(WordSet.class));
        verify(mock, times(1)).onNewWordTranslationWasNotFound();
    }

    @Test
    public void submit_noSentencesForFewWord() {
        OnAddingNewWordSetListener mock = mock(OnAddingNewWordSetListener.class);
        interactor.savePhraseTranslationInputOnPopup("house", null, mock);

        verify(mock, times(0)).onNewWordSuccessfullySubmitted(any(WordSet.class));
        verify(mock, times(0)).onNewWordTranslationWasNotFound();
    }

    @Test
    public void submit_allSentencesWereFound() throws SQLException {
        OnAddingNewWordSetListener mock = mock(OnAddingNewWordSetListener.class);
        String word0 = "house";
        interactor.savePhraseTranslationInputOnPopup(word0, null, mock);

        verify(mock, times(0)).onNewWordTranslationWasNotFound();
        verify(mock, times(1)).onPhraseTranslationInputWasValidatedSuccessfully(anyString(), nullable(String.class));
    }


    @Test
    public void submit_allSentencesWereFoundButThereFewExpressions() throws SQLException {
        OnAddingNewWordSetListener mock = mock(OnAddingNewWordSetListener.class);
        String word = "make out";
        String translation = "разглядеть, различить, разбирать";
        interactor.savePhraseTranslationInputOnPopup("  " + word + " ", "  " + translation + " ", mock);

        verify(mock, times(0)).onNewWordTranslationWasNotFound();
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
    }
}