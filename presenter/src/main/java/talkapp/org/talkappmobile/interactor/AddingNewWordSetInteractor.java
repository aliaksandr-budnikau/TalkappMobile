package talkapp.org.talkappmobile.interactor;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.listener.OnAddingNewWordSetListener;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.NewWordWithTranslation;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;

public class AddingNewWordSetInteractor {
    private static final String RUSSIAN_LANGUAGE = "russian";
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private final DataServer dataServer;

    @Inject
    public AddingNewWordSetInteractor(WordSetService wordSetService, WordTranslationService wordTranslationService, DataServer dataServer) {
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
        this.dataServer = dataServer;
    }

    public void initialize(OnAddingNewWordSetListener listener) {
        NewWordSetDraft newWordSetDraft = wordSetService.getNewWordSetDraft();
        WordTranslation[] words = newWordSetDraft.getWordTranslations().toArray(new WordTranslation[0]);
        listener.onNewWordSetDraftLoaded(words);
    }

    public void submitNewWordSet(List<WordTranslation> words, OnAddingNewWordSetListener listener) {
        List<WordTranslation> normalizedWords = normalizeAll(words);
        if (isAnyEmpty(normalizedWords, listener)) {
            return;
        }
        List<WordTranslation> translations = new LinkedList<>();
        for (WordTranslation normalizedWord : normalizedWords) {
            WordTranslation result;
            if (StringUtils.isEmpty(normalizedWord.getTranslation())) {
                result = wordTranslationService.findWordTranslationsByWordAndByLanguage(RUSSIAN_LANGUAGE, normalizedWord.getWord());
                if (result == null) {
                    continue;
                }
            } else {
                result = wordTranslationService.findByWordAndLanguage(normalizedWord.getWord(), RUSSIAN_LANGUAGE);
            }
            translations.add(result);
        }
        if (words.size() != translations.size()) {
            return;
        }

        WordSet wordSet = wordSetService.createNewCustomWordSet(translations);
        listener.onNewWordSuccessfullySubmitted(wordSet);
    }

    private List<WordTranslation> normalizeAll(List<WordTranslation> inputs) {
        for (WordTranslation input : inputs) {
            if (StringUtils.isNotEmpty(input.getWord()) && StringUtils.isNotEmpty(input.getTranslation())) {
                input.setTranslation(input.getTranslation().trim());
                input.setWord(input.getWord().trim());
            } else {
                if (StringUtils.isEmpty(input.getWord())) {
                    input.setWord(null);
                } else {
                    input.setWord(input.getWord().trim().toLowerCase());
                }
                input.setTranslation(null);
            }
        }
        return inputs;
    }

    private boolean isAnyEmpty(List<WordTranslation> words, OnAddingNewWordSetListener listener) {
        for (int i = 0; i < words.size(); i++) {
            if (StringUtils.isEmpty(words.get(i).getWord())) {
                listener.onSomeWordIsEmpty();
                return true;
            }
        }
        return false;
    }

    public void saveChangedDraft(List<WordTranslation> vocabulary) {
        wordSetService.save(new NewWordSetDraft(vocabulary));
    }

    public void savePhraseTranslationInputOnPopup(String newPhrase, String newTranslation, OnAddingNewWordSetListener listener) {
        NewWordWithTranslation normalizedPhrase = new NewWordWithTranslation(newPhrase, newTranslation);

        WordTranslation result;
        if (StringUtils.isEmpty(normalizedPhrase.getTranslation())) {
            result = dataServer.findWordTranslationsByWordAndByLanguage(RUSSIAN_LANGUAGE, normalizedPhrase.getWord());
            if (result == null) {
                listener.onNewWordTranslationWasNotFound();
                return;
            }
            wordTranslationService.saveWordTranslations(asList(result));
        } else {
            wordTranslationService.saveWordTranslations(normalizedPhrase.getWord(), normalizedPhrase.getTranslation());
        }
        listener.onPhraseTranslationInputWasValidatedSuccessfully(newPhrase, newTranslation);
    }

}