package talkapp.org.talkappmobile.service;


import java.io.IOException;
import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface DataSource {
    String getWordSetId();

    WordSet findWordSetById(String wordSetId) throws IOException, WordSetNotFoundException;

    List<Sentence> findSentencesByWords(String words) throws SentenceNotFoundException, IOException;
}
