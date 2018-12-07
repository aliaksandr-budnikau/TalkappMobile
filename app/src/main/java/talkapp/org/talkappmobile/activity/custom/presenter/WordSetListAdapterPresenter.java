package talkapp.org.talkappmobile.activity.custom.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.custom.interactor.WordSetListAdapterInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetListAdapterListener;
import talkapp.org.talkappmobile.activity.custom.view.WordSetListAdapterView;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class WordSetListAdapterPresenter implements OnWordSetListAdapterListener {
    private final WordSetListAdapterView view;
    private final WordSetListAdapterInteractor interactor;
    private List<WordSet> wordSetList;

    public WordSetListAdapterPresenter(WordSetListAdapterInteractor interactor, WordSetListAdapterView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void setModel(List<WordSet> wordSetList) {
        this.wordSetList = wordSetList;
    }

    public void refreshModel() {
        interactor.prepareModel(wordSetList, this);
    }

    public WordSet getWordSet(int position) {
        return interactor.getWordSet(wordSetList, position);
    }

    public WordSetExperience getWordSetExperience(int position) {
        return interactor.getWordSetExperience(wordSetList, position);
    }

    @Override
    public void onModelPrepared(List<WordSet> wordSetList) {
        view.onModelPrepared(wordSetList);
    }
}