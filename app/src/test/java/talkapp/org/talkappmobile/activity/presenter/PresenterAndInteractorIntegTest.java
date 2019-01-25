package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.powermock.reflect.Whitebox;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationInterceptor;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class PresenterAndInteractorIntegTest {

    private DataServer server;

    private AuthSign authSign;

    {
        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        authSign = new AuthSign();
        Whitebox.setInternalState(factory, "authSign", authSign);
        Whitebox.setInternalState(factory, "authorizationInterceptor", new AuthorizationInterceptor());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(provideLocalDataService());
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        server = factory.get();
    }

    private LocalDataService provideLocalDataService() {
        return new LocalDataServiceImpl(mock(WordSetDao.class), mock(TopicDao.class), provideSentenceDao(), mock(WordTranslationDao.class), new ObjectMapper(), new LoggerBean());
    }

    private SentenceDao provideSentenceDao() {
        return new SentenceDao() {

            private Set<SentenceMapping> storage = new HashSet<>();

            @Override
            public void save(List<SentenceMapping> mappings) {
                storage.addAll(mappings);
            }

            @Override
            public List<SentenceMapping> findAllByWord(String word, int wordsNumber) {
                List<SentenceMapping> result = new LinkedList<>();
                for (SentenceMapping mapping : storage) {
                    if (mapping.getTokens().contains(word) && mapping.getTokens().length() <= wordsNumber) {
                        result.add(mapping);
                    }
                }
                return result;
            }
        };
    }

    protected void login() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("sasha-ne@tut.by");
        credentials.setPassword("password0");
        String signature;
        try {
            signature = server.loginUser(credentials);
        } catch (LoginException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        authSign.put(signature);
    }

    public DataServer getServer() {
        return server;
    }

    protected WordSetExperienceDao provideWordSetExperienceDao() {
        return new WordSetExperienceDao() {
            private Set<WordSetExperienceMapping> storage = new HashSet<>();

            @Override
            public void createNewOrUpdate(WordSetExperienceMapping experience) {
                storage.add(experience);
            }

            @Override
            public WordSetExperienceMapping findById(int id) {
                for (WordSetExperienceMapping mapping : storage) {
                    if (mapping.getId() == id) {
                        return mapping;
                    }
                }
                return null;
            }
        };
    }

    protected PracticeWordSetExerciseDao providePracticeWordSetExerciseDao() {
        return new PracticeWordSetExerciseDao() {
            private Set<PracticeWordSetExerciseMapping> storage = new HashSet<>();

            @Override
            public List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, int wordSetId) {
                LinkedList<PracticeWordSetExerciseMapping> result = new LinkedList<>();
                for (PracticeWordSetExerciseMapping mapping : storage) {
                    if (mapping.getWordJSON().equals(word) && mapping.getWordSetId() == wordSetId) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public void createNewOrUpdate(PracticeWordSetExerciseMapping exercise) {
                storage.add(exercise);
            }

            @Override
            public void cleanByWordSetId(int wordSetId) {
                Set<PracticeWordSetExerciseMapping> old = storage;
                storage = new HashSet<>();
                for (PracticeWordSetExerciseMapping mapping : old) {
                    if (mapping.getWordSetId() == wordSetId) {
                        continue;
                    }
                    storage.add(mapping);
                }
            }

            @Override
            public int createAll(List<PracticeWordSetExerciseMapping> words) {
                storage.addAll(words);
                return 0;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findByStatusAndByWordSetId(WordSetExperienceStatus status, int wordSetId) {
                LinkedList<PracticeWordSetExerciseMapping> result = new LinkedList<>();
                for (PracticeWordSetExerciseMapping mapping : storage) {
                    if (mapping.getStatus() == status && mapping.getWordSetId() == wordSetId) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findByCurrentAndByWordSetId(int wordSetId) {
                LinkedList<PracticeWordSetExerciseMapping> result = new LinkedList<>();
                for (PracticeWordSetExerciseMapping mapping : storage) {
                    if (mapping.getWordSetId() == wordSetId && mapping.isCurrent()) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findFinishedWordSetsSortByUpdatedDate(long limit, Date olderThenInHours) {
                return null;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findByWordAndByStatus(String word, WordSetExperienceStatus status) {
                return null;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findByWordAndBySentenceAndByStatus(String s, String s1, WordSetExperienceStatus finished) {
                return null;
            }
        };
    }
}