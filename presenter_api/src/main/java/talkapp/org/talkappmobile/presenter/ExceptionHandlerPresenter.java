package talkapp.org.talkappmobile.presenter;

public interface ExceptionHandlerPresenter {
    void handleInternetConnectionLostException(Thread t, Throwable e, Throwable cause);

    void handleUncaughtException(Thread t, Throwable e, Throwable cause);

    void handleLocalCacheIsEmptyException(Thread t, Throwable e, Throwable cause);
}
