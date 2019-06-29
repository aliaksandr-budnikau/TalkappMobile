package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.WordSet;

public class WordSetsRemoveClickedEM {
    @NonNull
    private WordSet wordSet;
    @NonNull
    private int clickedItemNumber;

    public WordSetsRemoveClickedEM(@NonNull WordSet wordSet, @NonNull int clickedItemNumber) {
        this.wordSet = wordSet;
        this.clickedItemNumber = clickedItemNumber;
    }

    @NonNull
    public WordSet getWordSet() {
        return wordSet;
    }

    @NonNull
    public int getClickedItemNumber() {
        return clickedItemNumber;
    }
}
