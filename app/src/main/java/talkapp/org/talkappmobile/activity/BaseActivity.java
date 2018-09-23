package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.config.DIContext;

public class BaseActivity extends AppCompatActivity {
    @Inject
    InfraComponentsFactory componentsFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIContext.get().inject(this);
        Thread.UncaughtExceptionHandler handler = componentsFactory.createExceptionHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}