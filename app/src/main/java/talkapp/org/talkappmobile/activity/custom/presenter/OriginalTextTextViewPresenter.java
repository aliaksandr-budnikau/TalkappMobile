package talkapp.org.talkappmobile.activity.custom.presenter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;

public class OriginalTextTextViewPresenter {
    private Sentence sentence;
    private OriginalTextTextViewView view;
    private boolean locked;
    private boolean immutable;

    public OriginalTextTextViewPresenter(OriginalTextTextViewView view) {
        this.view = view;
    }

    public void setModel(Sentence sentence) {
        this.sentence = sentence;
    }

    public void refresh() {
        view.setOriginalText(sentence.getTranslations().get("russian"));
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void unlock() {
        locked = false;
    }

    public void lock() {
        locked = true;
    }

    public void changeSentence() {
        view.onChangeSentence();
    }

    public void prepareDialog(String anotherSentenceOption, String poorSentenceOption, String corruptedSentenceOption, String insultSentenceOption) {
        Map<SentenceContentScore, String> options = new HashMap<>();
        options.put(SentenceContentScore.POOR, poorSentenceOption);
        options.put(SentenceContentScore.CORRUPTED, corruptedSentenceOption);
        options.put(SentenceContentScore.INSULT, insultSentenceOption);

        List<String> optionsList = new LinkedList<>();
        boolean mutable = !locked && !immutable;
        if (mutable) {
            optionsList.add(anotherSentenceOption);
        }
        for (SentenceContentScore value : SentenceContentScore.values()) {
            optionsList.add(options.get(value));
        }
        view.openDialog(optionsList.toArray(new String[SentenceContentScore.values().length]), mutable);
    }

    public void enableImmutableMode() {
        immutable = true;
    }
}