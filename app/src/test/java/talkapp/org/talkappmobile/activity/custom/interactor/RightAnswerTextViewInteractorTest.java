package talkapp.org.talkappmobile.activity.custom.interactor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.TextToken;
import org.talkappmobile.model.Word2Tokens;

import java.util.Collections;

import talkapp.org.talkappmobile.activity.custom.listener.OnRightAnswerTextViewListener;
import talkapp.org.talkappmobile.component.TextUtils;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RightAnswerTextViewInteractorTest {
    @Mock
    private TextUtils textUtils;
    @Mock
    private OnRightAnswerTextViewListener listener;
    @InjectMocks
    private RightAnswerTextViewInteractor interactor;

    @Test
    public void hideRightAnswer_oneWord() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ben, we know you have it.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setStartOffset(8);
        sentence.getTokens().get(1).setEndOffset(12);
        String know = "know";
        sentence.getTokens().get(1).setToken(know);
        sentence.getTokens().get(2).setToken("");

        Word2Tokens word = new Word2Tokens(know, know, 4);

        // when
        when(textUtils.hideIntervalsInText(anyString(), ArgumentMatchers.<Integer>anyList())).thenReturn("test");
        interactor.maskOnlyWord(sentence, word, false, listener);

        // then
        verify(textUtils).hideIntervalsInText(sentence.getText(), asList(8, 12));
        verify(listener).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_oneWordBugWithIcing() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ice the girl and beat it.");
        sentence.getTokens().addAll(singletonList(new TextToken()));
        sentence.getTokens().get(0).setToken("ice");
        sentence.getTokens().get(0).setStartOffset(0);
        sentence.getTokens().get(0).setEndOffset(3);
        sentence.getTokens().get(0).setPosition(0);
        Word2Tokens word = new Word2Tokens("icing", "icing,ice", 3);

        // when
        when(textUtils.hideIntervalsInText(anyString(), ArgumentMatchers.<Integer>anyList())).thenReturn("test");
        interactor.maskOnlyWord(sentence, word, false, listener);

        // then
        verify(textUtils).hideIntervalsInText(sentence.getText(), asList(0, 3));
        verify(listener).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_oneWordBugWithGreeting() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Greetings to your parents.");
        sentence.getTokens().addAll(singletonList(new TextToken()));
        sentence.getTokens().get(0).setToken("greet");
        sentence.getTokens().get(0).setStartOffset(0);
        sentence.getTokens().get(0).setEndOffset(9);
        sentence.getTokens().get(0).setPosition(0);
        Word2Tokens word = new Word2Tokens("greeting", "greeting,greet", 3);

        // when
        when(textUtils.hideIntervalsInText(anyString(), ArgumentMatchers.<Integer>anyList())).thenReturn("test");
        interactor.maskOnlyWord(sentence, word, false, listener);

        // then
        verify(textUtils).hideIntervalsInText(sentence.getText(), asList(0, 9));
        verify(listener).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_zeroWord() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ben, we know you have it.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setStartOffset(8);
        sentence.getTokens().get(1).setEndOffset(12);
        sentence.getTokens().get(1).setToken("know2");
        sentence.getTokens().get(2).setToken("");

        String know = "know";
        Word2Tokens word = new Word2Tokens(know, know, 5);

        // when
        when(textUtils.hideIntervalsInText(anyString(), ArgumentMatchers.<Integer>anyList())).thenReturn("test");
        interactor.maskOnlyWord(sentence, word, false, listener);

        // then
        verify(textUtils).hideIntervalsInText(sentence.getText(), Collections.<Integer>emptyList());
        verify(listener).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_earthEarBug() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("We visited earth.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setToken("");

        sentence.getTokens().get(2).setStartOffset(11);
        sentence.getTokens().get(2).setEndOffset(15);
        sentence.getTokens().get(2).setToken("earth");

        sentence.getTokens().get(3).setStartOffset(11);
        sentence.getTokens().get(3).setEndOffset(15);
        sentence.getTokens().get(3).setToken("ear");

        Word2Tokens word = new Word2Tokens("earth", "earth,ear", 3);

        // when
        when(textUtils.hideIntervalsInText(anyString(), ArgumentMatchers.<Integer>anyList())).thenReturn("test");
        interactor.maskOnlyWord(sentence, word, false, listener);

        // then
        verify(textUtils).hideIntervalsInText("We visited earth.", asList(11, 15));
        verify(listener).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_oneWordAndLocked() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ben, we know you have it.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setStartOffset(8);
        sentence.getTokens().get(1).setEndOffset(12);
        sentence.getTokens().get(1).setToken("know");
        sentence.getTokens().get(2).setToken("");

        Word2Tokens word = new Word2Tokens("know", "know", 3);

        // when
        interactor.maskOnlyWord(sentence, word, true, listener);

        // then
        verify(textUtils, times(0)).hideIntervalsInText(sentence.getText(), asList(8, 12));
        verify(listener, times(0)).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_oneWordBugWithIcingAndLocked() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ice the girl and beat it.");
        sentence.getTokens().addAll(singletonList(new TextToken()));
        sentence.getTokens().get(0).setToken("ice");
        sentence.getTokens().get(0).setStartOffset(0);
        sentence.getTokens().get(0).setEndOffset(3);
        sentence.getTokens().get(0).setPosition(0);
        Word2Tokens word = new Word2Tokens("icing", "icing,ice", 3);

        // when
        interactor.maskOnlyWord(sentence, word, true, listener);

        // then
        verify(textUtils, times(0)).hideIntervalsInText(sentence.getText(), asList(0, 3));
        verify(listener, times(0)).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_oneWordBugWithGreetingAndLocked() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Greetings to your parents.");
        sentence.getTokens().addAll(singletonList(new TextToken()));
        sentence.getTokens().get(0).setToken("greet");
        sentence.getTokens().get(0).setStartOffset(0);
        sentence.getTokens().get(0).setEndOffset(9);
        sentence.getTokens().get(0).setPosition(0);
        Word2Tokens word = new Word2Tokens("greeting", "greeting,greet", 1);

        // when
        interactor.maskOnlyWord(sentence, word, true, listener);

        // then
        verify(textUtils, times(0)).hideIntervalsInText(sentence.getText(), asList(0, 9));
        verify(listener, times(0)).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_zeroWordAndLocked() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ben, we know you have it.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setStartOffset(8);
        sentence.getTokens().get(1).setEndOffset(12);
        sentence.getTokens().get(1).setToken("know2");
        sentence.getTokens().get(2).setToken("");

        String know = "know";
        Word2Tokens word = new Word2Tokens(know, know, 3);

        // when
        interactor.maskOnlyWord(sentence, word, true, listener);

        // then
        verify(textUtils, times(0)).hideIntervalsInText(sentence.getText(), Collections.<Integer>emptyList());
        verify(listener, times(0)).onNewValue("test");
    }

    @Test
    public void hideRightAnswer_earthEarBugAndLocked() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("We visited earth.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setToken("");

        sentence.getTokens().get(2).setStartOffset(11);
        sentence.getTokens().get(2).setEndOffset(15);
        sentence.getTokens().get(2).setToken("earth");

        sentence.getTokens().get(3).setStartOffset(11);
        sentence.getTokens().get(3).setEndOffset(15);
        sentence.getTokens().get(3).setToken("ear");

        Word2Tokens word = new Word2Tokens("earth", "earth,ear", 3);

        // when
        interactor.maskOnlyWord(sentence, word, true, listener);

        // then
        verify(textUtils, times(0)).hideIntervalsInText("We visited earth.", asList(11, 15));
        verify(listener, times(0)).onNewValue("test");
    }
}