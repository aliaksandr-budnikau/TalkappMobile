package talkapp.org.talkappmobile.activity.custom.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;

public interface OnWordSetListAdapterListener {
    void onModelPrepared(List<WordSet> wordSetList);
}