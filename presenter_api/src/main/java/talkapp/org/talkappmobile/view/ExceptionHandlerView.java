package talkapp.org.talkappmobile.view;

public interface ExceptionHandlerView {
    void showToastMessage(String text);

    void openCrashActivity(Throwable e, String errorReport);

    void killCurrentActivity();
}