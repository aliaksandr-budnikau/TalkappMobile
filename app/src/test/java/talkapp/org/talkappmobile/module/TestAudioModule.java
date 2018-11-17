package talkapp.org.talkappmobile.module;

import android.content.Context;

import java.util.concurrent.Executor;

import talkapp.org.talkappmobile.component.Speaker;

import static org.mockito.Mockito.mock;

public class TestAudioModule extends AudioModule {
    @Override
    public Speaker provideSpeaker(Context context, Executor executor) {
        return mock(Speaker.class);
    }
}