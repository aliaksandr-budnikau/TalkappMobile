package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

class SentenceServiceDecorator implements SentenceService {
    private final SentenceService sentenceService;

    public SentenceServiceDecorator(SentenceService sentenceService) {
        this.sentenceService = sentenceService;
    }

    @Override
    public boolean classifySentence(Sentence sentence) {
        return sentenceService.classifySentence(sentence);
    }

    @Override
    public List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word) {
        return sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word) {
        return sentenceService.findByWordAndWordSetId(word);
    }

    @Override
    public void saveSentences(Map<String, List<Sentence>> words2Sentences, int wordsNumber) {
        sentenceService.saveSentences(words2Sentences, wordsNumber);
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        return sentenceService.findSentencesByWordSetId(wordSetId, wordsNumber);
    }
}