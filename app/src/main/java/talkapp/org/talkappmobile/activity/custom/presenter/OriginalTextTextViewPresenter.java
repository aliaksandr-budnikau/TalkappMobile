package talkapp.org.talkappmobile.activity.custom.presenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class OriginalTextTextViewPresenter {
    private Sentence sentence;
    private OriginalTextTextViewView view;
    private boolean locked;

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

    public void changeSentence(Word2Tokens word) {
        view.onChangeSentence(word);
    }

    public void prepareDialog(Word2Tokens word, String anotherSentenceOption, String poorSentenceOption, String corruptedSentenceOption, String insultSentenceOption) {
        Map<SentenceContentScore, String> options = new HashMap<>();
        options.put(SentenceContentScore.POOR, poorSentenceOption);
        options.put(SentenceContentScore.CORRUPTED, corruptedSentenceOption);
        options.put(SentenceContentScore.INSULT, insultSentenceOption);

        List<String> optionsList = new LinkedList<>();
        boolean mutable = !locked;
        if (mutable) {
            optionsList.add(anotherSentenceOption);
        }
        for (SentenceContentScore value : SentenceContentScore.values()) {
            optionsList.add(options.get(value));
        }
        view.openDialog(word, optionsList.toArray(new String[SentenceContentScore.values().length]), mutable);
    }

    public void prepareSentencesForPicking(List<Sentence> sentences, List<Sentence> alreadyPickedSentences, Word2Tokens word) {
        LinkedList<String> options = new LinkedList<>();
        for (Sentence sentence : sentences) {
            options.add(sentence.getTranslations().get("russian"));
        }
        HashSet<Sentence> sentencesIdsSet = new HashSet<>(alreadyPickedSentences);
        boolean[] selectedOnes = new boolean[sentences.size()];
        for (int i = 0; i < sentences.size(); i++) {
            selectedOnes[i] = sentencesIdsSet.contains(sentences.get(i));
        }
        view.openDialogForPickingNewSentence(word, options.toArray(new String[sentences.size()]), sentences, selectedOnes);
    }
}