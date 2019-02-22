package talkapp.org.talkappmobile.activity.custom.interactor;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetsListItemViewInteractor {
    private final WordSetExperienceUtils experienceUtils;
    private final WordSetService wordSetService;

    public WordSetsListItemViewInteractor(WordSetExperienceUtils experienceUtils, WordSetService wordSetService) {
        this.experienceUtils = experienceUtils;
        this.wordSetService = wordSetService;
    }

    public void prepareModel(WordSet wordSet, OnWordSetsListItemViewListener listener) {
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Tokens : wordSet.getWords()) {
            words.add(word2Tokens.getWord());
        }
        String wordSetRowValue = StringUtils.joinWith(", ", words.toArray());

        int progressValue = experienceUtils.getProgress(wordSet.getTrainingExperience(), wordSetService.getMaxTrainingProgress(wordSet));
        listener.onModelPrepared(wordSetRowValue, progressValue);
    }
}