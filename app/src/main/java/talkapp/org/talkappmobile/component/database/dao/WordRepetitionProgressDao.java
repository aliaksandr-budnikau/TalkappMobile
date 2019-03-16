package talkapp.org.talkappmobile.component.database.dao;

import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

public interface WordRepetitionProgressDao {

    List<WordRepetitionProgressMapping> findByWordAndWordSetId(String word, int wordSetId);

    void createNewOrUpdate(WordRepetitionProgressMapping exercise);

    void cleanByWordSetId(int wordSetId);

    int createAll(List<WordRepetitionProgressMapping> words);

    List<WordRepetitionProgressMapping> findByStatusAndByWordSetId(WordSetProgressStatus status, int wordSetId);

    List<WordRepetitionProgressMapping> findByCurrentAndByWordSetId(int wordSetId);

    List<WordRepetitionProgressMapping> findFinishedWordSetsSortByUpdatedDate(long limit, Date olderThenInHours);

    List<WordRepetitionProgressMapping> findByWordAndByStatus(String word, WordSetProgressStatus status);

    List<WordRepetitionProgressMapping> findByWordAndBySentenceAndByStatus(String word, String sentence, WordSetProgressStatus status);
}