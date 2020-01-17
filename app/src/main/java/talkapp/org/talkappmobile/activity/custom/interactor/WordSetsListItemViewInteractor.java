package talkapp.org.talkappmobile.activity.custom.interactor;

import org.apache.commons.lang3.StringUtils;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;

public class WordSetsListItemViewInteractor {
    private final WordSetExperienceUtils experienceUtils;

    public WordSetsListItemViewInteractor(WordSetExperienceUtils experienceUtils) {
        this.experienceUtils = experienceUtils;
    }

    public void prepareModel(WordSet wordSet, OnWordSetsListItemViewListener listener) {
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Tokens : wordSet.getWords()) {
            words.add(word2Tokens.getWord());
        }
        String wordSetRowValue = StringUtils.joinWith(", ", words.toArray());

        int progressValue = experienceUtils.getProgress(wordSet.getTrainingExperience(), wordSet.getMaxTrainingExperience());
        listener.onModelPrepared(wordSetRowValue, progressValue, wordSet.getAvailableInHours());
    }
}