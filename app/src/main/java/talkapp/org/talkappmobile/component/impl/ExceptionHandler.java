package talkapp.org.talkappmobile.component.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.activity.CrashActivity;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationException;
import talkapp.org.talkappmobile.component.backend.impl.InternetConnectionLostException;

import static talkapp.org.talkappmobile.activity.CrashActivity.STACK_TRACE;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private final String LINE_SEPARATOR = "\n";
    private final Context context;
    private final Handler uiEventHandler;

    public ExceptionHandler(Context context, Handler uiEventHandler) {
        this.context = context;
        this.uiEventHandler = uiEventHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (getCause(e) instanceof InternetConnectionLostException) {
            uiEventHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Internet connection was lost", Toast.LENGTH_LONG).show();
                }
            });
            return;
        } else if (getCause(e) instanceof AuthorizationException) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
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

            Intent intent = new Intent(context, CrashActivity.class);
            intent.putExtra(STACK_TRACE, errorReport.toString());
            context.startActivity(intent);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private Throwable getCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}