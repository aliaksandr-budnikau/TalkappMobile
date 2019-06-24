package talkapp.org.talkappmobile.activity.event.wordset;

import org.talkappmobile.model.WordSet;

public class WordSetsRemoveClickedEM {

    private final WordSet wordSet;
    private final int clickedItemNumber;

    public WordSetsRemoveClickedEM(WordSet wordSet, int clickedItemNumber) {
        this.wordSet = wordSet;
        this.clickedItemNumber = clickedItemNumber;
    }

    public int getClickedItemNumber() {
        return clickedItemNumber;
    }

    public WordSet getWordSet() {
        return wordSet;
    }
}
