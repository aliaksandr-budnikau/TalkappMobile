package org.talkappmobile.activity.custom.view;

import java.util.List;

import org.talkappmobile.model.WordSet;

public interface WordSetListAdapterView {
    void onModelPrepared(List<WordSet> wordSetList);

    void onWordSetRemoved();
}