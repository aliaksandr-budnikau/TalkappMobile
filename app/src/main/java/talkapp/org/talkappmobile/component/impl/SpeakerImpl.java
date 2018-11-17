package talkapp.org.talkappmobile.component.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.concurrent.Executor;

import talkapp.org.talkappmobile.component.Speaker;

public class SpeakerImpl implements Speaker, TextToSpeech.OnInitListener {

    private final TextToSpeech speech;
    private final Executor executor;

    public SpeakerImpl(Context context, Executor executor) {
        this.speech = new TextToSpeech(context, this);
        this.executor = executor;
    }

    @Override
    public AsyncTask<Void, Void, Void> speak(final String text) {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                speech.speak(text, TextToSpeech.QUEUE_ADD, null);
                while (speech.isSpeaking()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
                return null;
            }
        }.executeOnExecutor(executor);
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            speech.setLanguage(Locale.US);
            speech.setSpeechRate(0.8f);
        }
    }
}