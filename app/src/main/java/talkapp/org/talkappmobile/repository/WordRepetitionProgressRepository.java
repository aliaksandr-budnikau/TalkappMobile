package talkapp.org.talkappmobile.repository;

import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.model.WordRepetitionProgress;

public interface WordRepetitionProgressRepository {
    List<WordRepetitionProgress> findByWordIndexAndWordSetId(int index, Integer sourceWordSetId);

    void createNewOrUpdate(WordRepetitionProgress progress);

    void cleanByWordSetId(int wordSetId);

    void createAll(List<WordRepetitionProgress> wordsEx);

    List<WordRepetitionProgress> findWordSetsSortByUpdatedDateAndByStatus(long l, Date time, String name);

    List<WordRepetitionProgress> findByWordIndexAndByWordSetIdAndByStatus(int index, int wordSetId, String name);
}