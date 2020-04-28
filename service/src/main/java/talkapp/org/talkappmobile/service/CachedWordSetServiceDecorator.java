package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.service.WordSetService;

public class CachedWordSetServiceDecorator extends WordSetServiceDecorator {
    private final WordSetRepository wordSetRepository;

    public CachedWordSetServiceDecorator(WordSetRepository wordSetRepository, WordSetService wordSetService) {
        super(wordSetService);
        this.wordSetRepository = wordSetRepository;
    }

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        List<WordSet> wordSets = null;
        try {
            wordSets = super.getWordSets(topic);
        } catch (InternetConnectionLostException e) {
            return getWordSetsFromDB(topic);
        }
        if (!wordSets.isEmpty()) {
            super.saveWordSets(wordSets);
        }
        return getWordSetsFromDB(topic);
    }

    private List<WordSet> getWordSetsFromDB(Topic topic) {
        if (topic == null) {
            return wordSetRepository.findAll();
        } else {
            return wordSetRepository.findAllByTopicId(topic.getId());
        }
    }
}