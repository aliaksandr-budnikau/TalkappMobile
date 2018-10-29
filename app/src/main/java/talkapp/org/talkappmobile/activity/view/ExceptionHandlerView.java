package talkapp.org.talkappmobile.activity.view;

public interface ExceptionHandlerView {
    void showToastMessage(String text);

    void openLoginActivity();

    void openCrashActivity(Throwable e, String errorReport);

    void killCurrentActivity();
}