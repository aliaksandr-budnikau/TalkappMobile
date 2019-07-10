package talkapp.org.talkappmobile.activity.interactor.impl;

import java.util.Comparator;

import talkapp.org.talkappmobile.model.WordSet;

public class WordSetComparator implements Comparator<WordSet> {
    private static final int MAX_TOP_VALUE = 20000;

    @Override
    public int compare(WordSet o1, WordSet o2) {
        int o1WordsSize = (o1.getWords() == null ? 0 : o1.getWords().size());
        int o2WordsSize = (o2.getWords() == null ? 0 : o2.getWords().size());
        int result = o2WordsSize - o1WordsSize;
        if (result != 0) {
            return result;
        }

        int o1Top = (o1.getTop() == null ? MAX_TOP_VALUE : o1.getTop());
        int o2Top = (o2.getTop() == null ? MAX_TOP_VALUE : o2.getTop());
        result = o1Top - o2Top;
        if (result != 0) {
            return result;
        }

        return o1.getId() - o2.getId();
    }
}