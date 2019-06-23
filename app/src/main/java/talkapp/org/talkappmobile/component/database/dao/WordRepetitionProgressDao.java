package talkapp.org.talkappmobile.component.database.dao;

import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;

public interface WordRepetitionProgressDao {

    List<WordRepetitionProgressMapping> findByWordAndWordSetId(String word, int wordSetId);

    void createNewOrUpdate(WordRepetitionProgressMapping exercise);

    void cleanByWordSetId(int wordSetId);

    int createAll(List<WordRepetitionProgressMapping> words);

    List<WordRepetitionProgressMapping> findByStatusAndByWordSetId(String status, int wordSetId);

    List<WordRepetitionProgressMapping> findByCurrentAndByWordSetId(int wordSetId);

    List<WordRepetitionProgressMapping> findWordSetsSortByUpdatedDateAndByStatus(long limit, Date olderThenInHours, String status);

    List<WordRepetitionProgressMapping> findByWordAndByWordSetIdAndByStatus(String word, int sourceWordSetId, String status);
}