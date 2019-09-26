package talkapp.org.talkappmobile.events;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public class AddNewWordSetButtonSubmitClickedEM {
    private final List<WordTranslation> wordTranslations;

    public AddNewWordSetButtonSubmitClickedEM(List<WordTranslation> wordTranslations) {
        this.wordTranslations = wordTranslations;
    }

    public List<WordTranslation> getWordTranslations() {
        return wordTranslations;
    }
}