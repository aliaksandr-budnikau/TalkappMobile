package talkapp.org.talkappmobile.module;

import retrofit2.Retrofit;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;

import static org.mockito.Mockito.mock;

public class TestBackEndServiceModule extends BackEndServiceModule {

    @Override
    public TopicRestClient provideTopicService(Retrofit retrofit) {
        return mock(TopicRestClient.class);
    }
}