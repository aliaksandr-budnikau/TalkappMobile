package talkapp.org.talkappmobile.activity.view;

import android.content.Context;

public interface ExceptionHandlerView {
    void showToastMessage(String text);

    void openLoginActivity(Context currentActivityContext);

    void openCrashActivity(Context currentActivityContext, Throwable e, String errorReport);

    void killCurrentActivity();
}