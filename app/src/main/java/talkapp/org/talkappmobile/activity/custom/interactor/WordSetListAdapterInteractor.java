package talkapp.org.talkappmobile.activity.custom.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetListAdapterListener;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class WordSetListAdapterInteractor {
    private final WordSetExperienceService experienceService;

    public WordSetListAdapterInteractor(WordSetExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    public void prepareModel(List<WordSet> wordSetList, OnWordSetListAdapterListener listener) {
        listener.onModelPrepared(wordSetList);
    }

    public WordSet getWordSet(List<WordSet> wordSetList, int position) {
        return wordSetList.get(position);
    }

    public WordSetExperience getWordSetExperience(List<WordSet> wordSetList, int position) {
        WordSet wordSet = wordSetList.get(position);
        return experienceService.findById(wordSet.getId());
    }
}