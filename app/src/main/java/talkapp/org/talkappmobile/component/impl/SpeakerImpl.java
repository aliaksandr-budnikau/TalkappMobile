package talkapp.org.talkappmobile.component.impl;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import talkapp.org.talkappmobile.component.Speaker;

public class SpeakerImpl implements Speaker, TextToSpeech.OnInitListener {

    private final TextToSpeech speech;

    public SpeakerImpl(Context context) {
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