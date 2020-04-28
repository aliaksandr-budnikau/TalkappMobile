package talkapp.org.talkappmobile.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;

public interface OnWordSetListAdapterListener {
    void onModelPrepared(List<WordSet> wordSetList);

    void onWordSetRemoved(WordSet wordSet);
}