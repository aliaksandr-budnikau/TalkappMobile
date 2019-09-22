package talkapp.org.talkappmobile.controller;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.events.AddNewWordSetButtonSubmitClickedEM;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentGotReadyEM;
import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.SomeWordIsEmptyEM;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.NewWordWithTranslation;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AddingNewWordSetFragmentController {

    private static final int WORDS_NUMBER = 6;
    private static final String RUSSIAN_LANGUAGE = "russian";
    private final EventBus eventBus;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private final DataServer server;

    public AddingNewWordSetFragmentController(@NonNull EventBus eventBus, @NonNull DataServer server, @NonNull ServiceFactory factory) {
        this.eventBus = eventBus;
        this.server = server;
        this.wordTranslationService = factory.getWordTranslationService();
        this.wordSetService = factory.getWordSetExperienceRepository();
    }

    public void handle(AddingNewWordSetFragmentGotReadyEM event) {
        NewWordSetDraft newWordSetDraft = wordSetService.getNewWordSetDraft();
        eventBus.post(new NewWordSetDraftLoadedEM(newWordSetDraft));
    }

    public void handle(NewWordSetDraftWasChangedEM event) {
        wordSetService.save(new NewWordSetDraft(event.getWords()));
    }

    public void handle(AddNewWordSetButtonSubmitClickedEM event) {
        List<String> words = event.getWords();
        List<NewWordWithTranslation> normalizedWords = normalizeAll(words);
        if (isAnyEmpty(normalizedWords)) {
            return;
        }
        if (hasDuplicates(normalizedWords)) {
            return;
        }

        List<WordTranslation> translations = new LinkedList<>();
        for (NewWordWithTranslation normalizedWord : normalizedWords) {
            WordTranslation result;
            if (isEmpty(normalizedWord.getTranslation())) {
                result = server.findWordTranslationsByWordAndByLanguage(RUSSIAN_LANGUAGE, normalizedWord.getWord());
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
        eventBus.post(new NewWordSuccessfullySubmittedEM(wordSet));
    }

    private boolean hasDuplicates(List<NewWordWithTranslation> words) {
        boolean hasDuplicates = false;
        for (int i = 0; i < words.size(); i++) {
            NewWordWithTranslation word = words.get(i);
            if (words.subList(0, i).contains(word)) {
                hasDuplicates = true;
                eventBus.post(new NewWordIsDuplicateEM(i));
            }
        }
        return hasDuplicates;
    }

    private boolean isAnyEmpty(List<NewWordWithTranslation> words) {
        for (int i = 0; i < words.size(); i++) {
            if (isEmpty(words.get(i).getWord())) {
                eventBus.post(new SomeWordIsEmptyEM());
                return true;
            }
        }
        return false;
    }

    private List<NewWordWithTranslation> normalizeAll(List<String> inputs) {
        LinkedList<NewWordWithTranslation> words = new LinkedList<>();
        for (String input : inputs) {
            String[] wordAndTranslation = input.split("\\|");
            String word, translation;
            if (wordAndTranslation.length == 2) {
                word = wordAndTranslation[0].trim();
                translation = wordAndTranslation[1].trim();
            } else if (wordAndTranslation.length == 1) {
                word = wordAndTranslation[0].trim().toLowerCase();
                translation = null;
            } else {
                word = input.trim().toLowerCase();
                translation = null;
            }
            words.add(new NewWordWithTranslation(word, translation));
        }
        return words;
    }
}