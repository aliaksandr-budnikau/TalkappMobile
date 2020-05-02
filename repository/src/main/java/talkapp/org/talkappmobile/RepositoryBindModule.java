package talkapp.org.talkappmobile;

import dagger.Binds;
import dagger.Module;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.dao.impl.NewWordSetDraftDaoImpl;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.TopicDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordTranslationDaoImpl;
import talkapp.org.talkappmobile.repository.ExpAuditRepository;
import talkapp.org.talkappmobile.repository.ExpAuditRepositoryImpl;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.repository.SentenceRepositoryImpl;
import talkapp.org.talkappmobile.repository.TopicRepository;
import talkapp.org.talkappmobile.repository.TopicRepositoryImpl;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepositoryImpl;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.repository.WordSetRepositoryImpl;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;
import talkapp.org.talkappmobile.repository.WordTranslationRepositoryImpl;

@Module
public abstract class RepositoryBindModule {

    @Binds
    abstract ExpAuditRepository bindExpAuditRepository(ExpAuditRepositoryImpl target);

    @Binds
    abstract SentenceRepository bindSentenceRepository(SentenceRepositoryImpl target);

    @Binds
    abstract WordRepetitionProgressRepository bindWordRepetitionProgressRepository(WordRepetitionProgressRepositoryImpl target);

    @Binds
    abstract WordSetRepository bindWordSetRepository(WordSetRepositoryImpl target);

    @Binds
    abstract WordTranslationRepository bindWordTranslationRepository(WordTranslationRepositoryImpl target);

    @Binds
    abstract TopicRepository bindTopicRepository(TopicRepositoryImpl target);

    @Binds
    abstract WordTranslationDao bindWordTranslationDao(WordTranslationDaoImpl target);

    @Binds
    abstract SentenceDao bindSentenceDao(SentenceDaoImpl target);

    @Binds
    abstract WordSetDao bindWordSetDao(WordSetDaoImpl target);

    @Binds
    abstract WordRepetitionProgressDao bindWordRepetitionProgressDao(WordRepetitionProgressDaoImpl target);

    @Binds
    abstract TopicDao bindTopicDao(TopicDaoImpl target);

    @Binds
    abstract NewWordSetDraftDao bindNewWordSetDraftDao(NewWordSetDraftDaoImpl target);

    @Binds
    abstract ExpAuditDao bindExpAuditDao(ExpAuditDaoImpl target);
}