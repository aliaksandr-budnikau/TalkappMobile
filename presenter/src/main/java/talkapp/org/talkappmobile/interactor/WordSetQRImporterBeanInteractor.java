package talkapp.org.talkappmobile.interactor;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

public class WordSetQRImporterBeanInteractor {
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;

    public WordSetQRImporterBeanInteractor(WordSetService wordSetService, WordTranslationService wordTranslationService) {
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
    }

    public void saveWordSetDraft(NewWordSetDraft wordSetDraft) {
        wordSetService.save(wordSetDraft);
        wordTranslationService.saveWordTranslations(wordSetDraft);
    }
}
