package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.PresenterFactory;
import talkapp.org.talkappmobile.activity.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class ChangeVocabularyItemAndSubmitCorrectAnswerRepetitionIntegTest {
    private IPracticeWordSetPresenter practiceWordSetPresenter;
    private PracticeWordSetVocabularyPresenter practiceWordSetVocabularyPresenter;
    private WordSet wordSet;

    @Before
    public void setUp() {
        RepositoryFactory repositoryFactory = new RepositoryFactoryImpl(mock(Context.class)) {
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
        ServiceFactory serviceFactory = ServiceFactoryBean.getInstance(repositoryFactory);

        wordSet = new WordSet();
        wordSet.setId(1000000 + 1);
        wordSet.setStatus(WordSetProgressStatus.FINISHED);
        String name = WordSetProgressStatus.FINISHED.name();

        int ageWordSetId = wordSet.getId() + 1;
        Word2Tokens age = new Word2Tokens("ace", "ace", ageWordSetId);
        List<Word2Tokens> ageWordSetWords = asList(age, new Word2Tokens(), new Word2Tokens());
        WordRepetitionProgress exercise = new WordRepetitionProgress();
        exercise.setSentenceIds(asList("AWbgbnj1NEXFMlzHK5Rk"));
        exercise.setStatus(name);
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(ageWordSetId);
        exercise.setWordIndex(ageWordSetWords.indexOf(age));
        repositoryFactory.getWordRepetitionProgressRepository().createNewOrUpdate(exercise);

        WordSet ageWordSet = new WordSet();
        ageWordSet.setId(ageWordSetId);
        ageWordSet.setStatus(WordSetProgressStatus.FINISHED);
        ageWordSet.setWords(new LinkedList<>(ageWordSetWords));
        ageWordSet.setTopicId("topicId");
        ageWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetExperienceRepository().save(ageWordSet);

        int anniversaryWordSetId = wordSet.getId() + 2;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", anniversaryWordSetId);
        List<Word2Tokens> anniversaryWordSetWords = asList(new Word2Tokens(), anniversary, new Word2Tokens());
        exercise = new WordRepetitionProgress();
        exercise.setSentenceIds(asList("AWoFiKcqDTAu_IiLfhod"));
        exercise.setStatus(name);
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(anniversaryWordSetId);
        exercise.setWordIndex(anniversaryWordSetWords.indexOf(anniversary));
        repositoryFactory.getWordRepetitionProgressRepository().createNewOrUpdate(exercise);

        WordSet anniversaryWordSet = new WordSet();
        anniversaryWordSet.setId(anniversaryWordSetId);
        anniversaryWordSet.setStatus(WordSetProgressStatus.FINISHED);
        anniversaryWordSet.setWords(new LinkedList<>(anniversaryWordSetWords));
        anniversaryWordSet.setTopicId("topicId");
        anniversaryWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetExperienceRepository().save(anniversaryWordSet);

        int birthWordSetId = wordSet.getId() + 3;
        Word2Tokens birth = new Word2Tokens("birth", "birth", birthWordSetId);
        List<Word2Tokens> birthWordSetWords = asList(new Word2Tokens(), new Word2Tokens(), birth);
        int wordIndexBirth = birthWordSetWords.indexOf(birth);
        exercise = new WordRepetitionProgress();
        exercise.setSentenceIds(asList("AWoEEi8tDTAu_IiLecuS"));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(birthWordSetId);
        exercise.setWordIndex(wordIndexBirth);
        repositoryFactory.getWordRepetitionProgressRepository().createNewOrUpdate(exercise);

        WordSet birthWordSet = new WordSet();
        birthWordSet.setId(birthWordSetId);
        birthWordSet.setStatus(WordSetProgressStatus.FINISHED);
        birthWordSet.setWords(new LinkedList<>(birthWordSetWords));
        birthWordSet.setTopicId("topicId");
        birthWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetExperienceRepository().save(birthWordSet);

        wordSet.setWords(new LinkedList<>(asList(age, anniversary, birth)));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(0);
        wordSet.setStatus(WordSetProgressStatus.FINISHED);
        PresenterFactory presenterFactory = new PresenterFactory();

        Whitebox.setInternalState(presenterFactory, "equalityScorer", new EqualityScorerBean());
        Whitebox.setInternalState(presenterFactory, "textUtils", new TextUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "logger", new LoggerBean());
        Whitebox.setInternalState(presenterFactory, "audioStuffFactory", new AudioStuffFactoryBean());

        practiceWordSetPresenter = presenterFactory.create(mock(PracticeWordSetView.class), mock(Context.class), true);
        practiceWordSetVocabularyPresenter = presenterFactory.create(mock(PracticeWordSetVocabularyView.class));

    }

    @Test
    public void test() {
        practiceWordSetPresenter.initialise(wordSet);
        practiceWordSetPresenter.nextButtonClick();
        practiceWordSetVocabularyPresenter.initialise(wordSet);
        WordTranslation wordTranslation = new WordTranslation();
        String age = "age";
        wordTranslation.setWord(age);
        wordTranslation.setTokens(age);
        wordTranslation.setLanguage("russian");
        wordTranslation.setTranslation("возраст");
        practiceWordSetVocabularyPresenter.updateCustomWordSet(0, wordTranslation);
        practiceWordSetPresenter.checkRightAnswerCommandRecognized();
        practiceWordSetPresenter.nextButtonClick();
        practiceWordSetVocabularyPresenter.updateCustomWordSet(1, wordTranslation);
        practiceWordSetPresenter.checkRightAnswerCommandRecognized();
        practiceWordSetPresenter.nextButtonClick();
        practiceWordSetVocabularyPresenter.updateCustomWordSet(2, wordTranslation);
        practiceWordSetPresenter.checkRightAnswerCommandRecognized();
        practiceWordSetPresenter.nextButtonClick();
        HashSet<String> words = new HashSet<>();
        for (Word2Tokens word : wordSet.getWords()) {
            words.add(word.getWord());
        }
        assertTrue(words.contains(age));
        assertEquals(1, words.size());
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
    }
}
