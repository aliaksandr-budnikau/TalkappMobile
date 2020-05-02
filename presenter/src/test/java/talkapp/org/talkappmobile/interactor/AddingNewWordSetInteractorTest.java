package talkapp.org.talkappmobile.interactor;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;

import talkapp.org.talkappmobile.listener.OnAddingNewWordSetListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.presenter.BuildConfig;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetInteractorTest {
    private AddingNewWordSetInteractor interactor;
    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        serviceFactory = new ServiceFactoryImpl(RuntimeEnvironment.application);
        interactor = new AddingNewWordSetInteractor(null, serviceFactory.getWordTranslationService(), serviceFactory.getDataServer());
    }

    @Test
    public void submit_noSentencesForAnyWord() {
        OnAddingNewWordSetListener mock = Mockito.mock(OnAddingNewWordSetListener.class);
        interactor.savePhraseTranslationInputOnPopup("  sdfds ", null, mock);

        Mockito.verify(mock, Mockito.times(0)).onNewWordSuccessfullySubmitted(ArgumentMatchers.any(WordSet.class));
        Mockito.verify(mock, Mockito.times(1)).onNewWordTranslationWasNotFound();
    }

    @Test
    public void submit_noSentencesForFewWord() {
        OnAddingNewWordSetListener mock = Mockito.mock(OnAddingNewWordSetListener.class);
        interactor.savePhraseTranslationInputOnPopup("house", null, mock);

        Mockito.verify(mock, Mockito.times(0)).onNewWordSuccessfullySubmitted(ArgumentMatchers.any(WordSet.class));
        Mockito.verify(mock, Mockito.times(0)).onNewWordTranslationWasNotFound();
    }

    @Test
    public void submit_allSentencesWereFound() throws SQLException {
        OnAddingNewWordSetListener mock = Mockito.mock(OnAddingNewWordSetListener.class);
        String word0 = "house";
        interactor.savePhraseTranslationInputOnPopup(word0, null, mock);

        Mockito.verify(mock, Mockito.times(0)).onNewWordTranslationWasNotFound();
        Mockito.verify(mock, Mockito.times(1)).onPhraseTranslationInputWasValidatedSuccessfully(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(String.class));
    }


    @Test
    public void submit_allSentencesWereFoundButThereFewExpressions() throws SQLException {
        OnAddingNewWordSetListener mock = Mockito.mock(OnAddingNewWordSetListener.class);
        String word = "make out";
        String translation = "разглядеть, различить, разбирать";
        interactor.savePhraseTranslationInputOnPopup("  " + word + " ", "  " + translation + " ", mock);

        Mockito.verify(mock, Mockito.times(0)).onNewWordTranslationWasNotFound();
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }
}