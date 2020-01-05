package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;

public abstract class WordSetServiceDecorator implements WordSetService {

    private final WordSetService wordSetService;

    public WordSetServiceDecorator(WordSetService wordSetService) {
        this.wordSetService = wordSetService;
    }

    @Override
    public void resetProgress(WordSet wordSet) {
        wordSetService.resetProgress(wordSet);
    }

    @Override
    public void moveToAnotherState(int id, WordSetProgressStatus value) {
        wordSetService.moveToAnotherState(id, value);
    }

    @Override
    public void remove(WordSet wordSet) {
        wordSetService.remove(wordSet);
    }

    @Override
    public int getCustomWordSetsStartsSince() {
        return wordSetService.getCustomWordSetsStartsSince();
    }

    @Override
    public WordSet createNewCustomWordSet(List<WordTranslation> translations) {
        return wordSetService.createNewCustomWordSet(translations);
    }

    @Override
    public void updateWord2Tokens(Word2Tokens newWord2Tokens, Word2Tokens position) {
        wordSetService.updateWord2Tokens(newWord2Tokens, position);
    }

    @Override
    public NewWordSetDraft getNewWordSetDraft() {
        return wordSetService.getNewWordSetDraft();
    }

    @Override
    public void save(NewWordSetDraft draft) {
        wordSetService.save(draft);
    }

    @Override
    public WordSet findById(int id) {
        return wordSetService.findById(id);
    }

    @Override
    public void save(WordSet wordSet) {
        wordSetService.save(wordSet);
    }

    @Override
    public List<WordSet> findAllWordSets() {
        return wordSetService.findAllWordSets();
    }

    @Override
    public void saveWordSets(List<WordSet> incomingSets) {
        wordSetService.saveWordSets(incomingSets);
    }

    @Override
    public List<WordSet> findAllWordSetsLocally() {
        return wordSetService.findAllWordSetsLocally();
    }

    @Override
    public List<WordSet> findWordSetsByTopicId(int topicId) {
        return wordSetService.findWordSetsByTopicId(topicId);
    }

    @Override
    public List<WordSet> findAllWordSetsByTopicId(int topicId) {
        return wordSetService.findAllWordSetsByTopicId(topicId);
    }

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        return wordSetService.getWordSets(topic);
    }
}