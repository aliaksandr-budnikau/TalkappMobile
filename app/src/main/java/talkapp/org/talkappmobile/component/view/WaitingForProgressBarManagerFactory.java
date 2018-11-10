package talkapp.org.talkappmobile.component.view;

import android.view.View;

public class WaitingForProgressBarManagerFactory {
    private final int shortAnimTime;

    public WaitingForProgressBarManagerFactory(int shortAnimTime) {
        this.shortAnimTime = shortAnimTime;
    }

    public WaitingForProgressBarManager get(View progressBar, View form) {
        return new WaitingForProgressBarManager(progressBar, form, shortAnimTime);
    }
}