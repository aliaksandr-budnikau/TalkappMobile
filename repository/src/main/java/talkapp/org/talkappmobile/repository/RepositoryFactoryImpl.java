package talkapp.org.talkappmobile.repository;

import android.content.Context;

import javax.inject.Inject;

import talkapp.org.talkappmobile.DaggerRepositoryComponent;
import talkapp.org.talkappmobile.RepositoryComponent;
import talkapp.org.talkappmobile.RepositoryModule;

public class RepositoryFactoryImpl implements RepositoryFactory {
    @Inject
    WordRepetitionProgressRepository wordRepetitionProgressRepository;
    @Inject
    WordSetRepository wordSetRepository;
    @Inject
    WordTranslationRepository wordTranslationRepository;
    @Inject
    ExpAuditRepository expAuditRepository;
    @Inject
    SentenceRepository sentenceRepository;
    @Inject
    TopicRepository topicRepository;

    public RepositoryFactoryImpl(Context context) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .repositoryModule(new RepositoryModule(context)).build();
        component.inject(this);
    }

    @Override
    public WordRepetitionProgressRepository getWordRepetitionProgressRepository() {
        return wordRepetitionProgressRepository;
    }

    @Override
    public WordSetRepository getWordSetRepository() {
        return wordSetRepository;
    }

    @Override
    public WordTranslationRepository getWordTranslationRepository() {
        return wordTranslationRepository;
    }

    @Override
    public ExpAuditRepository getExpAuditRepository() {
        return expAuditRepository;
    }

    @Override
    public SentenceRepository getSentenceRepository() {
        return sentenceRepository;
    }

    @Override
    public TopicRepository getTopicRepository() {
        return topicRepository;
    }
}
