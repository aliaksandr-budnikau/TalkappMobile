package talkapp.org.talkappmobile.component.impl;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.Locale;

import talkapp.org.talkappmobile.component.Speaker;

@EBean(scope = EBean.Scope.Singleton)
public class SpeakerBean implements Speaker, TextToSpeech.OnInitListener {

    @RootContext
    Context context;
    private TextToSpeech speech;

    @AfterInject
    public void init() {
        this.speech = new TextToSpeech(context, this);
    }

    @Override
    public void speak(final String text) {
        speech.speak(text, TextToSpeech.QUEUE_ADD, null);
        while (speech.isSpeaking()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            speech.setLanguage(Locale.US);
            speech.setSpeechRate(0.8f);
        }
    }
}