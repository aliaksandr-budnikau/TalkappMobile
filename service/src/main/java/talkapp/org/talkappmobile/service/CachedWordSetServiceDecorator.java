package talkapp.org.talkappmobile.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.repository.WordSetRepository;

@RequiredArgsConstructor
public class CachedWordSetServiceDecorator implements WordSetService {
    private final WordSetRepository wordSetRepository;
    @Delegate(excludes = ExcludedMethods.class)
    private final WordSetService service;

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        List<WordSet> wordSets = null;
        try {
            wordSets = service.getWordSets(topic);
        } catch (InternetConnectionLostException e) {
            return getWordSetsFromDB(topic);
        }
        if (!wordSets.isEmpty()) {
            service.saveWordSets(wordSets);
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

    private interface ExcludedMethods {
        List<WordSet> getWordSets(Topic topic);
    }
}