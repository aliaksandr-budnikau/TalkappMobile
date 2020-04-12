package talkapp.org.talkappmobile.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.activity.view.impl.ExceptionHandlerViewBean;
import talkapp.org.talkappmobile.component.impl.ExceptionHandler;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

@EBean
public class BaseActivity extends AppCompatActivity {
    @Bean(ExceptionHandlerViewBean.class)
    ExceptionHandlerView exceptionHandlerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceFactory serviceFactory = ServiceFactoryBean.getInstance(getApplicationContext());
        ExceptionHandlerInteractor interactor = new ExceptionHandlerInteractor(serviceFactory.getLogger());
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(exceptionHandlerView, interactor));
    }
}