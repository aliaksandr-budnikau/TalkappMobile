package talkapp.org.talkappmobile.events;

public class NewWordTranslationWasNotFoundEM {
    private final int wordIndex;

    public NewWordTranslationWasNotFoundEM(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public int getWordIndex() {
        return wordIndex;
    }
}