package talkapp.org.talkappmobile.activity.interactor;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;

public class PracticeWordSetVocabularyInteractor {
    private final DataServer server;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private final CurrentPracticeStateService currentPracticeStateService;
    private final WordRepetitionProgressService wordRepetitionProgressService;

    public PracticeWordSetVocabularyInteractor(DataServer server, WordSetService wordSetService, WordTranslationService wordTranslationService, WordRepetitionProgressService wordRepetitionProgressService, CurrentPracticeStateService currentPracticeStateService) {
        this.server = server;
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
            return server.findWordTranslationsByWordsAndByLanguage(getWords(wordSet), "russian");
        } else {
            return server.findWordTranslationsByWordSetIdAndByLanguage(wordSet.getId(), "russian");
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
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        Word2Tokens oldWord2Token = currentPracticeState.getWordSet().getWords().get(editedItemPosition);
        if (oldWord2Token.getSourceWordSetId() < wordSetService.getCustomWordSetsStartsSince()) {
            listener.onUpdateNotCustomWordSet();
            return;
        }
        Word2Tokens newWord2Token = new Word2Tokens(wordTranslation.getWord(), wordTranslation.getWord(), oldWord2Token.getSourceWordSetId());
        wordSetService.updateWord2Tokens(newWord2Token, oldWord2Token);
        wordTranslationService.saveWordTranslations(asList(wordTranslation));
        wordRepetitionProgressService.updateSentenceIds(newWord2Token, oldWord2Token);

        listener.onUpdateCustomWordSetFinished();
    }
}