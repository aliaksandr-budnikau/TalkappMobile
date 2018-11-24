package talkapp.org.talkappmobile.module;

import android.content.Context;

import talkapp.org.talkappmobile.component.Speaker;

import static org.mockito.Mockito.mock;

public class TestAudioModule extends AudioModule {
    @Override
    public Speaker provideSpeaker(Context context) {
        return mock(Speaker.class);
    }
}