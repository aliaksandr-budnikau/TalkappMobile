package talkapp.org.talkappmobile.dao;

import talkapp.org.talkappmobile.repository.ExpAuditRepository;
import talkapp.org.talkappmobile.repository.MigrationService;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.repository.TopicRepository;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;

public interface RepositoryFactory {
    WordRepetitionProgressRepository getWordRepetitionProgressRepository();

    WordSetRepository getWordSetRepository();

    WordTranslationRepository getWordTranslationRepository();

    ExpAuditRepository getExpAuditRepository();

    SentenceRepository getSentenceRepository();

    @Deprecated
    MigrationService getMigrationService();

    TopicRepository getTopicRepository();
}
