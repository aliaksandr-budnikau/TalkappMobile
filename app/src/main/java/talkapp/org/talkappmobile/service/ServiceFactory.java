package talkapp.org.talkappmobile.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;

public interface ServiceFactory {
    RequestExecutor getRequestExecutor();

    WordSetService getWordSetExperienceRepository();

    MigrationService getMigrationService();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    TopicService getTopicService();

    WordTranslationService getWordTranslationService();

    ExpAuditMapper getExpAuditMapper();

    ObjectMapper getMapper();

    CurrentPracticeStateService getCurrentPracticeStateService();

    SentenceService getSentenceService(DataServer server);

    DataServer getDataServer();
}