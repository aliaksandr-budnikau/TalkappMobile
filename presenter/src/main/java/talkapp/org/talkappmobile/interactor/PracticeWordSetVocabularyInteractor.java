package talkapp.org.talkappmobile.interactor;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;

public class PracticeWordSetVocabularyInteractor {
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private final CurrentPracticeStateService currentPracticeStateService;
    private final WordRepetitionProgressService wordRepetitionProgressService;

    @Inject
    public PracticeWordSetVocabularyInteractor(WordSetService wordSetService, WordTranslationService wordTranslationService, WordRepetitionProgressService wordRepetitionProgressService, CurrentPracticeStateService currentPracticeStateService) {
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
        this.currentPracticeStateService = currentPracticeStateService;
        this.wordRepetitionProgressService = wordRepetitionProgressService;
    }

    public void initialiseVocabulary(WordSet wordSet, OnPracticeWordSetVocabularyListener listener) {
        List<WordTranslation> wordTranslations = getWordTranslations(wordSet);
        listener.onWordSetVocabularyFound(wordTranslations);
    }

    private List<WordTranslation> getWordTranslations(WordSet wordSet) {
        if (wordSet.getId() == 0) {
            return wordTranslationService.findWordTranslationsByWordsAndByLanguage(getWords(wordSet), "russian");
        } else {
            return wordTranslationService.findWordTranslationsByWordSetIdAndByLanguage(wordSet.getId(), "russian");
        }
    }

    @NonNull
    private LinkedList<String> getWords(WordSet wordSet) {
        List<Word2Tokens> word2Tokens = wordSet.getWords();
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Token : word2Tokens) {
            words.add(word2Token.getWord());
        }
        return words;
    }

    public void updateCustomWordSet(int editedItemPosition, WordTranslation wordTranslation, OnPracticeWordSetVocabularyListener listener) {
        Word2Tokens oldWord2Token = currentPracticeStateService.getAllWords().get(editedItemPosition);
        if (oldWord2Token.getSourceWordSetId() >= 0 && oldWord2Token.getSourceWordSetId() < wordSetService.getCustomWordSetsStartsSince()) {
            listener.onUpdateNotCustomWordSet();
            return;
        }
        Word2Tokens newWord2Token = new Word2Tokens(wordTranslation.getWord(), wordTranslation.getWord(), oldWord2Token.getSourceWordSetId());
        wordSetService.updateWord2Tokens(newWord2Token, oldWord2Token);
        int index = currentPracticeStateService.getWordSet().getWords().indexOf(oldWord2Token);
        currentPracticeStateService.getWordSet().getWords().set(index, newWord2Token);
        wordTranslationService.saveWordTranslations(asList(wordTranslation));
        wordRepetitionProgressService.updateSentenceIds(newWord2Token, oldWord2Token);

        listener.onUpdateCustomWordSetFinished();
    }
}