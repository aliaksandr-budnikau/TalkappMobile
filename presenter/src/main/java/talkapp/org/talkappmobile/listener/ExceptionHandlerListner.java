package talkapp.org.talkappmobile.listener;

public interface ExceptionHandlerListner {
    void onInternetConnectionLost();

    void onUncaughtException(Throwable e);

    void onLocalCacheIsEmpty();
}