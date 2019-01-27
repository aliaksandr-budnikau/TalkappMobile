package talkapp.org.talkappmobile.activity.listener;

public interface ExceptionHandlerListner {
    void onInternetConnectionLost();

    void onUncaughtException(Throwable e);

    void onLocalCacheIsEmpty();
}