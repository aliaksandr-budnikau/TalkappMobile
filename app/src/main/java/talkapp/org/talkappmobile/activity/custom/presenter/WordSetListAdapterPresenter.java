package talkapp.org.talkappmobile.activity.custom.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.custom.interactor.WordSetListAdapterInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetListAdapterListener;
import talkapp.org.talkappmobile.activity.custom.view.WordSetListAdapterView;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetListAdapterPresenter implements OnWordSetListAdapterListener {
    private final WordSetListAdapterView view;
    private final WordSetListAdapterInteractor interactor;
    private List<WordSet> wordSetList;
    private List<WordSet> filteredList;

    public WordSetListAdapterPresenter(WordSetListAdapterInteractor interactor, WordSetListAdapterView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void refreshModel() {
        interactor.prepareModel(wordSetList, this);
    }

    public WordSet getWordSet(int position) {
        return interactor.getWordSet(filteredList, position);
    }

    public WordSet getWordSetExperience(int position) {
        return wordSetList.get(position);
    }

    @Override
    public void onModelPrepared(List<WordSet> wordSetList) {
        filteredList = wordSetList;
        view.onModelPrepared(wordSetList);
    }

    public List<WordSet> getModel() {
        return wordSetList;
    }

    public void setModel(List<WordSet> wordSetList) {
        this.wordSetList = wordSetList;
    }

    public void filterNew() {
        interactor.filterNew(wordSetList, this);
    }

    public void filterStarted() {
        interactor.filterStarted(wordSetList, this);
    }

    public void filterFinished() {
        interactor.filterFinished(wordSetList, this);
    }
}