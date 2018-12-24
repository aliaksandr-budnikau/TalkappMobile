package talkapp.org.talkappmobile.activity.custom.presenter;

import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.model.Sentence;

public class OriginalTextTextViewPresenter {
    private Sentence sentence;
    private OriginalTextTextViewView view;

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
}