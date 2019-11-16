package talkapp.org.talkappmobile.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.model.NewWordWithTranslation;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.AddingEditingNewWordSetsService;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;

public class AddingEditingNewWordSetsServiceImpl implements AddingEditingNewWordSetsService {
    public static final String RUSSIAN_LANGUAGE = "russian";
    private final DataServer server;
    private final EventBus eventBus;
    private final WordTranslationService wordTranslationService;

    public AddingEditingNewWordSetsServiceImpl(EventBus eventBus, DataServer server, WordTranslationService wordTranslationService) {
        this.eventBus = eventBus;
        this.server = server;
        this.wordTranslationService = wordTranslationService;
    }

    @Override
    public void saveNewWordTranslation(String phrase, String translation) {
        NewWordWithTranslation normalizedPhrase = new NewWordWithTranslation(phrase, translation);

        WordTranslation result;
        if (StringUtils.isEmpty(normalizedPhrase.getTranslation())) {
            result = server.findWordTranslationsByWordAndByLanguage(RUSSIAN_LANGUAGE, normalizedPhrase.getWord());
            if (result == null) {
                eventBus.post(new NewWordTranslationWasNotFoundEM());
                return;
            }
            wordTranslationService.saveWordTranslations(asList(result));
        } else {
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
            wordTranslation.setTranslation(normalizedPhrase.getTranslation());
            wordTranslation.setWord(normalizedPhrase.getWord());
            wordTranslation.setTokens(normalizedPhrase.getWord());
            wordTranslationService.saveWordTranslations(asList(wordTranslation));
        }
        eventBus.post(new PhraseTranslationInputWasValidatedSuccessfullyEM(phrase, translation));
    }
}