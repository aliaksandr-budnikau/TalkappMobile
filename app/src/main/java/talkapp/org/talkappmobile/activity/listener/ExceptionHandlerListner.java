package talkapp.org.talkappmobile.activity.listener;

public interface ExceptionHandlerListner {
    void onInternetConnectionLost();

    void onUnauthorizedAccess();

    void onUncaughtException(Throwable e);
}