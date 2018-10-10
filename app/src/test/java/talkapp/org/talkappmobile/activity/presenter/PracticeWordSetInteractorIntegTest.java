package talkapp.org.talkappmobile.activity.presenter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.PracticeWordSetExerciseTempRepository;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.backend.RefereeService;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static retrofit2.Response.success;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetInteractorIntegTest {
    @Mock
    private PracticeWordSetView view;
    @Mock
    private PracticeWordSetViewStrategy viewStrategy;
    @Mock
    private PracticeWordSetExerciseTempRepository practiceWordSetExerciseTempRepository;
    @Mock
    private RefereeService refereeService;
    @Mock
    private SentenceProvider sentenceProvider;

    @BeforeClass
    public static void setUpContext() {
        DIContext.init(new TalkappMobileApplication());
    }

    @Test
    public void initialiseSentence_throwIndexOutOfBoundsExceptionInHalfFinishedPractice() throws IOException {
        int maxTrainingExperience = 4;

        String wordSetId = "wordSetId";

        WordSet wordSet = new WordSet();
        wordSet.setId(wordSetId);
        String word1 = "word1";
        wordSet.setWords(asList(word1, "word2"));
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setTrainingExperience(0);
        wordSet.getExperience().setMaxTrainingExperience(maxTrainingExperience);

        Sentence sentence = new Sentence();
        sentence.setText("some text");

        AnswerCheckingResult result = new AnswerCheckingResult();
        result.setCurrentTrainingExperience(maxTrainingExperience / 2);
        result.setErrors(new ArrayList<GrammarError>());

        PracticeWordSetPresenter presenter = new PracticeWordSetPresenter(wordSet, view);
        Whitebox.setInternalState(presenter, "viewStrategy", viewStrategy);
        Whitebox.setInternalState(presenter, "practiceWordSetExerciseTempRepository", practiceWordSetExerciseTempRepository);
        whenRefereeServiceCheckAnswer(result);
        presenter.interactor.refereeService = refereeService;
        when(sentenceProvider.findByWordAndWordSetId(word1, wordSetId)).thenReturn(asList(sentence));
        presenter.interactor.sentenceProvider = sentenceProvider;


        presenter.onResume();
        presenter.onNextButtonClick();
        presenter.onNextButtonClick();
        presenter.onCheckAnswerButtonClick("Answer 1");
        presenter.onNextButtonClick();
    }

    private void whenRefereeServiceCheckAnswer(AnswerCheckingResult result) throws IOException {
        Call call = mock(Call.class);
        when(call.execute()).thenReturn(success(result));
        when(refereeService.checkAnswer(any(UncheckedAnswer.class), any(Map.class))).thenReturn(call);
    }
}