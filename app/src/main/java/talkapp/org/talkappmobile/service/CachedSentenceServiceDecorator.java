package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static talkapp.org.talkappmobile.service.impl.SentenceServiceImpl.WORDS_NUMBER;

public class CachedSentenceServiceDecorator extends SentenceServiceDecorator {
    public CachedSentenceServiceDecorator(SentenceService sentenceService) {
        super(sentenceService);
    }

    @Override
    public List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word) {
        List<Sentence> result;
        try {
            result = super.fetchSentencesFromServerByWordAndWordSetId(word);
        } catch (LocalCacheIsEmptyException e) {
            Map<String, List<Sentence>> body = findSentencesByWordSetId(word.getSourceWordSetId(), WORDS_NUMBER);
            if (body != null) {
                saveSentences(body, WORDS_NUMBER);
            }
            List<Sentence> cached = super.fetchSentencesFromServerByWordAndWordSetId(word);
            result = new LinkedList<>(cached);
        }
        return result;
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        Map<String, List<Sentence>> sentences = super.findSentencesByWordSetId(wordSetId, wordsNumber);
        if (sentences != null) {
            saveSentences(sentences, WORDS_NUMBER);
        }
        return sentences;
    }
}
