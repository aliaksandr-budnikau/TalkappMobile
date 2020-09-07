package talkapp.org.talkappmobile.presenter;

import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;

import talkapp.org.talkappmobile.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.listener.ExceptionHandlerListner;
import talkapp.org.talkappmobile.view.ExceptionHandlerView;

public class ExceptionHandlerPresenterImpl implements ExceptionHandlerListner, ExceptionHandlerPresenter {
    private final ExceptionHandlerInteractor interactor;
    private final ExceptionHandlerView view;

    public ExceptionHandlerPresenterImpl(ExceptionHandlerView view, ExceptionHandlerInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void handleInternetConnectionLostException(Thread t, Throwable e, Throwable cause) {
        interactor.handleInternetConnectionLostException(this);
    }

    @Override
    public void onInternetConnectionLost() {
        view.showToastMessage("Internet connection was lost");
    }

    @Override
    public void onUncaughtException(Throwable e) {
        String lineSeparator = "\n";
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        String errorReport = "************ CAUSE OF ERROR ************\n\n" + stackTrace.toString() +
                "\n************ DEVICE INFORMATION ***********\n" +
                "Brand: " + Build.BRAND + lineSeparator +
                "Device: " + Build.DEVICE + lineSeparator +
                "Model: " + Build.MODEL + lineSeparator +
                "Id: " + Build.ID + lineSeparator +
                "Product: " + Build.PRODUCT + lineSeparator +
                "\n************ FIRMWARE ************\n" +
                "SDK: " + Build.VERSION.SDK + lineSeparator +
                "Release: " + Build.VERSION.RELEASE + lineSeparator +
                "Incremental: " + Build.VERSION.INCREMENTAL + lineSeparator;
        view.openCrashActivity(e, errorReport);
        view.killCurrentActivity();
    }

    @Override
    public void onLocalCacheIsEmpty() {
        view.showToastMessage("You need internet connection to preload this task.");
    }

    @Override
    public void handleUncaughtException(Thread t, Throwable e, Throwable cause) {
        interactor.handleUncaughtException(this, t, e, cause);
    }

    @Override
    public void handleLocalCacheIsEmptyException(Thread t, Throwable e, Throwable cause) {
        interactor.handleLocalCacheIsEmptyException(this);
    }
}