package talkapp.org.talkappmobile.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetVocabularyFragmentTest {

    private PracticeWordSetVocabularyFragment practiceWordSetVocabularyFragment;
    private WordSet wordSet;

    @Before
    public void setUp() throws JsonProcessingException, SQLException {
        DaoHelper daoHelper = new DaoHelper();

        ObjectMapper mapper = new ObjectMapper();

        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);

        WordSetServiceImpl wordSetService = new WordSetServiceImpl(daoHelper.getWordSetDao(), daoHelper.getNewWordSetDraftDao(), new WordSetExperienceUtilsImpl(), new WordSetMapper(mapper));
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(wordSetService);

        PresenterFactory presenterFactory = new PresenterFactory();
        presenterFactory = new PresenterFactory();

        BackendServerFactoryBean serverFactoryBean = mock(BackendServerFactoryBean.class);
        when(serverFactoryBean.get()).thenReturn(mock(DataServer.class));
        Whitebox.setInternalState(presenterFactory, "backendServerFactory", serverFactoryBean);
        Whitebox.setInternalState(presenterFactory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(presenterFactory, "equalityScorer", new EqualityScorerBean());
        Whitebox.setInternalState(presenterFactory, "textUtils", new TextUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "experienceUtils", new WordSetExperienceUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "logger", new LoggerBean());
        Whitebox.setInternalState(presenterFactory, "audioStuffFactory", new AudioStuffFactoryBean());

        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(View.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        practiceWordSetVocabularyFragment = new PracticeWordSetVocabularyFragment();
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "presenterFactory", presenterFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "eventBus", mock(EventBus.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSetVocabularyView", mock(RecyclerView.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));

        int id = 0;
        wordSet = new WordSet();
        wordSet.setId(id);

        int ageWordSetId = id + 1;
        Word2Tokens age = new Word2Tokens("age", "age", ageWordSetId);
        List<Word2Tokens> ageWordSetWords = asList(age, new Word2Tokens(), new Word2Tokens());
        WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgboVdNEXFMlzHK5SR", age.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(ageWordSetId);
        exercise.setWordIndex(ageWordSetWords.indexOf(age));
        daoHelper.getWordRepetitionProgressDao().createNewOrUpdate(exercise);

        WordSet ageWordSet = new WordSet();
        ageWordSet.setId(ageWordSetId);
        ageWordSet.setStatus(WordSetProgressStatus.FINISHED);
        ageWordSet.setWords(new LinkedList<>(ageWordSetWords));
        ageWordSet.setTopicId("topicId");
        ageWordSet.setTrainingExperience(0);
        WordSetMapping ageWordSetMapping = new WordSetMapper(mapper).toMapping(ageWordSet);
        daoHelper.getWordSetDao().createNewOrUpdate(ageWordSetMapping);

        int anniversaryWordSetId = id + 2;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", anniversaryWordSetId);
        List<Word2Tokens> anniversaryWordSetWords = asList(new Word2Tokens(), anniversary, new Word2Tokens());
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgbq6hNEXFMlzHK5Ul", anniversary.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(anniversaryWordSetId);
        exercise.setWordIndex(anniversaryWordSetWords.indexOf(anniversary));
        daoHelper.getWordRepetitionProgressDao().createNewOrUpdate(exercise);

        WordSet anniversaryWordSet = new WordSet();
        anniversaryWordSet.setId(anniversaryWordSetId);
        anniversaryWordSet.setStatus(WordSetProgressStatus.FINISHED);
        anniversaryWordSet.setWords(new LinkedList<>(anniversaryWordSetWords));
        anniversaryWordSet.setTopicId("topicId");
        anniversaryWordSet.setTrainingExperience(0);
        WordSetMapping anniversaryWordSetMapping = new WordSetMapper(mapper).toMapping(anniversaryWordSet);
        daoHelper.getWordSetDao().createNewOrUpdate(anniversaryWordSetMapping);

        int birthWordSetId = id + 3;
        Word2Tokens birth = new Word2Tokens("birth", "birth", birthWordSetId);
        List<Word2Tokens> birthWordSetWords = asList(new Word2Tokens(), new Word2Tokens(), birth);
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgbsUXNEXFMlzHK5V2", birth.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(birthWordSetId);
        exercise.setWordIndex(birthWordSetWords.indexOf(birth));
        daoHelper.getWordRepetitionProgressDao().createNewOrUpdate(exercise);

        WordSet birthWordSet = new WordSet();
        birthWordSet.setId(birthWordSetId);
        birthWordSet.setStatus(WordSetProgressStatus.FINISHED);
        birthWordSet.setWords(new LinkedList<>(birthWordSetWords));
        birthWordSet.setTopicId("topicId");
        birthWordSet.setTrainingExperience(0);
        WordSetMapping birthWordSetMapping = new WordSetMapper(mapper).toMapping(birthWordSet);
        daoHelper.getWordSetDao().createNewOrUpdate(birthWordSetMapping);

        wordSet.setWords(new LinkedList<>(asList(age, anniversary, birth)));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(1);
        wordSet.setStatus(WordSetProgressStatus.FINISHED);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSet", wordSet);
    }

    @Test
    public void testPracticeWordSetVocabularyFragment_repetitionMode() {
        practiceWordSetVocabularyFragment.init();

    }

    private String getSentenceJSON(ObjectMapper mapper, String sentenceId, String word, int lengthInWords) throws JsonProcessingException {
        return mapper.writeValueAsString(singletonList(new SentenceIdMapping(sentenceId, lengthInWords)));
    }
}