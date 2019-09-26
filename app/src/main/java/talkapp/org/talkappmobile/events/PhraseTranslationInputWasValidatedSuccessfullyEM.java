package talkapp.org.talkappmobile.events;

public class PhraseTranslationInputWasValidatedSuccessfullyEM {
    private final int adapterPosition;
    private final String phrase;
    private final String translation;

    public PhraseTranslationInputWasValidatedSuccessfullyEM(int adapterPosition, String phrase, String translation) {
        this.adapterPosition = adapterPosition;
        this.phrase = phrase;
        this.translation = translation;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getTranslation() {
        return translation;
    }
}