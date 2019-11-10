package talkapp.org.talkappmobile.events;

public class PhraseTranslationInputPopupOkClickedEM {
    private final String phrase;
    private final String translation;

    public PhraseTranslationInputPopupOkClickedEM(String phrase, String translation) {
        this.phrase = phrase;
        this.translation = translation;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getTranslation() {
        return translation;
    }
}