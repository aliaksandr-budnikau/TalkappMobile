package talkapp.org.talkappmobile.component.database;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;

public interface LocalDataService {
    List<WordSet> findAllWordSets();

    void saveWordSets(List<WordSet> wordSets);

    List<WordSet> findAllWordSetsFromMemCache();
}