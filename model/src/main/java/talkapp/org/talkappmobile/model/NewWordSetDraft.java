package talkapp.org.talkappmobile.model;

import java.util.List;

public class NewWordSetDraft {
    private final List<WordTranslation> wordTranslations;

    public NewWordSetDraft(List<WordTranslation> wordTranslations) {
        this.wordTranslations = wordTranslations;
    }


    public List<WordTranslation> getWordTranslations() {
        return wordTranslations;
    }
}