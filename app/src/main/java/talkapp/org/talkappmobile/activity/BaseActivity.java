package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;

public class BaseActivity extends AppCompatActivity {
    @Inject
    InfraComponentsFactory componentsFactory;
    @Inject
    ExceptionHandlerInteractor interactor;
    @Inject
    ExceptionHandlerView exceptionHandlerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIContextUtils.get().inject(this);
        Thread.setDefaultUncaughtExceptionHandler(componentsFactory.createExceptionHandler(exceptionHandlerView, interactor));
    }
}