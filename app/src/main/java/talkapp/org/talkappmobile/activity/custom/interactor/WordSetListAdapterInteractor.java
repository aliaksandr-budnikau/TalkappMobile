package talkapp.org.talkappmobile.activity.custom.interactor;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetListAdapterListener;
import org.talkappmobile.model.WordSet;

import static org.talkappmobile.model.RepetitionClass.LEARNED;
import static org.talkappmobile.model.RepetitionClass.NEW;
import static org.talkappmobile.model.RepetitionClass.REPEATED;
import static org.talkappmobile.model.RepetitionClass.SEEN;
import static org.talkappmobile.model.WordSetProgressStatus.FINISHED;

public class WordSetListAdapterInteractor {

    private final Filter onlyNewWordSets;
    private final Filter onlyStartedWordSets;
    private final Filter onlyFinishedWordSets;
    private final Filter onlyNewRepWordSets;
    private final Filter onlySeenRepWordSets;
    private final Filter onlyRepeatedRepWordSets;
    private final Filter onlyLearnedRepWordSets;

    public WordSetListAdapterInteractor() {
        onlyNewWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getTrainingExperience() == 0;
            }
        };
        onlyStartedWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getTrainingExperience() != 0 && !FINISHED.equals(wordSet.getStatus());
            }
        };
        onlyFinishedWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return FINISHED.equals(wordSet.getStatus());
            }
        };
        onlyNewRepWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == NEW;
            }
        };
        onlySeenRepWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == SEEN;
            }
        };
        onlyRepeatedRepWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == REPEATED;
            }
        };
        onlyLearnedRepWordSets = new Filter() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == LEARNED;
            }
        };
    }

    public void prepareModel(List<WordSet> wordSetList, OnWordSetListAdapterListener listener) {
        listener.onModelPrepared(wordSetList);
    }

    public WordSet getWordSet(List<WordSet> wordSetList, int position) {
        return wordSetList.get(position);
    }

    public void filterNew(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlyNewWordSets);
    }

    public void filterStarted(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlyStartedWordSets);
    }

    public void filterFinished(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlyFinishedWordSets);
    }

    private void filter(List<WordSet> origList, OnWordSetListAdapterListener listener, Filter filter) {
        LinkedList<WordSet> filtered = new LinkedList<>();
        for (WordSet wordSet : origList) {
            if (filter.filter(wordSet)) {
                filtered.add(wordSet);
            }
        }
        listener.onModelPrepared(filtered);
    }

    public void filterNewRep(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlyNewRepWordSets);
    }

    public void filterSeenRep(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlySeenRepWordSets);
    }

    public void filterRepeatedRep(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlyRepeatedRepWordSets);
    }

    public void filterLearnedRep(List<WordSet> origList, OnWordSetListAdapterListener listener) {
        filter(origList, listener, onlyLearnedRepWordSets);
    }

    public void remove(WordSet wordSet, List<WordSet> wordSetList, List<WordSet> filteredList, OnWordSetListAdapterListener listener) {
        wordSetList.remove(wordSet);
        filteredList.remove(wordSet);
        listener.onWordSetRemoved(wordSet);
    }

    private abstract class Filter {
        abstract boolean filter(WordSet wordSet);
    }
}