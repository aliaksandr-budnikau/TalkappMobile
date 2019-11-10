package talkapp.org.talkappmobile.events;

public class PhraseTranslationInputWasValidatedSuccessfullyEM {
    private final String phrase;
    private final String translation;

    public PhraseTranslationInputWasValidatedSuccessfullyEM(String phrase, String translation) {
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