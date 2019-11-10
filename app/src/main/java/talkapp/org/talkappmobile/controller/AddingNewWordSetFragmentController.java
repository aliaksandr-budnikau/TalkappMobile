package talkapp.org.talkappmobile.controller;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.events.AddNewWordSetButtonSubmitClickedEM;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentGotReadyEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.SomeWordIsEmptyEM;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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
        wordSetService.save(new NewWordSetDraft(event.getWordTranslations()));
    }

    public void handle(AddNewWordSetButtonSubmitClickedEM event) {
        List<WordTranslation> words = event.getWordTranslations();
        List<WordTranslation> normalizedWords = normalizeAll(words);
        if (isAnyEmpty(normalizedWords)) {
            return;
        }
        List<WordTranslation> translations = new LinkedList<>();
        for (WordTranslation normalizedWord : normalizedWords) {
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

    private boolean isAnyEmpty(List<WordTranslation> words) {
        for (int i = 0; i < words.size(); i++) {
            if (isEmpty(words.get(i).getWord())) {
                eventBus.post(new SomeWordIsEmptyEM());
                return true;
            }
        }
        return false;
    }

    private List<WordTranslation> normalizeAll(List<WordTranslation> inputs) {
        for (WordTranslation input : inputs) {
            if (isNotEmpty(input.getWord()) && isNotEmpty(input.getTranslation())) {
                input.setTranslation(input.getTranslation().trim());
                input.setWord(input.getWord().trim());
            } else {
                if (isEmpty(input.getWord())) {
                    input.setWord(null);
                } else {
                    input.setWord(input.getWord().trim().toLowerCase());
                }
                input.setTranslation(null);
            }
        }
        return inputs;
    }
}