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
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;

import static talkapp.org.talkappmobile.activity.CrashActivity.STACK_TRACE;

public class ExceptionHandlerViewImpl implements ExceptionHandlerView {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private final Context context;
    private final Handler uiEventHandler;
    private final Logger logger;

    public ExceptionHandlerViewImpl(Context context, Handler uiEventHandler, Logger logger) {
        this.context = context;
        this.uiEventHandler = uiEventHandler;
        this.logger = logger;
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
        logger.e(TAG, e.getMessage(), e);
        Intent intent = new Intent(context, CrashActivity.class);
        intent.putExtra(STACK_TRACE, errorReport);
        context.startActivity(intent);
    }
}