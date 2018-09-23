package talkapp.org.talkappmobile.component.impl;

import android.content.Intent;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.activity.BaseActivity;
import talkapp.org.talkappmobile.activity.CrashActivity;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationException;

import static talkapp.org.talkappmobile.activity.CrashActivity.STACK_TRACE;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private final String LINE_SEPARATOR = "\n";
    private BaseActivity activity;

    public ExceptionHandler(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e.getCause() instanceof AuthorizationException) {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        } else {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace();
            e.printStackTrace(new PrintWriter(stackTrace));
            final StringBuilder errorReport = new StringBuilder();
            errorReport.append("************ CAUSE OF ERROR ************\n\n");
            errorReport.append(stackTrace.toString());

            errorReport.append("\n************ DEVICE INFORMATION ***********\n");
            errorReport.append("Brand: ");
            errorReport.append(Build.BRAND);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Device: ");
            errorReport.append(Build.DEVICE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Model: ");
            errorReport.append(Build.MODEL);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Id: ");
            errorReport.append(Build.ID);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Product: ");
            errorReport.append(Build.PRODUCT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\n************ FIRMWARE ************\n");
            errorReport.append("SDK: ");
            errorReport.append(Build.VERSION.SDK);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Release: ");
            errorReport.append(Build.VERSION.RELEASE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Incremental: ");
            errorReport.append(Build.VERSION.INCREMENTAL);
            errorReport.append(LINE_SEPARATOR);

            Intent intent = new Intent(activity, CrashActivity.class);
            intent.putExtra(STACK_TRACE, errorReport.toString());
            activity.startActivity(intent);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}