package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.model.NewWordSetDraftQRObject;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetsListPresenter implements OnWordSetsListListener {
    private final Topic topic;
    private final WordSetsListView view;
    private final WordSetsListInteractor interactor;

    public WordSetsListPresenter(Topic topic, WordSetsListView view, WordSetsListInteractor interactor) {
        this.topic = topic;
        this.view = view;
        this.interactor = interactor;
    }

    public void initialize() {
        wrapWithTryFinally(new Runnable() {
            @Override
            public void run() {
                interactor.initializeWordSets(topic, WordSetsListPresenter.this);
            }
        });
    }

    private void wrapWithTryFinally(Runnable runnable) {
        try {
            view.onInitializeBeginning();
            runnable.run();
        } finally {
            view.onInitializeEnd();
        }
    }

    @Override
    public void onWordSetsInitialized(List<WordSet> wordSets, RepetitionClass repetitionClass) {
        view.onWordSetsInitialized(wordSets, repetitionClass);
    }

    @Override
    public void onWordSetFinished(WordSet wordSet, int clickedItemNumber) {
        view.onWordSetFinished(wordSet, clickedItemNumber);
    }

    @Override
    public void onResetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        view.onResetExperienceClick(wordSet, clickedItemNumber);
    }

    @Override
    public void onWordSetNotFinished(Topic topic, WordSet wordSet) {
        view.onWordSetNotFinished(topic, wordSet);
    }

    @Override
    public void onWordSetRemoved(WordSet wordSet, int clickedItemNumber) {
        view.onWordSetRemoved(wordSet, clickedItemNumber);
    }

    @Override
    public void onWordSetNotRemoved(WordSet wordSet, int clickedItemNumber) {
        view.onWordSetNotRemoved();
    }

    public void itemClick(WordSet wordSet, int clickedItemNumber) {
        interactor.itemClick(topic, wordSet, clickedItemNumber, this);
    }

    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        interactor.resetExperienceClick(wordSet, clickedItemNumber, this);
    }

    public void itemLongClick(WordSet wordSet, int clickedItemNumber) {
        interactor.itemLongClick(wordSet, clickedItemNumber, this);
    }

    @Override
    public void onItemLongClick(WordSet wordSet, int clickedItemNumber) {
        view.onItemLongClick(wordSet, clickedItemNumber);
    }

    @Override
    public void onWordSetTooSmallForRepetition(int maxWordSetSize, int actualSize) {
        view.onWordSetTooSmallForRepetition(maxWordSetSize, actualSize);
    }

    @Override
    public void onWordSetsFetched(List<WordSet> wordSets, RepetitionClass repetitionClass) {
        view.onWordSetsRefreshed(wordSets, repetitionClass);
    }

    @Override
    public void onWordSetDraftPrepare(NewWordSetDraftQRObject qrObject) {
        view.onWordSetDraftPrepare(qrObject);
    }

    @Override
    public void onWordSetCantBeShared() {
        view.onWordSetCantBeShared();
    }

    @Override
    public void onWordSetIsNotAvailableYet(int availableInHours) {
        view.onWordSetIsNotAvailableYet(availableInHours);
    }

    public void deleteWordSetClick(WordSet wordSet, int clickedItemNumber) {
        interactor.deleteWordSetClick(wordSet, clickedItemNumber, this);
    }

    public void refresh() {
        wrapWithTryFinally(new Runnable() {
            @Override
            public void run() {
                interactor.refreshWordSets(topic, WordSetsListPresenter.this);
            }
        });
    }

    public void prepareWordSetDraftForQRCode(int wordSetId) {
        interactor.prepareWordSetDraftForQRCode(wordSetId, this);
    }
}