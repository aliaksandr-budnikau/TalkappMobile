package talkapp.org.talkappmobile.activity.interactor.impl;

import java.util.Collections;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

import static org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

public class StudyingWordSetsListInteractor implements WordSetsListInteractor {
    private final DataServer server;
    private final WordSetService wordSetService;
    private final WordRepetitionProgressService exerciseService;

    public StudyingWordSetsListInteractor(DataServer server, WordSetService wordSetService, WordRepetitionProgressService exerciseService) {
        this.server = server;
        this.wordSetService = wordSetService;
        this.exerciseService = exerciseService;
    }

    @Override
    public void initializeWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets;
        if (topic == null) {
            wordSets = server.findAllWordSets();
        } else {
            try {
                wordSets = server.findWordSetsByTopicId(topic.getId());
            } catch (LocalCacheIsEmptyException e) {
                initLocalCache();
                wordSets = server.findWordSetsByTopicId(topic.getId());
            }
        }
        Collections.sort(wordSets, new WordSetComparator());
        listener.onWordSetsInitialized(wordSets);
    }

    private void initLocalCache() {
        server.findAllWordSets();
    }

    @Override
    public void itemClick(Topic topic, WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        if (FINISHED.equals(wordSet.getStatus())) {
            listener.onWordSetFinished(wordSet, clickedItemNumber);
        } else {
            listener.onWordSetNotFinished(topic, wordSet);
        }
    }

    @Override
    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        exerciseService.cleanByWordSetId(wordSet.getId());
        wordSetService.resetProgress(wordSet);
        wordSet.setTrainingExperience(0);
        wordSet.setStatus(FIRST_CYCLE);
        listener.onResetExperienceClick(wordSet, clickedItemNumber);
    }

    @Override
    public void deleteWordSetClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        exerciseService.cleanByWordSetId(wordSet.getId());
        if (wordSet.getId() >= wordSetService.getCustomWordSetsStartsSince()) {
            wordSetService.remove(wordSet);
            listener.onWordSetRemoved(wordSet, clickedItemNumber);
        } else {
            listener.onWordSetNotRemoved(wordSet, clickedItemNumber);
        }
    }

    @Override
    public void itemLongClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        listener.onItemLongClick(wordSet, clickedItemNumber);
    }
}