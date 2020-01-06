package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;

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
    public List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word) {
        return sentenceService.fetchSentencesNotFromServerByWordAndWordSetId(word);
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word) {
        return sentenceService.findByWordAndWordSetId(word);
    }

    @Override
    public List<Sentence> selectSentences(List<Sentence> sentences) {
        return sentenceService.selectSentences(sentences);
    }

    @Override
    public Sentence convertToSentence(WordTranslation wordTranslation) {
        return sentenceService.convertToSentence(wordTranslation);
    }

    @Override
    public void saveSentences(Map<String, List<Sentence>> words2Sentences, int wordsNumber) {
        sentenceService.saveSentences(words2Sentences, wordsNumber);
    }

    @Override
    public void orderByScore(List<Sentence> sentences) {
        sentenceService.orderByScore(sentences);
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        return sentenceService.findSentencesByWordSetId(wordSetId, wordsNumber);
    }

}
