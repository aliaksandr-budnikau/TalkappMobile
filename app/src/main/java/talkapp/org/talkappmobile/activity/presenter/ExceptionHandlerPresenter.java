package talkapp.org.talkappmobile.activity.presenter;

import android.os.Build;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.listener.ExceptionHandlerListner;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

public class ExceptionHandlerPresenter implements ExceptionHandlerListner {
    private final String LINE_SEPARATOR = "\n";
    private final ExceptionHandlerInteractor interactor;
    private final ExceptionHandlerView view;

    public ExceptionHandlerPresenter(ExceptionHandlerView view, ExceptionHandlerInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void handleInternetConnectionLostException(Thread t, Throwable e, Throwable cause) {
        interactor.handleInternetConnectionLostException(this);
    }

    @Override
    public void onInternetConnectionLost() {
        view.showToastMessage("Internet connection was lost");
    }

    @Override
    public void onUnauthorizedAccess() {
        view.openLoginActivity();
        view.killCurrentActivity();
    }

    @Override
    public void onUncaughtException(Throwable e) {
        String errorReport = "************ CAUSE OF ERROR ************\n\n" +
                "\n************ DEVICE INFORMATION ***********\n" +
                "Brand: " + Build.BRAND + LINE_SEPARATOR +
                "Device: " + Build.DEVICE + LINE_SEPARATOR +
                "Model: " + Build.MODEL + LINE_SEPARATOR +
                "Id: " + Build.ID + LINE_SEPARATOR +
                "Product: " + Build.PRODUCT + LINE_SEPARATOR +
                "\n************ FIRMWARE ************\n" +
                "SDK: " + Build.VERSION.SDK + LINE_SEPARATOR +
                "Release: " + Build.VERSION.RELEASE + LINE_SEPARATOR +
                "Incremental: " + Build.VERSION.INCREMENTAL + LINE_SEPARATOR;
        view.openCrashActivity(e, errorReport);
        view.killCurrentActivity();
    }

    public void handleAuthorizationException(Thread t, Throwable e, Throwable cause) {
        interactor.handleAuthorizationException(this);
    }

    public void handleUncaughtException(Thread t, Throwable e, Throwable cause) {
        interactor.handleUncaughtException(this, e);
    }
}