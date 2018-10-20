package talkapp.org.talkappmobile.activity.presenter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetViewHideNewWordOnlyStrategyTest {

    @Mock
    private TextUtils textUtils;
    @InjectMocks
    private PracticeWordSetViewHideNewWordOnlyStrategy strategy = new PracticeWordSetViewHideNewWordOnlyStrategy(null);

    @BeforeClass
    public static void setUpContext() {
        DIContextUtils.init(new TalkappMobileApplication());
    }

    @Test
    public void hideRightAnswer_oneWord() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("Ben, we know you have it.");
        sentence.getTokens().addAll(asList(new TextToken(), new TextToken(), new TextToken()));
        sentence.getTokens().get(0).setToken("");
        sentence.getTokens().get(1).setStartOffset(8);
        sentence.getTokens().get(1).setEndOffset(12);
        sentence.getTokens().get(1).setToken("know");
        sentence.getTokens().get(2).setToken("");

        String word = "know";

        // when
        strategy.hideRightAnswer(sentence, word);

        // then
        verify(textUtils).hideIntervalsInText(sentence.getText(), asList(8, 12));
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

        String word = "know";

        // when
        strategy.hideRightAnswer(sentence, word);

        // then
        verify(textUtils).hideIntervalsInText(sentence.getText(), Collections.<Integer>emptyList());
    }
}