package talkapp.org.talkappmobile.activity.custom.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetListAdapterListener;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetListAdapterInteractor {

    public void prepareModel(List<WordSet> wordSetList, OnWordSetListAdapterListener listener) {
        listener.onModelPrepared(wordSetList);
    }

    public WordSet getWordSet(List<WordSet> wordSetList, int position) {
        return wordSetList.get(position);
    }
}