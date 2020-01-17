package talkapp.org.talkappmobile.activity.custom.interactor;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetsListItemViewInteractor {

    public WordSetsListItemViewInteractor() {
    }

    public void prepareModel(WordSet wordSet, OnWordSetsListItemViewListener listener) {
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Tokens : wordSet.getWords()) {
            words.add(word2Tokens.getWord());
        }
        String wordSetRowValue = StringUtils.joinWith(", ", words.toArray());

        listener.onModelPrepared(wordSetRowValue, wordSet.getTrainingExperienceInPercentages(), wordSet.getAvailableInHours());
    }
}