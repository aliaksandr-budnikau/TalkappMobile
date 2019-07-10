package talkapp.org.talkappmobile.activity.interactor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.UserExpService;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityListener;

public class MainActivityInteractor {

    private final DataServer server;
    private final UserExpService userExpService;
    private final Context context;

    public MainActivityInteractor(DataServer server, UserExpService userExpService, Context context) {
        this.server = server;
        this.userExpService = userExpService;
        this.context = context;
    }

    public void checkServerAvailability() {
        server.findAllTopics();
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