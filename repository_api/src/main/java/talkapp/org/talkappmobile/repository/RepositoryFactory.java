package talkapp.org.talkappmobile.repository;

public interface RepositoryFactory {
    WordRepetitionProgressRepository getWordRepetitionProgressRepository();

    WordSetRepository getWordSetRepository();

    WordTranslationRepository getWordTranslationRepository();

    ExpAuditRepository getExpAuditRepository();

    SentenceRepository getSentenceRepository();

    TopicRepository getTopicRepository();
}
