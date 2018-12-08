package talkapp.org.talkappmobile.activity.interactor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityListener;
import talkapp.org.talkappmobile.component.backend.BackendServer;

public class MainActivityInteractor {

    private final BackendServer server;
    private final Context context;

    public MainActivityInteractor(BackendServer server, Context context) {
        this.server = server;
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
}