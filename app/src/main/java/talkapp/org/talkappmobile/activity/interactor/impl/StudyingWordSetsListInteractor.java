package talkapp.org.talkappmobile.activity.interactor.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.model.NewWordSetDraftQRObject;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordAndTranslationQRObject;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

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
        List<WordSet> wordSets = wordSetService.getWordSets(topic);
        listener.onWordSetsInitialized(wordSets, null);
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

    @Override
    public void refreshWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets = wordSetService.getWordSets(topic);
        listener.onWordSetsFetched(wordSets, null);
    }

    @Override
    public void prepareWordSetDraftForQRCode(int wordSetId, OnWordSetsListListener listener) {
        if (wordSetId < wordSetService.getCustomWordSetsStartsSince()) {
            listener.onWordSetCantBeShared();
            return;
        }
        WordSet wordSet = wordSetService.findById(wordSetId);
        List<WordTranslation> wordTranslations = server.findWordTranslationsByWordSetIdAndByLanguage(wordSet.getId(), "russian");
        LinkedList<WordAndTranslationQRObject> qrObjects = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            qrObjects.add(new WordAndTranslationQRObject(wordTranslation.getWord(), wordTranslation.getTranslation()));
        }
        NewWordSetDraftQRObject draft = new NewWordSetDraftQRObject(qrObjects);
        listener.onWordSetDraftPrepare(draft);
    }
}