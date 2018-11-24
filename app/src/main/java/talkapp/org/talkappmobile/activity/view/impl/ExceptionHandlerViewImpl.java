package talkapp.org.talkappmobile.activity.view.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import talkapp.org.talkappmobile.activity.CrashActivity_;
import talkapp.org.talkappmobile.activity.LoginActivity_;
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
    public void openLoginActivity(Context currentActivityContext) {
        Intent intent = new Intent(currentActivityContext, LoginActivity_.class);
        currentActivityContext.startActivity(intent);
    }

    @Override
    public void openCrashActivity(Context currentActivityContext, Throwable e, String errorReport) {
        Intent intent = new Intent(currentActivityContext, CrashActivity_.class);
        intent.putExtra(STACK_TRACE, errorReport);
        currentActivityContext.startActivity(intent);
    }

    @Override
    public void killCurrentActivity() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}