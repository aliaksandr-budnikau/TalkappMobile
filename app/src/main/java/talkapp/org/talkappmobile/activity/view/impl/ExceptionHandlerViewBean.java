package talkapp.org.talkappmobile.activity.view.impl;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import talkapp.org.talkappmobile.activity.CrashActivity_;
import talkapp.org.talkappmobile.activity.LoginActivity_;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

import static talkapp.org.talkappmobile.activity.CrashActivity.STACK_TRACE;

@EBean
public class ExceptionHandlerViewBean implements ExceptionHandlerView {

    @RootContext
    Context context;

    @Override
    @UiThread
    public void showToastMessage(final String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
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