package talkapp.org.talkappmobile.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.service.impl.RequestExecutor;

public interface ServiceFactory {
    RequestExecutor getRequestExecutor();

    WordSetService getWordSetExperienceRepository();

    WordRepetitionProgressService getWordRepetitionProgressService();

    UserExpService getUserExpService();

    TopicService getTopicService();

    WordTranslationService getWordTranslationService();

    ObjectMapper getMapper();

    CurrentPracticeStateService getCurrentPracticeStateService();

    SentenceService getSentenceService(DataServer server);

    DataServer getDataServer();

    SentenceProvider getSentenceProvider();
}