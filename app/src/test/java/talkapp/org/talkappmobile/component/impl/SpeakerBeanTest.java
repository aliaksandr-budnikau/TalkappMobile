package talkapp.org.talkappmobile.component.impl;

import android.speech.tts.TextToSpeech;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.Locale;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpeakerBeanTest {
    @Mock
    private TextToSpeech speech;
    private SpeakerBean speaker;

    @Before
    public void init() {
        speaker = new SpeakerBean();
        Whitebox.setInternalState(speaker, "speech", speech);
    }

    @Test
    public void speak_localeAlreadySet() {
        // when
        when(speech.getLanguage()).thenReturn(Locale.US);
        speaker.speak("dslls jdsjf;l");

        // then
        verify(speech, times(0)).setLanguage(Locale.US);
        verify(speech).isSpeaking();
    }

    @Test
    public void speak_localeWasLost() {
        // when
        when(speech.getLanguage()).thenReturn(Locale.ITALY);
        speaker.speak("dslls jdsjf;l");

        // then
        verify(speech).setLanguage(Locale.US);
        verify(speech).isSpeaking();
    }
}