package talkapp.org.talkappmobile.module;

import org.androidannotations.annotations.EBean;

import retrofit2.Retrofit;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;

import static org.mockito.Mockito.mock;

@EBean
public class TestBackEndServiceModule extends BackEndServiceModule {

    @Override
    public TopicRestClient provideTopicService(Retrofit retrofit) {
        return mock(TopicRestClient.class);
    }
}