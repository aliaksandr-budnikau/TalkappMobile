package talkapp.org.talkappmobile.activity.view.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

import talkapp.org.talkappmobile.activity.CrashActivity;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

import static talkapp.org.talkappmobile.activity.CrashActivity.STACK_TRACE;

public class ExceptionHandlerViewImpl implements ExceptionHandlerView {
    private final Context context;
    private final Handler uiEventHandler;

    public ExceptionHandlerViewImpl(Context context, Handler uiEventHandler) {
        this.context = context;
        this.uiEventHandler = uiEventHandler;
    }

    @Override
    public void showToastMessage(final String text) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void openLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void openCrashActivity(Throwable e, String errorReport) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace();
        e.printStackTrace(new PrintWriter(stackTrace));
        Intent intent = new Intent(context, CrashActivity.class);
        intent.putExtra(STACK_TRACE, errorReport);
        context.startActivity(intent);
    }

    @Override
    public void killCurrentActivity() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}