package talkapp.org.talkappmobile.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.repository.ExpAuditRepository;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.repository.impl.ExpAuditMapper;

public interface ServiceFactory {
    RequestExecutor getRequestExecutor();

    WordSetService getWordSetExperienceRepository();

    MigrationService getMigrationService();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    WordRepetitionProgressRepository getWordRepetitionProgressRepository();

    UserExpService getUserExpService();

    TopicService getTopicService();

    WordTranslationService getWordTranslationService();

    ExpAuditMapper getExpAuditMapper();

    ObjectMapper getMapper();

    CurrentPracticeStateService getCurrentPracticeStateService();

    SentenceService getSentenceService(DataServer server);

    DataServer getDataServer();

    SentenceProvider getSentenceProvider();

    WordSetRepository getWordSetRepository();

    WordTranslationRepository getWordTranslationRepository();

    ExpAuditRepository getExpAuditRepository();

    SentenceRepository getSentenceRepository();
}