package talkapp.org.talkappmobile.activity.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class WaitingForProgressBarManager {

    private final View progressBar;
    private final View form;
    private final int shortAnimTime;

    public WaitingForProgressBarManager(View progressBar, View form, int shortAnimTime) {
        this.progressBar = progressBar;
        this.form = form;
        this.shortAnimTime = shortAnimTime;
    }

    public void showProgressBar() {
        setVisibility(true);
    }

    public void hideProgressBar() {
        setVisibility(false);
    }

    private void setVisibility(final boolean show) {
        form.setVisibility(show ? View.GONE : View.VISIBLE);
        form.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        form.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar
                .animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }
}