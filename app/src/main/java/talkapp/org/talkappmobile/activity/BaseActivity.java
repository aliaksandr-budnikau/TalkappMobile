package talkapp.org.talkappmobile.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.PresenterFactory;
import talkapp.org.talkappmobile.activity.view.impl.ExceptionHandlerViewBean;
import talkapp.org.talkappmobile.component.BeanFactory;
import talkapp.org.talkappmobile.component.impl.ExceptionHandler;
import talkapp.org.talkappmobile.presenter.ExceptionHandlerPresenter;
import talkapp.org.talkappmobile.view.ExceptionHandlerView;

@EBean
public class BaseActivity extends AppCompatActivity {
    @Bean(ExceptionHandlerViewBean.class)
    ExceptionHandlerView exceptionHandlerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PresenterFactory presenterFactory = BeanFactory.presenterFactory(getApplicationContext());
        ExceptionHandlerPresenter presenter = presenterFactory.create(exceptionHandlerView);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(presenter));
    }
}