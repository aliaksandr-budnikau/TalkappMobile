package talkapp.org.talkappmobile.activity;

import android.view.View;

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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyItemAlertDialog;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetVocabularyFragmentTest {

    private PracticeWordSetVocabularyFragment practiceWordSetVocabularyFragment;
    private WordSet wordSet;

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Before
    public void setUp() {
        RepositoryFactory repositoryFactory = RepositoryFactoryProvider.get(RuntimeEnvironment.application);
        ServiceFactory serviceFactory = new ServiceFactoryImpl(repositoryFactory);
        PresenterFactory presenterFactory = new talkapp.org.talkappmobile.presenter.PresenterFactoryImpl(serviceFactory);

//        PresenterFactoryProvider presenterFactoryProvider = new PresenterFactoryProvider();
  //      presenterFactoryProvider.setPresenterFactory(presenterFactory);

        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(View.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        practiceWordSetVocabularyFragment = new PracticeWordSetVocabularyFragment();
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "eventBus", mock(EventBus.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "editVocabularyItemAlertDialog", mock(WordSetVocabularyItemAlertDialog.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSetVocabularyView", mock(WordSetVocabularyView.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
      //  Whitebox.setInternalState(practiceWordSetVocabularyFragment, "presenterFactoryProvider", presenterFactoryProvider);

        int id = 0;
        wordSet = new WordSet();
        wordSet.setId(id);

        int ageWordSetId = id + 1;
        Word2Tokens age = new Word2Tokens("age", "age", ageWordSetId);
        List<Word2Tokens> ageWordSetWords = asList(age, new Word2Tokens(), new Word2Tokens());
        WordRepetitionProgress exercise = new WordRepetitionProgress();
        exercise.setSentenceIds(asList("AWbgboVdNEXFMlzHK5SR"));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(ageWordSetId);
        exercise.setWordIndex(ageWordSetWords.indexOf(age));
        serviceFactory.getWordRepetitionProgressService().createNewOrUpdate(exercise);

        WordSet ageWordSet = new WordSet();
        ageWordSet.setId(ageWordSetId);
        ageWordSet.setStatus(WordSetProgressStatus.FINISHED);
        ageWordSet.setWords(new LinkedList<>(ageWordSetWords));
        ageWordSet.setTopicId("topicId");
        ageWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetService().save(ageWordSet);

        int anniversaryWordSetId = id + 2;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", anniversaryWordSetId);
        List<Word2Tokens> anniversaryWordSetWords = asList(new Word2Tokens(), anniversary, new Word2Tokens());
        exercise = new WordRepetitionProgress();
        exercise.setSentenceIds(asList("AWbgbq6hNEXFMlzHK5Ul"));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(anniversaryWordSetId);
        exercise.setWordIndex(anniversaryWordSetWords.indexOf(anniversary));
        serviceFactory.getWordRepetitionProgressService().createNewOrUpdate(exercise);

        WordSet anniversaryWordSet = new WordSet();
        anniversaryWordSet.setId(anniversaryWordSetId);
        anniversaryWordSet.setStatus(WordSetProgressStatus.FINISHED);
        anniversaryWordSet.setWords(new LinkedList<>(anniversaryWordSetWords));
        anniversaryWordSet.setTopicId("topicId");
        anniversaryWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetService().save(anniversaryWordSet);

        int birthWordSetId = id + 3;
        Word2Tokens birth = new Word2Tokens("birth", "birth", birthWordSetId);
        List<Word2Tokens> birthWordSetWords = asList(new Word2Tokens(), new Word2Tokens(), birth);
        exercise = new WordRepetitionProgress();
        exercise.setSentenceIds(asList("AWbgbsUXNEXFMlzHK5V2"));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(birthWordSetId);
        exercise.setWordIndex(birthWordSetWords.indexOf(birth));
        serviceFactory.getWordRepetitionProgressService().createNewOrUpdate(exercise);

        WordSet birthWordSet = new WordSet();
        birthWordSet.setId(birthWordSetId);
        birthWordSet.setStatus(WordSetProgressStatus.FINISHED);
        birthWordSet.setWords(new LinkedList<>(birthWordSetWords));
        birthWordSet.setTopicId("topicId");
        birthWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetService().save(birthWordSet);

        wordSet.setWords(new LinkedList<>(asList(age, anniversary, birth)));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(1);
        wordSet.setStatus(WordSetProgressStatus.FINISHED);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSet", wordSet);

        List<Word2Tokens> words = wordSet.getWords();
        for (Word2Tokens word : words) {
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setWord(word.getWord());
            wordTranslation.setTranslation(word.getWord());
            wordTranslation.setLanguage("russian");
            serviceFactory.getWordTranslationService().createNewOrUpdate(asList(wordTranslation));
        }
    }

    @Test
    public void testPracticeWordSetVocabularyFragment_repetitionMode() {
        practiceWordSetVocabularyFragment.init();
    }
}