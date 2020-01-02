package talkapp.org.talkappmobile.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;

public interface ServiceFactory {
    WordSetService getWordSetExperienceRepository();

    MigrationService getMigrationService();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    LocalDataService getLocalDataService();

    WordTranslationService getWordTranslationService();

    ExpAuditMapper getExpAuditMapper();

    ObjectMapper getMapper();

    CurrentPracticeStateService getCurrentPracticeStateService();

    SentenceService getSentenceService(DataServer server);
}