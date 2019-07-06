package org.talkappmobile.service;

import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordSetProgressStatus;
import org.talkappmobile.model.WordTranslation;

import java.util.List;

public interface WordSetService {

    void resetProgress(WordSet wordSet);

    int increaseExperience(WordSet wordSet, int value);

    void moveToAnotherState(int id, WordSetProgressStatus value);

    void remove(WordSet wordSet);

    int getCustomWordSetsStartsSince();

    WordSet createNewCustomWordSet(List<WordTranslation> translations);
}