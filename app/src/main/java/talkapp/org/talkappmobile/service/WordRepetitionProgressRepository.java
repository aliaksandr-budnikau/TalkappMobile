package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.WordRepetitionProgress;

public interface WordRepetitionProgressRepository {
    List<WordRepetitionProgress> findByWordIndexAndWordSetId(int index, Integer sourceWordSetId);
}