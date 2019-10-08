package talkapp.org.talkappmobile.dao;

import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;

public interface WordRepetitionProgressDao {

    List<WordRepetitionProgressMapping> findByWordAndWordSetId(String word, int wordSetId);

    void createNewOrUpdate(WordRepetitionProgressMapping exercise);

    void cleanByWordSetId(int wordSetId);

    int createAll(List<WordRepetitionProgressMapping> words);

    List<WordRepetitionProgressMapping> findByStatusAndByWordSetId(String status, int wordSetId);

    List<WordRepetitionProgressMapping> findByCurrentAndByWordSetId(int wordSetId);

    List<WordRepetitionProgressMapping> findWordSetsSortByUpdatedDateAndByStatus(long limit, Date olderThenInHours, String status);

    List<WordRepetitionProgressMapping> findByWordIndexAndByWordSetIdAndByStatus(int wordIndex, int sourceWordSetId, String status);

    List<WordRepetitionProgressMapping> findAll();
}