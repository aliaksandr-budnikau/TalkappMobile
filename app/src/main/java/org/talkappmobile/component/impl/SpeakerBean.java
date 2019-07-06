package org.talkappmobile.component.impl;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.Locale;

import org.talkappmobile.component.Speaker;

@EBean(scope = EBean.Scope.Singleton)
public class SpeakerBean implements Speaker, TextToSpeech.OnInitListener {

    public static final Locale US = Locale.US;
    @RootContext
    Context context;
    private TextToSpeech speech;

    @AfterInject
    public void init() {
        this.speech = new TextToSpeech(context, this);
    }

    @Override
    public void speak(final String text) {
        if (!speech.getLanguage().equals(US)) {
            onInit(0);
        }
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
            speech.setLanguage(US);
            speech.setSpeechRate(0.8f);
        }
    }
}