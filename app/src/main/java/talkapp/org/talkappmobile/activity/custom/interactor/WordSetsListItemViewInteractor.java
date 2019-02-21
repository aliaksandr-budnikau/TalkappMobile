package talkapp.org.talkappmobile.activity.custom.interactor;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class WordSetsListItemViewInteractor {
    private final WordSetExperienceUtils experienceUtils;

    public WordSetsListItemViewInteractor(WordSetExperienceUtils experienceUtils) {
        this.experienceUtils = experienceUtils;
    }

    public void prepareModel(WordSet wordSet, WordSetExperience experience, OnWordSetsListItemViewListener listener) {
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Tokens : wordSet.getWords()) {
            words.add(word2Tokens.getWord());
        }
        String wordSetRowValue = StringUtils.joinWith(", ", words.toArray());

        int progressValue = 0;
        if (experience != null) {
            progressValue = experienceUtils.getProgress(experience, wordSet);
        }
        listener.onModelPrepared(wordSetRowValue, progressValue);
    }
}