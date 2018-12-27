package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.activity.view.impl.ExceptionHandlerViewBean;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.config.DIContextUtils;

@EBean
public class BaseActivity extends AppCompatActivity {
    @Inject
    InfraComponentsFactory componentsFactory;
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(ExceptionHandlerViewBean.class)
    ExceptionHandlerView exceptionHandlerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIContextUtils.get().inject(this);
        ExceptionHandlerInteractor interactor = new ExceptionHandlerInteractor(logger);
        Thread.setDefaultUncaughtExceptionHandler(componentsFactory.createExceptionHandler(exceptionHandlerView, interactor));
    }
}