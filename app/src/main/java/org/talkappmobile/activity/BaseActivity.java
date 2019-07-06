package org.talkappmobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.talkappmobile.service.Logger;
import org.talkappmobile.service.impl.LoggerBean;

import org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import org.talkappmobile.activity.view.ExceptionHandlerView;
import org.talkappmobile.activity.view.impl.ExceptionHandlerViewBean;
import org.talkappmobile.component.impl.ExceptionHandler;

@EBean
public class BaseActivity extends AppCompatActivity {
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(ExceptionHandlerViewBean.class)
    ExceptionHandlerView exceptionHandlerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionHandlerInteractor interactor = new ExceptionHandlerInteractor(logger);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(exceptionHandlerView, interactor));
    }
}