package talkapp.org.talkappmobile.service;

public interface ServiceFactory {
    EqualityScorer getEqualityScorer();

    Logger getLogger();

    WordSetService getWordSetService();

    WordRepetitionProgressService getWordRepetitionProgressService();

    UserExpService getUserExpService();

    TopicService getTopicService();

    WordTranslationService getWordTranslationService();

    CurrentPracticeStateService getCurrentPracticeStateService();

    SentenceService getSentenceService();

    RefereeService getRefereeService();

    DataServer getDataServer();

    SentenceProvider getSentenceProvider();

    TextUtils getTextUtils();
}