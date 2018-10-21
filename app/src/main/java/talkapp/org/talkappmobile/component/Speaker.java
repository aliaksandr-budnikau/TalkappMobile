package talkapp.org.talkappmobile.component;

import android.os.AsyncTask;

public interface Speaker {
    AsyncTask<Void, Void, Void> speak(String text);
}