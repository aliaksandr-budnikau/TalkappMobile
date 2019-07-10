package talkapp.org.talkappmobile.activity.custom;

import android.view.View;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.IntegerRes;

@EBean
public class WaitingForProgressBarManagerFactory {

    @IntegerRes(android.R.integer.config_shortAnimTime)
    int shortAnimTime;

    public WaitingForProgressBarManager get(View progressBar, View form) {
        return new WaitingForProgressBarManager(progressBar, form, shortAnimTime);
    }
}