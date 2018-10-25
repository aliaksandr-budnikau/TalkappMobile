package talkapp.org.talkappmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;

public class BaseActivity extends AppCompatActivity {
    @Inject
    InfraComponentsFactory componentsFactory;
    @Inject
    Context context;
    @Inject
    Handler uiEventHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIContextUtils.get().inject(this);
        Thread.setDefaultUncaughtExceptionHandler(componentsFactory.createExceptionHandler(context, uiEventHandler));
    }
}