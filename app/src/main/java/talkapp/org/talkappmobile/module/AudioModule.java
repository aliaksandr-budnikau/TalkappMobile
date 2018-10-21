package talkapp.org.talkappmobile.module;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class AudioModule {
    private TextToSpeech speech;

    @Provides
    @Singleton
    public AudioStuffFactory provideAudioStuffFactory() {
        return new AudioStuffFactoryImpl();
    }

    @Provides
    @Singleton
    public TextToSpeech provideTextToSpeech(Context context) {
        speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);
                    speech.setSpeechRate(1.0f);
                }
            }
        });
        return speech;
    }
}