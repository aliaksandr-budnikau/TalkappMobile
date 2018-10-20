package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;
import static talkapp.org.talkappmobile.component.AuthSign.AUTHORIZATION_HEADER_KEY;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterAndInteractorIntegTest {
    @Mock
    private PracticeWordSetView view;
    private PracticeWordSetPresenter presenter;

    @Before
    public void setup() {
        final ClassForInjection injection = new ClassForInjection();
        String id = "qwe0";

        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setWords(asList("age", "anniversary", "birth"));
        wordSet.setTopicId("topicId");

        ViewStrategyFactory viewStrategyFactory = injection.getViewStrategyFactory();
        PracticeWordSetInteractor interactor = injection.getInteractor();
        PracticeWordSetViewHideNewWordOnlyStrategy newWordOnlyStrategy = viewStrategyFactory.createPracticeWordSetViewHideNewWordOnlyStrategy(view);
        PracticeWordSetViewHideAllStrategy hideAllStrategy = viewStrategyFactory.createPracticeWordSetViewHideAllStrategy(view);
        presenter = new PracticeWordSetPresenter(wordSet, interactor, newWordOnlyStrategy, hideAllStrategy);

        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("sasha-ne@tut.by");
        credentials.setPassword("password0");
        injection.getLoginService().login(credentials).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                String signature = response.headers().get(AUTHORIZATION_HEADER_KEY);
                injection.getAuthSign().put(signature);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    @Test
    public void test() {
        presenter.initialise();
        presenter.nextButtonClick();
    }
}