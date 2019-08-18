package talkapp.org.talkappmobile.activity;

import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddingNewWordSetFragmentTest {
    private TextView word1;
    private TextView word2;
    private TextView word3;
    private TextView word4;
    private TextView word5;
    private TextView word6;
    private TextView word7;
    private TextView word8;
    private TextView word9;
    private TextView word10;
    private TextView word11;
    private TextView word12;
    private EventBus eventBus;

    @Test
    public void testOnMessageEventNewWordSuccessfullySubmittedEM() {
        AddingNewWordSetFragment addingNewWordSetFragment = new AddingNewWordSetFragment();
        word1 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word1", word1);
        word2 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word2", word2);
        word3 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word3", word3);
        word4 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word4", word4);
        word5 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word5", word5);
        word6 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word6", word6);
        word7 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word7", word7);
        word8 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word8", word8);
        word9 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word9", word9);
        word10 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word10", word10);
        word11 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word11", word11);
        word12 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word12", word12);
        eventBus = mock(EventBus.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "eventBus", eventBus);

        when(word1.getText()).thenReturn("");
        when(word2.getText()).thenReturn("");
        when(word3.getText()).thenReturn("");
        when(word4.getText()).thenReturn("");
        when(word5.getText()).thenReturn("");
        when(word6.getText()).thenReturn("");
        when(word7.getText()).thenReturn("");
        when(word8.getText()).thenReturn("");
        when(word9.getText()).thenReturn("");
        when(word10.getText()).thenReturn("");
        when(word11.getText()).thenReturn("");
        when(word12.getText()).thenReturn("");

        addingNewWordSetFragment.onMessageEvent(new NewWordSuccessfullySubmittedEM(new WordSet()));

        verify(word1).setText("");
        verify(word2).setText("");
        verify(word3).setText("");
        verify(word4).setText("");
        verify(word5).setText("");
        verify(word6).setText("");
        verify(word7).setText("");
        verify(word8).setText("");
        verify(word9).setText("");
        verify(word10).setText("");
        verify(word11).setText("");
        verify(word12).setText("");
    }
}