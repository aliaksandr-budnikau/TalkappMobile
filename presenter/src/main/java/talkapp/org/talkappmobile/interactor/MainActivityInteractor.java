package talkapp.org.talkappmobile.interactor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import talkapp.org.talkappmobile.listener.OnMainActivityListener;
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.UserExpService;

public class MainActivityInteractor {

    private final UserExpService userExpService;
    private final Context context;
    private final TopicService topicService;

    public MainActivityInteractor(TopicService topicService, UserExpService userExpService, Context context) {
        this.topicService = topicService;
        this.userExpService = userExpService;
        this.context = context;
    }

    public void checkServerAvailability() {
        topicService.findAllTopics();
    }

    public void initAppVersion(OnMainActivityListener listener) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo("talkapp.org.talkappmobile", 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        listener.onAppVersionInitialized(info.versionName);
    }

    public void initYourExp(OnMainActivityListener listener) {
        double exp = userExpService.getOverallExp();
        listener.onYourExpInitialized(exp);
    }
}