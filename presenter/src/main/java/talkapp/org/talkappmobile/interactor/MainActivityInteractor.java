package talkapp.org.talkappmobile.interactor;

import talkapp.org.talkappmobile.listener.OnMainActivityListener;
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.UserExpService;

public class MainActivityInteractor {

    private final UserExpService userExpService;
    private final String versionName;
    private final TopicService topicService;

    public MainActivityInteractor(TopicService topicService, UserExpService userExpService, String versionName) {
        this.topicService = topicService;
        this.userExpService = userExpService;
        this.versionName = versionName;
    }

    public void checkServerAvailability() {
        topicService.findAllTopics();
    }

    public void initAppVersion(OnMainActivityListener listener) {
        listener.onAppVersionInitialized(versionName);
    }

    public void initYourExp(OnMainActivityListener listener) {
        double exp = userExpService.getOverallExp();
        listener.onYourExpInitialized(exp);
    }
}