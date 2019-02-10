package talkapp.org.talkappmobile.activity.presenter;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.powermock.reflect.Whitebox;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.ExpAuditDao;
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
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class PresenterAndInteractorIntegTest {

    private DataServer server;

    {
        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(provideLocalDataService());
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        server = factory.get();
    }

    private LocalDataService provideLocalDataService() {
        return new LocalDataServiceImpl(provideWordSetDao(), mock(TopicDao.class), provideSentenceDao(), mock(WordTranslationDao.class), mock(ExpAuditDao.class), new ObjectMapper(), new LoggerBean());
    }

    private WordSetDao provideWordSetDao() {
        return new WordSetDao() {

            private Map<String, List<WordSetMapping>> wordSets = new HashMap<>();

            @Override
            public List<WordSetMapping> findAll() {
                return getAllWordSets(wordSets);
            }

            @Override
            public void save(List<WordSetMapping> mappings) {
                wordSets = splitAllWortSetsByTopicId(mappings);
            }

            @Override
            public List<WordSetMapping> findAllByTopicId(String topicId) {
                return wordSets.get(topicId) == null ? new LinkedList<WordSetMapping>() : wordSets.get(topicId);
            }

            @NonNull
            private List<WordSetMapping> getAllWordSets(Map<String, List<WordSetMapping>> all) {
                LinkedList<WordSetMapping> result = new LinkedList<>();
                for (List<WordSetMapping> wordSets : all.values()) {
                    result.addAll(wordSets);
                }
                return result;
            }

            private Map<String, List<WordSetMapping>> splitAllWortSetsByTopicId(List<WordSetMapping> incomminMapping) {
                Map<String, List<WordSetMapping>> result = new HashMap<>();
                for (WordSetMapping mapping : incomminMapping) {
                    List<WordSetMapping> wordSetList = result.get(mapping.getTopicId());
                    if (wordSetList == null) {
                        wordSetList = new LinkedList<>();
                        result.put(mapping.getTopicId(), wordSetList);
                    }
                    wordSetList.add(mapping);
                }
                return result;
            }
        };
    }

    private SentenceDao provideSentenceDao() {
        return new SentenceDao() {

            private Map<String, List<SentenceMapping>> sentences = new HashMap<>();

            @Override
            public void save(List<SentenceMapping> mappings) {
                for (SentenceMapping mapping : mappings) {
                    String[] ids = mapping.getId().split("#");
                    List<SentenceMapping> list = sentences.get(getKey(ids[1], Integer.valueOf(ids[2])));
                    if (list != null && !list.isEmpty()) {
                        continue;
                    } else {
                        sentences.put(getKey(ids[1], Integer.valueOf(ids[2])), new LinkedList<SentenceMapping>());
                    }
                    sentences.get(getKey(ids[1], Integer.valueOf(ids[2]))).add(mapping);
                }
            }

            @Override
            public List<SentenceMapping> findAllByWord(String word, int wordsNumber) {
                List<SentenceMapping> mappings = sentences.get(getKey(word, wordsNumber));
                return mappings == null ? new LinkedList<SentenceMapping>() : mappings;
            }

            private String getKey(String word, int wordsNumber) {
                return word + "_" + wordsNumber;
            }
        };
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