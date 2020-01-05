package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

public class CachedWordSetServiceDecorator extends WordSetServiceDecorator {
    public CachedWordSetServiceDecorator(WordSetService wordSetService) {
        super(wordSetService);
    }

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        try {
            return super.getWordSets(topic);
        } catch (LocalCacheIsEmptyException e) {
            findAllWordSets();
        }
        return super.getWordSets(topic);
    }

    @Override
    public List<WordSet> findAllWordSets() {
        List<WordSet> allWordSets = super.findAllWordSets();
        if (allWordSets == null) {
            return new LinkedList<>();
        }
        super.saveWordSets(allWordSets);
        return findAllWordSetsLocally();
    }
}