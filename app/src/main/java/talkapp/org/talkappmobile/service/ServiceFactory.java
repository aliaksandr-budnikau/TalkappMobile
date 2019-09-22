package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

public interface ServiceFactory {
    WordSetService getWordSetExperienceRepository();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    LocalDataService getLocalDataService();

    WordTranslationService getWordTranslationService();

    WordSetMapper getWordSetMapper();

    WordTranslationMapper getWordTranslationMapper();

    ExpAuditMapper getExpAuditMapper();
}